package com.lazylite.media.remote.core.play;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.down.DownloadTask;
import com.lazylite.media.remote.core.strategies.TingShuPlayStrategy;
import com.lazylite.mod.http.mgr.HttpWrapper;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.IRequestInfo;
import com.lazylite.mod.http.mgr.model.IResponseInfo;
import com.lazylite.mod.http.mgr.model.RequestInfo;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.receiver.network.NetworkStateUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.kuwo.p2p.Sign;


//by haiping
public final class AntiStealing {
	private String TAG				= "AntiStealing";

	private static final long	CACHE_TIME		= 3 * 60 * 1000;
	private static final int	MAX_RETRY_TIMES	= 3; //共4次，两次http，两次tcp

	private AntiStealingDelegate				delegate;
	private int								currentRequestVersion;
	private HttpWrapper httpWrapper;
	private String urlString;
	private int								retryTimes;
	private static int							nextRequestVersion	= 100;
	private static ThreadMessageHandler		threadHandler;
	private static HashMap<String, CacheItem> cacheItems			= new HashMap<>();

	public static void init(final ThreadMessageHandler handler) {
		threadHandler = handler;
	}

	public AntiStealing(final AntiStealingDelegate d, final String TAG) {
		if (!TextUtils.isEmpty(TAG)) {
			this.TAG = TAG + "_" + this.TAG;
		}
		delegate = d;

	}

	public static final class AntiStealingResult {
		public String format;
		public String url;
		public String sig;
		public int		bitrate;
		String quality;
		boolean  openp2p = true;

		public Sign getSign(){
			if(null == sig || sig.length() <= 0){
				return null;
			}
			try{
				//无符号64位取出高32位和低32位
				//由于java不支持无符号64位，不得不分段处理。鄙视java的这个设计。//嗯，相当鄙视
				long low = Long.parseLong(sig.substring(sig.length()-1));
				long l = Long.parseLong(sig.substring(0, sig.length() - 1));
				long lowbit = low % 2;
				l  = (l / 2) * 10 + (l %2 )* 5 + low / 2;
				long s1 = l >> 31;
				long s2 = (l & 0x7fffffff) * 2 + lowbit;
				return new Sign(s1, s2);
			}catch(NumberFormatException e){
				return null;
			}
		}
	}

	public interface AntiStealingDelegate {
		void onAntiStealingFinished(final AntiStealingResult result, boolean success);
	}

	public boolean isRunning() {
		return currentRequestVersion!=0;
	}

	public void request(final DownloadTask task, final String oldSig) {
		cancel();
		currentRequestVersion = nextRequestVersion++;
		Log.i(TAG, "request" + currentRequestVersion);
		retryTimes = 0;
		urlString = task.downloadStrategy.makeUrl(task.audioInfo);
		final CacheItem item = getCacheUrl(urlString);
		if (item != null && System.currentTimeMillis() - item.createTime < CACHE_TIME) {
			Log.i(TAG, "use cache" + currentRequestVersion);
			MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(),
					new MessageManager.Runner(currentRequestVersion) {
						@Override
						public void call() {
							if (callVersion == currentRequestVersion) {
								onSuccess(item.result);
							} else {
								Log.i(TAG, "canled" + callVersion);
							}
						}
					});
		} else {
			sendHTTPRequest();
		}
        removeOutOfTimeCache();
	}

	public void cancel() {
		currentRequestVersion = 0;
		if (httpWrapper != null) {
			httpWrapper.cancel();
			httpWrapper = null;
		}
	}

	private void sendHTTPRequest() {
		Log.i(TAG, "sendHTTPRequest" + currentRequestVersion);
		IRequestInfo iRequestInfo = new RequestInfo(urlString, null, null, threadHandler.getHandler());
		httpWrapper = KwHttpMgr.getInstance().getKwHttpFetch().asyncGet(iRequestInfo, new IKwHttpFetcher.FetchCallback() {
			@Override
			public void onFetch(@NonNull IResponseInfo responseInfo) {
				httpWrapper = null;
				if (responseInfo.isSuccessful()) {
					String resultStr;
					try {
						resultStr = responseInfo.dataToString();
					} catch (Exception e) {
						onFailed();
						return;
					}
					AntiStealingResult result = parse(resultStr);
					if (result == null) {
						onFailed();
					} else {
						addCache(urlString, result);
						onSuccess(result);
					}
				} else {
					onFailed();
				}
			}
		});
	}


	private AntiStealingResult parse(final String resultStr) {

        if(TextUtils.isEmpty(resultStr)){
            return null;
        }

	    try {
            JSONObject jsonObject = new JSONObject(resultStr);
            int code = jsonObject.optInt("code");
            if (code != 200) {
                return null;
            }
            JSONObject dataObj = jsonObject.optJSONObject("data");
            if (dataObj == null) {
                return null;
            }
            AntiStealingResult result = new AntiStealingResult();
            result.bitrate = dataObj.optInt("bitRate", 0);
            result.format = dataObj.optString("fileType");
            result.url = dataObj.optString("fileUrl");
            return result;
        } catch (Exception e) {
	        return null;
        }
	}

	private void onSuccess(final AntiStealingResult result) {
		delegate.onAntiStealingFinished(result, true);
		currentRequestVersion = 0;
	}

	private void onFailed() {
		if (NetworkStateUtil.isAvailable() && retryTimes < MAX_RETRY_TIMES) {
			Log.i(TAG, "failed retry" + currentRequestVersion);
			++retryTimes;
			sendHTTPRequest();
		} else {
			Log.i(TAG, "failed" + currentRequestVersion);
			delegate.onAntiStealingFinished(null, false);
			currentRequestVersion = 0;
		}
	}

	private synchronized static void addCache(String urlString, final AntiStealingResult result) {
//		CacheItem item = new CacheItem();
//		item.createTime = System.currentTimeMillis();
//		item.result = result;
//		cacheItems.put(urlString, item);
	}

	private synchronized static void removeOutOfTimeCache() {
        for (Iterator<Map.Entry<String, CacheItem>> ite = cacheItems.entrySet().iterator(); ite.hasNext();) {
            Map.Entry<String, CacheItem> entry = ite.next();
            CacheItem value = entry.getValue();
            if (System.currentTimeMillis() - value.createTime > CACHE_TIME) {
                ite.remove();
            }
        }
    }

    private synchronized static CacheItem getCacheUrl(String keyUrl) {
	    return cacheItems.get(keyUrl);
    }

    public synchronized static void removeOneCache(AudioInfo audioInfo) { //group tingshu
	    String keyUrl = new TingShuPlayStrategy().makeUrl(audioInfo);
	    cacheItems.remove(keyUrl);
    }

	public static class CacheItem {
		long					createTime;
		public AntiStealingResult	result;
	}

}

package com.lazylite.media.utils;

import android.text.TextUtils;

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.KuwoUrl;
import com.lazylite.mod.utils.crypt.Base64Coder;
import com.lazylite.mod.utils.crypt.KuwoDES;


/**
 * API接口
 * 主要是走听书后台卢永旺的服务接口
 * @author yjsf216
 * @date 2018/6/9
 */
public class TsUrlManager {

    private static final String SECRET_KEY = "kwtingshu"; // des密钥
    private static final String HOST = UrlEntrustUtils.entrustHost("https://mp.tencentmusic.com", "https://mp.tencentmusic.com");
    private static final String RES_BASE_HOST = "http://cxcnd.kuwo.cn/tingshu/";
    private final static String BASE_V2_API_URL = KuwoUrl.UrlDef.TS_MAIN_HOST.getSafeUrl()+ "/v2/api/";

    private static final String RES_HOST = RES_BASE_HOST + "res/";

//    private final static String BASE_URL = TsWebUrlManager.MAIN_HOST + "/tingshu/api/";
//    private final static String BASE_DATA_URL = BASE_URL + "data/";
//    private final static String BASE_V2_API_URL = TsWebUrlManager.MAIN_HOST + "/v2/api/";

    /**
     * 播放页购买条
     */
//    private final static String NOW_PLAY_ALBUM_URL = BASE_DATA_URL + "advert/native/album";
    /**
     * 广告栏目
     */
//    private final static String AD_FRAGMENT_URL = BASE_V2_API_URL + "advert/native/column";
//    /**
//     * 旧bookid升级成新的
//     */
//    private final static String NEW_BOOK_ID_URL = BASE_DATA_URL + "update/albumIdUpdate";
//
//    /**
//     * 订阅页面更新
//     */
//    private final static String BOOK_UPDATE_URL = BASE_DATA_URL + "album/lastSong";
//
//
//    /**
//     * 提示升级配置连接
//     */
//    private final static String UPGRADE_URL = BASE_V2_API_URL + "product/version";

    /**
     * 获取章节资源下载地址
     *
     * @param bookId
     * @param path
     * @return
     */
    public static String getResUrl(long bookId, String path) {

        StringBuilder base = new StringBuilder(RES_HOST);
        byte[] bytes = String.valueOf(bookId).getBytes();
        if (bytes == null) {
            return null;
        }
        String dir = new String(Base64Coder.encode(bytes, bytes.length,
                SECRET_KEY));
        base.append(dir).append("/");
        base.append(path);
        return base.toString();
    }

    //获取歌曲防盗链
    public static String getSongUrl(long rid, String format, String rate,
                                    String sig) {
        StringBuilder buffer = createCommonParams();
        buffer.append("type=convert_url2");
        buffer.append("&br=").append(TextUtils.isEmpty(rate) ? "" : rate);
        buffer.append("&format=").append(format);
        buffer.append("&sig=").append(TextUtils.isEmpty(sig) ? "0" : sig);
        buffer.append("&rid=").append(rid);
        buffer.append("&priority=bitrate"); //有此参数会按码率依次向下匹配,2000flac 320mp3  128mp3  48aac
        buffer.append("&loginUid=").append("0");
        buffer.append("&network=").append(NetworkStateUtil.getNetworkTypeName());
        return createURL_N(buffer.toString().getBytes());
    }

    public static synchronized StringBuilder createCommonParams() {
        return createCommonParams(true);
    }

    public static synchronized StringBuilder createCommonParams(boolean needUid) {
        StringBuilder sbCmnParams = new StringBuilder();
        sbCmnParams.append(createNewBaseParams(needUid));
        sbCmnParams.append("&android_id=").append("test");
        sbCmnParams.append("&corp=kuwo");
        sbCmnParams.append("&p2p=1");
        sbCmnParams.append("&notrace=").append(1);
        sbCmnParams.append("&");

        return sbCmnParams;
    }

    //新的请求统计通用参数,有写uid外边代表了登录id，这个地方就不能再拼装了,这里的uid是代表appuid
    private static String createNewBaseParams(boolean needUid){
        StringBuilder sb = new StringBuilder();
        //增加新的统计规则参数
        sb.append("user=").append("test");
        if (needUid) {
            sb.append("&uid=").append("0");
        }
        sb.append("&ui=").append(0);
        sb.append("&prod=").append("test");
        sb.append("&bkprod=").append("test");
        sb.append("&source=").append("test");
        sb.append("&bksource=").append("test");
        sb.append("&udid=").append("test");
        sb.append("&uniqueid=").append("test");
        //服务返回配置参数、通用参数
//        sb.append(ServerConfigMgr.getInstance().appendCommonParams());
        return sb.toString();
    }

    private static String createURL_N(byte[] paramsBytes) {
        return "http://" + "nmobi.kuwo.cn" + "/mobi.s?f=kuwo&q=" + createB64Params(paramsBytes);
    }

    private static String createB64Params(byte[] paramsBytes) {
        byte[] bytes = KuwoDES.encrypt(paramsBytes, paramsBytes.length, KuwoDES.SECRET_KEY, KuwoDES.SECRET_KEY_LENGTH);
        return new String(Base64Coder.encode(bytes, bytes.length));
    }

    public static String getPlayPageAdUrl() {
        return null;
    }

    public static String getGameTaskListUrl() {
        // TODO: 2021/7/1 后面替换url
//        String loginSid = "";
//        UserInfo userInfo = ModMgr.getUserInfoMgr().getUserInfo();
//        if (userInfo != null){
//            loginSid = userInfo.getSessionId();
//        }
//        StringBuilder sb = new StringBuilder(BASE_V2_API_URL + "mission/game/config");
//        sb.append("?").append(getCommonParams())
//                .append("&loginUid=").append(ModMgr.getUserInfoMgr().getCurrentUserId())
//                .append("&loginSid=").append(loginSid);
//        return sb.toString();

        return  "https://tingshu.kuwo.cn/v2/api/mission/game/config?user=c2d3db024774765f&uid=1964524730&" +
                "ui=522493821&prod=kwbooklite_ar_1.1.7.0&bkprod=kwbooklite_ar_1.1.7.0&source=" +
                "kwbooklite_ar_1.1.7.0_kw_cs.apk&bksource=kwbooklite_ar_1.1.7.0_kw_cs.apk&udid=" +
                "66ed73b6-b851-4eed-86cf-5f96d297942a&uniqueid=cae9a7f3021b7080825a65259109c586&examineMode=0" +
                "&userId=522493821&version=1.1.7.0&appuid=1964524730&deviceId=c2d3db024774765f&android_id=c2d3db024774765f" +
                "&src=kwbooklite_ar_1.1.7.0_kw_cs.apk&channel=kw_cs&umDeviceToken=AkWvGwN12udCYALePxO7TKRshPY3" +
                "QCP3phztfl_v6im-&loginUid=522493821&loginSid=3732075821";
    }

    /**
     * 懒人极速版权利声明
     */
    public static String getRightToDeclare() {
        return "https://tingshu.kuwo.cn/v2/api/user/protocol/power";
    }

    /**
     * 懒人极速版用户服务协议
     */
    public static String getUserServiceAgreement() {
        return "https://tingshu.kuwo.cn/v2/api/user/protocol/use";
    }

    /**
     * 隐私政策
     */
    public static String getPrivacyPolicy() {
        return "https://tingshu.kuwo.cn/v2/api/user/protocol/disclaimer";
    }

    public static String getProtocolUrl(String type) {
        StringBuilder sb = new StringBuilder("https://mp.tencentmusic.com");
        sb.append("/api/v1/config/protocol/info?type=" + type);
        return sb.toString();
}

    /**
     * 关于
     */
    public static String getKWAbout() {
        return "https://tingshu.kuwo.cn/v2/api/user/protocol/about";
    }

    public static String receiveRewardUrl(int time, int taskId) {
        // TODO: 2021/7/2 后面要替换Url
//        String loginSid = "";
//        UserInfo userInfo = ModMgr.getUserInfoMgr().getUserInfo();
//        if (userInfo != null) {
//            loginSid = userInfo.getSessionId();
//        }
//        long timestamp = System.currentTimeMillis();
//        String sign = MD5.getMD5Str(LzfAdp.AppInfo.getAppUid() + ModMgr.getUserInfoMgr().getCurrentUserId() + loginSid + taskId + time + timestamp + "receive");
//        StringBuilder sb = new StringBuilder(BASE_V2_API_URL + "mission/receive");
//        sb.append("?").append(getCommonParams())
//                .append("&taskId=").append(taskId)
//                .append("&timestamp=").append(timestamp)
//                .append("&time=").append(time)
//                .append("&loginUid=").append(ModMgr.getUserInfoMgr().getCurrentUserId())
//                .append("&loginSid=").append(loginSid)
//                .append("&sign=").append(sign);
//        return sb.toString();

        return "https://tingshu.kuwo.cn/v2/api/mission/receive?user=c2d3db024774765f&uid=196452473" +
                "0&ui=522493821&prod=kwbooklite_ar_1.1.7.0&bkprod=kwbooklite_ar_1.1.7.0&source=kwbo" +
                "oklite_ar_1.1.7.0_kw_cs.apk&bksource=kwbooklite_ar_1.1.7.0_kw_cs.apk&udid=66ed73b6" +
                "-b851-4eed-86cf-5f96d297942a&uniqueid=cae9a7f3021b7080825a65259109c586&examineMode" +
                "=0&userId=522493821&version=1.1.7.0&appuid=1964524730&deviceId=c2d3db024774765f&a" +
                "ndroid_id=c2d3db024774765f&src=kwbooklite_ar_1.1.7.0_kw_cs.apk&channel=kw_cs&umDevic" +
                "eToken=AkWvGwN12udCYALePxO7TKRshPY3QCP3phztfl_v6im-&taskId=77&timestamp=1625196408" +
                "085&time=55&loginUid=522493821&loginSid=2743520621&sign=58c30c56cef9cac61c6b56d4e5d14dc5";
    }



}

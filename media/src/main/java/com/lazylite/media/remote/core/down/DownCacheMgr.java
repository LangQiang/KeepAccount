package com.lazylite.media.remote.core.down;

import android.text.TextUtils;

import com.lazylite.media.utils.MediaDirs;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.utils.KwFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//by haiping
public final class DownCacheMgr {
	private static final String TAG = "DownCacheMgr";
	// finished cache music name format: rid.bit.format.song
	// unfinished cache music name format: rid.bit.sig.format.dat
	// info file name format: rid.bit.sig.format.totalsize.info
	//
	// unfinished download file name format:tarname.dat
	// info file name format:tarname.totalsize.info

	public static final String TING_SHU_CACHE_EXT="tingshu";
	public static final String FINISHED_CACHE_AUDIO_EXT = "audio";
	public static final String KSING_CACHE_SONG_EXT = "sing";
	public static final String FINISHED_CACHE_SONG_EXT = "song";
	public static final String UNFINISHED_CACHE_EXT = "dat";
	public static final String INFO_FILE_EXT = "info";

	public static DownCacheMgr getInstance() {
		return instance;
	}

	public static void init(final ThreadMessageHandler handler) {
		threadHandler = handler;
	}

	public static boolean isFinishedCacheSong(final String path) {
		return path == null ? false : path.endsWith(FINISHED_CACHE_SONG_EXT);
	}

	public static boolean isUnFinishedCacheSong(final String path) {
		return path == null ? false : path.endsWith(UNFINISHED_CACHE_EXT);
	}

	public static boolean isFinishedCacheTingshu(final String path) {
		return path == null ? false : path.endsWith(FINISHED_CACHE_AUDIO_EXT);
	}



	public static boolean isCacheSong(final String path) {
		return isFinishedCacheSong(path) || isUnFinishedCacheSong(path);
	}

	public static String getSongFormat(final String path) {
		if (path.endsWith(FINISHED_CACHE_SONG_EXT)
				|| path.endsWith(UNFINISHED_CACHE_EXT)
				|| path.endsWith(FINISHED_CACHE_AUDIO_EXT)
				) {
			return KwFileUtils.getFileExtension(KwFileUtils
					.getFileNameWithoutSuffix(path));
		} else {
			return KwFileUtils.getFileExtension(path);
		}
	}


	// tingshu.bookId.rid.format.dat
	public static String getUnFinishedTingshu(long bookId, long rid, String format, String md5) {
		StringBuilder builder = new StringBuilder();
		builder.append("tingshu");
		builder.append(".").append(bookId);
		builder.append(".").append(rid).append("-").append(md5);
		if (TextUtils.isEmpty(format)) {
			builder.append(".*.");
		} else{
			builder.append(".").append(format).append(".");
		}
		builder.append(UNFINISHED_CACHE_EXT);
		File[] files = KwFileUtils.getFilesClassic(MediaDirs.getMediaPath(MediaDirs.PLAY_CACHE), builder.toString());
		if (files != null && files.length > 0) {
			return files[0].getPath();
		} else {
			return null;
		}
	}

	// tingshu.bookId.rid.format.dat
	public static String getFinishedTingshu(long bookId, long rid, String md5) {
		StringBuilder builder = new StringBuilder();
		builder.append("tingshu");
		builder.append(".").append(bookId);
		builder.append(".").append(rid).append("-").append(md5);
        builder.append(".*.");//format 暂时先不传
		builder.append(FINISHED_CACHE_AUDIO_EXT);
		File[] files = KwFileUtils.getFilesClassic(MediaDirs.getMediaPath(MediaDirs.PLAY_CACHE), builder.toString());
		if (files != null && files.length > 0) {
			return files[0].getPath();
		} else {
			return null;
		}
	}


	public static String getAntiStealingSig(final String tempFilePath) {
		String fileName = KwFileUtils.getFileNameByPath(tempFilePath);
		if (fileName != null) {
			fileName = KwFileUtils.getFileNameWithoutSuffix(fileName);
			if (fileName != null) {
				fileName = KwFileUtils.getFileExtension(fileName);
			}
		}
		return fileName;
	}

	// unfinishedfile - "dat" + ".size.info"
	public static File findInfoFile(final String tmpFile) {
		String dir = KwFileUtils.getFilePath(tmpFile);
		String pre = KwFileUtils.getFileNameByPath(tmpFile);
		File[] infos = KwFileUtils.getFilesClassic(dir, pre + ".*."
				+ INFO_FILE_EXT);
		if (infos == null) {
			return null;
		}
		if (!KwFileUtils.isExist(tmpFile)) {
			infos[0].delete();
			return null;
		}
		return infos[0];
	}

	// unfinishedfile - "dat" + ".size.info"
	public static File createInfoFile(final String tmpFile,
                                      final DownloadProxy.DownType type, int totalSize) {
		StringBuilder builder = new StringBuilder(
				KwFileUtils.getFileNameWithoutSuffix(tmpFile));
		builder.append(".").append(totalSize);
		builder.append(".").append(INFO_FILE_EXT);
		File f = new File(builder.toString());
		savePos(f, type, 0);
		return f;
	}

	// info file name format: rid.bit.sig.format.totalsize.info
	public static int getSavedTotalSize(final String tmpFile) {
		File infoFile = findInfoFile(tmpFile);
		if (infoFile == null) {
			return 0;
		}
		String part = KwFileUtils.getFileNameWithoutSuffix(infoFile.getName());
		String sizeStr = KwFileUtils.getFileExtension(part);
		if (TextUtils.isEmpty(sizeStr)) {
			return 0;
		}
		int size = 0;
		try {

			size = Integer.parseInt(sizeStr);
		} catch (NumberFormatException e) {
		}
		return size;
	}

	public static void saveContinuePos(final File infoFile,
			final DownloadProxy.DownType type, final int size) {
		savePos(infoFile, type, size);
	}

	public static int getContinuePos(final File infoFile) {
		if (infoFile == null || !infoFile.exists()) {
			return 0;
		}
		int startPos = 0;
		try {
			FileInputStream fin = new FileInputStream(infoFile);
			try {
				DownloadIOUtils.readInt(fin); // ignore task type
				startPos = (int) DownloadIOUtils.readInt(fin);
			} finally {
				fin.close();
			}
			if (startPos < 0) {
				startPos = 0;
			}
		} catch (Exception e) {
			startPos = 0;
		}
		return startPos;
	}


	// 完成没完成的都删，包括info文件
	public static void deleteCacheFile(final String path) {
		MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				if (isFinishedCacheSong(path)) {
					if (!DownCacheMgr.isNoDeleteCacheType(path)) {
						KwFileUtils.deleteFile(path);
					}
				} else if (isUnFinishedCacheSong(path)) {
					DownCacheMgr.deleteInfoFile(path);
					KwFileUtils.deleteFile(path);
				} else if (isFinishedCacheTingshu(path)) {
					KwFileUtils.deleteFile(path);
				}
			}
		});
	}

	// 只删未完成，包括info文件
	public static void deleteTempFile(final String tmpFile) {
		MessageManager.getInstance().syncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				deleteInfoFile(tmpFile);
				KwFileUtils.deleteFile(tmpFile);
			}
		});
	}

	private static void savePos(final File f, final DownloadProxy.DownType type,
                                final int size) {
		if (f==null) {
			return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			try {
				DownloadIOUtils.writeInt(fos, type.ordinal());
				DownloadIOUtils.writeInt(fos, size);
			} finally {
				fos.close();
			}
		} catch (IOException e) {
		}
	}

	public static boolean moveTempFile2SavePath(final String path, final String target) {
		deleteInfoFile(path);

		if (path.equals(target)) {
			return true;
		}

		boolean ret = KwFileUtils.fileMove(path, target, true, true);

		return ret;
	}

	public static void deleteInfoFile(final String path) {
		File infoFile = findInfoFile(path);
		if (infoFile != null) {
			infoFile.delete();
		}
	}

	public static int getBitrateFromCacheFileName(final String fileName) {
		String name = KwFileUtils.getFileNameByPath(fileName);
		int first = name.indexOf(".");
		if (first > 0) {
			String part = name.substring(first + 1);
			first = part.indexOf(".");
			if (first > 0) {
				part = part.substring(0, first);
				try {
					return Integer.parseInt(part);
				} catch (Exception e) {
				}
			}
		}
		return 0;
	}

	public static String getTypeFromCacheFileName(final String fileName) {
		String name = KwFileUtils.getFileNameByPath(fileName);
		int first = name.indexOf(".");
		if (first > 0) {
			String part = name.substring(first + 1);
			first = part.indexOf(".");
			if (first > 0) {
				part = part.substring(first + 1);
				first = part.indexOf(".");
				if (first > 0) {
					part = part.substring(0, first);
					return part;
				}
			}
		}
		return null;
	}

	public static boolean isAutoDownCacheType(final String fileName) {
		if(TextUtils.isEmpty(fileName)){
			return false;
		}
		String type = getTypeFromCacheFileName(fileName);
		if(!TextUtils.isEmpty(type) && type.equals("autodown")){
			return true;
		}
		return false;
	}

	public static boolean isOfflineCacheType(final String fileName) {
		if(TextUtils.isEmpty(fileName)){
			return false;
		}
		String type = getTypeFromCacheFileName(fileName);
		if(!TextUtils.isEmpty(type) && type.equals("offline")){
			return true;
		}
		return false;
	}

	public static boolean isNoDeleteCacheType(final String fileName) {
		if(TextUtils.isEmpty(fileName)){
			return false;
		}
		String type = getTypeFromCacheFileName(fileName);
		if(!TextUtils.isEmpty(type) && (type.equals("autodown") || type.equals("offline"))){
			return true;
		}
		return false;
	}

	private static DownCacheMgr instance = new DownCacheMgr();
	private static ThreadMessageHandler threadHandler;
}

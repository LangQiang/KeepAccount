package com.lazylite.media.bean;

import android.os.Parcel;
import android.os.Parcelable;

//by haiping
public class PlayLogInfo implements Parcelable {
	public int bitrate;
	public String format;
	public boolean download;
	public int averageSpeed; // B/s
	public long fileSize;


	// 跨进程通信序列化用
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(bitrate);
		dest.writeString(format);
		dest.writeInt(download ? 1 : 0);
		dest.writeInt(averageSpeed);
	}

	public static final Parcelable.Creator<PlayLogInfo> CREATOR = new Parcelable.Creator<PlayLogInfo>() {
		@Override
		public PlayLogInfo createFromParcel(Parcel source) {
			PlayLogInfo p = new PlayLogInfo();
			p.bitrate = source.readInt();
			p.format = source.readString();
			p.download = source.readInt() != 0;
			p.averageSpeed = source.readInt();
			return p;
		}

		@Override
		public PlayLogInfo[] newArray(int size) {
			return new PlayLogInfo[size];
		}
	};
}

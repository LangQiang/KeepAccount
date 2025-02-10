package com.lazylite.media.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioInfo implements Parcelable {

    public long albumId;

    public long rid;

    public String musicName;

    public String artistName;

    public String coverUrl;

    public String musicMd5;

    public int serverDuration;

    public String resUrl;

    public String mFilePath;

    public int bitrate;

    public AudioInfo(long albumId, long rid, String musicName, String artistName, String coverUrl, String musicMd5, String resUrl) {
        this.albumId = albumId;
        this.rid = rid;
        this.musicName = musicName;
        this.artistName = artistName;
        this.coverUrl = coverUrl;
        this.musicMd5 = musicMd5;
        this.resUrl = resUrl;
    }

    protected AudioInfo(Parcel in) {
        albumId = in.readLong();
        rid = in.readLong();
        mFilePath = in.readString();
        serverDuration = in.readInt();
        resUrl = in.readString();
        bitrate = in.readInt();
        musicName = in.readString();
        artistName = in.readString();
        coverUrl = in.readString();
        musicMd5 = in.readString();
    }

    public static final Creator<AudioInfo> CREATOR = new Creator<AudioInfo>() {
        @Override
        public AudioInfo createFromParcel(Parcel in) {
            return new AudioInfo(in);
        }

        @Override
        public AudioInfo[] newArray(int size) {
            return new AudioInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(albumId);
        dest.writeLong(rid);
        dest.writeString(mFilePath);
        dest.writeInt(serverDuration);
        dest.writeString(resUrl);
        dest.writeInt(bitrate);
        dest.writeString(musicName);
        dest.writeString(artistName);
        dest.writeString(coverUrl);
        dest.writeString(musicMd5);
    }
}

package com.godq.upload.chooseimage;

import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.lazylite.mod.log.LogMgr;

/**
 * 作者：aprz on 2016/4/11.
 * 邮箱：aprz512@163.com
 */
public class PhotoInfo implements Parcelable {

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {
        @Override
        public PhotoInfo createFromParcel(Parcel source) {
            return new PhotoInfo(source);
        }

        @Override
        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };
    public boolean isSelected;
    private int photoId;
    private String path;
    private int width;
    private int height;
    private boolean isGif;
    private long fileSize;         //保存图片，视频或gif的文件大小
    private int gifDuration;       //如果是gif的话，保存gif的播放时长
    @Nullable
    private byte[] data;            //防止多次读取，
    private Matrix mMatrix = new Matrix();
    private boolean isMatrix = false;
    private boolean isUploadFailed;// 写真页使用，标识上传失败

    public PhotoInfo() {
    }

    protected PhotoInfo(Parcel in) {
        this.photoId = in.readInt();
        this.path = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.isGif = in.readByte() != 0;
        this.fileSize = in.readLong();
        this.gifDuration = in.readInt();
        this.isMatrix = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
    }

    public Matrix getMatrix() {
        if (isMatrix == false) {
            return null;
        }
        LogMgr.d("PhotoInfo", photoId + "-----get->" + mMatrix);
        return new Matrix(mMatrix);
    }

    public void setMatrix(Matrix matrix) {
        mMatrix.set(matrix);
        // null 复位
        isMatrix = matrix != null;
        LogMgr.d("PhotoInfo", photoId + "-set-->" + mMatrix);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Nullable
    public byte[] getData() {
        return data;
    }

    public void setData(@Nullable byte[] data) {
        this.data = data;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PhotoInfo)) {
            return false;
        }
        PhotoInfo info = (PhotoInfo) o;
        if (info == null) {
            return false;
        }

        return TextUtils.equals(info.getPath(), getPath());
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isUploadFailed() {
        return isUploadFailed;
    }

    public void setUploadFailed(boolean uploadFailed) {
        isUploadFailed = uploadFailed;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    public int getGifDuration() {
        return gifDuration;
    }

    public void setGifDuration(int gifDuration) {
        this.gifDuration = gifDuration;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.photoId);
        dest.writeString(this.path);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeByte(this.isGif ? (byte) 1 : (byte) 0);
        dest.writeLong(this.fileSize);
        dest.writeInt(this.gifDuration);
        dest.writeByte(this.isMatrix ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    @Override
    public String toString() {
        return "PhotoInfo{" +
                "photoId=" + photoId +
                ", path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", isGif=" + isGif +
                ", fileSize=" + fileSize +
                ", mMatrix=" + mMatrix +
                ", isMatrix=" + isMatrix +
                ", isSelected=" + isSelected +
                '}';
    }

    public static class VideoInfo extends PhotoInfo implements Parcelable {
        public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
            @Override
            public VideoInfo createFromParcel(Parcel source) {
                return new VideoInfo(source);
            }

            @Override
            public VideoInfo[] newArray(int size) {
                return new VideoInfo[size];
            }
        };
        public static int NO_ROTATION = -1;
        private long duration;//视频时长,毫秒
        private String thumbPath;//视频封面
        private int rotate = -1;//旋转度数

        public VideoInfo() {
        }

        protected VideoInfo(Parcel in) {
            super(in);
            this.duration = in.readLong();
            this.thumbPath = in.readString();
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getThumbPath() {
            return thumbPath;
        }

        public void setThumbPath(String thumbPath) {
            this.thumbPath = thumbPath;
        }

        public int getRotate() {
            return rotate;
        }

        public void setRotate(int rotate) {
            this.rotate = rotate;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(this.duration);
            dest.writeString(this.thumbPath);
        }
    }//VideoInfo end
}

package com.godq.upload.chooseimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.godq.upload.R;

import java.util.ArrayList;


/**
 * 作者：aprz on 2016/4/11.
 * 邮箱：aprz512@163.com
 */
public class PhotoListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PhotoInfo> mCurPhotoList;
    private ArrayList<PhotoInfo> mPhotoSelectList;
    private ArrayList<PhotoInfo.VideoInfo> mVideoSelectList;
    private boolean isMultiSelected = true;
    private boolean isSkin = true;

    public PhotoListAdapter(Context context, ArrayList<PhotoInfo> curPhotoList, ArrayList<PhotoInfo> selectList, ArrayList<PhotoInfo.VideoInfo> videoSelectList) {
        this.mContext = context;
        this.mCurPhotoList = curPhotoList;
        this.mPhotoSelectList = selectList;
        this.mVideoSelectList = videoSelectList;
        if (null == mVideoSelectList) {
            mVideoSelectList = new ArrayList<>();
        }
        if (null == mPhotoSelectList) {
            mPhotoSelectList = new ArrayList<>();
        }
    }

    public void setIsSkin(boolean isSkin) {
        this.isSkin = isSkin;
    }

    public void setMultiSelected(boolean multiSelected) {
        isMultiSelected = multiSelected;
    }

    @Override
    public int getCount() {
        return mCurPhotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCurPhotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (isSkin) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.tools_gallery_photo_item, parent, false);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.tools_gallery_photo_item_black, parent, false);
            }

            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.mPhotoImage = (SimpleDraweeView) convertView.findViewById(R.id.iv_photo_image);
            holder.mPhotoStatus = convertView.findViewById(R.id.cb_photo_check_status);
            holder.tvGifFlag = (TextView) convertView.findViewById(R.id.tv_photo_gif);
            holder.tvGifDuration = (TextView) convertView.findViewById(R.id.tv_photo_gifdur);
            holder.durationBg = convertView.findViewById(R.id.iv_duration_bg);
            if (!isMultiSelected) {
                holder.mPhotoStatus.setVisibility(View.GONE);
            } else {
                holder.mPhotoStatus.setVisibility(View.VISIBLE);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PhotoInfo photoInfo = mCurPhotoList.get(position);
        final boolean isVideoInfo = PhotoTools.isVideoItem(photoInfo);//是否是视频item
        String photoPath;
        if (isVideoInfo && photoInfo instanceof PhotoInfo.VideoInfo) {
            photoPath = ((PhotoInfo.VideoInfo) photoInfo).getThumbPath();
            if (TextUtils.isEmpty(photoPath)) {
                photoPath = photoInfo.getPath();//fresco 竟然可以直接加载出视频的封面图
            }
        } else {
            photoPath = photoInfo.getPath();
        }
        if (!TextUtils.isEmpty(photoPath) && (photoPath.endsWith("heic") || photoPath.endsWith("heif"))) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                holder.mPhotoImage.setImageBitmap(bitmap);
            } catch (Throwable e) {
                holder.mPhotoImage.setImageResource(R.drawable.tools_shape_photo_default_imge);
            }
        } else {
            ImageLoaderWapper.getInstance().load(holder.mPhotoImage, "file://" + photoPath);
        }
        if (isMultiSelected) {
            if (isVideoInfo) {
                holder.mPhotoStatus.setChecked(mVideoSelectList.contains(photoInfo));
            } else {
                holder.mPhotoStatus.setChecked(mPhotoSelectList.contains(photoInfo));
            }
        }

        if (isVideoInfo) {//视频
            final PhotoInfo.VideoInfo videoInfo = (PhotoInfo.VideoInfo) photoInfo;
            holder.tvGifFlag.setVisibility(View.INVISIBLE);
            if (videoInfo.getDuration() > 0) {
                holder.tvGifDuration.setText(TimeUtils.formatPlayTime((int) videoInfo.getDuration()));
                holder.tvGifDuration.setVisibility(View.VISIBLE);
                holder.setDurationBgVisible(true);
            } else {
                holder.tvGifDuration.setVisibility(View.INVISIBLE);
                holder.setDurationBgVisible(false);
            }
        } else {
            if (photoInfo.isGif()) {
                holder.tvGifFlag.setVisibility(View.VISIBLE);
                if (photoInfo.getGifDuration() > 0) {
                    holder.tvGifDuration.setText(TimeUtils.formatPlayTime(photoInfo.getGifDuration()));
                    holder.tvGifDuration.setVisibility(View.VISIBLE);
                    holder.setDurationBgVisible(true);
                } else {
                    holder.tvGifDuration.setVisibility(View.INVISIBLE);
                    holder.setDurationBgVisible(false);
                }
            } else {
                holder.tvGifFlag.setVisibility(View.INVISIBLE);
                holder.tvGifDuration.setVisibility(View.INVISIBLE);
                holder.setDurationBgVisible(false);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        public SimpleDraweeView mPhotoImage;
        public CheckBox mPhotoStatus;
        public TextView tvGifFlag, tvGifDuration;
        public ImageView durationBg;

        private void setDurationBgVisible(boolean visible) {
            if (null == durationBg) {
                return;
            }
            durationBg.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}

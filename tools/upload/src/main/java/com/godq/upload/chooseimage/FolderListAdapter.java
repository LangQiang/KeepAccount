package com.godq.upload.chooseimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.godq.upload.R;

import java.util.ArrayList;

/**
 * 作者：aprz on 2016/4/11.
 * 邮箱：aprz512@163.com
 */
public class FolderListAdapter extends BaseAdapter {
    private final ArrayList<PhotoFolderInfo> mAllPhotoFolderList;
    private final Context mContext;
    private boolean isSkin = true;

    public FolderListAdapter(Context context, ArrayList<PhotoFolderInfo> allPhotoFolderList) {
        this.mContext = context;
        this.mAllPhotoFolderList = allPhotoFolderList;
    }

    public void setIsSkin(boolean isSkin) {
        this.isSkin = isSkin;
    }

    @Override
    public int getCount() {
        if (mAllPhotoFolderList.size() == 0) {
            return 0;
        } else {
            if (mAllPhotoFolderList.get(0).getPhotoList() == null
                    || mAllPhotoFolderList.get(0).getPhotoList().size() == 0) {
                return 0;
            }
            return mAllPhotoFolderList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mAllPhotoFolderList.get(position);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.tools_gallery_folder_item, parent, false);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.tools_gallery_folder_item_black, parent, false);
            }
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.mFolderCover = (SimpleDraweeView) convertView.findViewById(R.id.iv_folder_cover);
            holder.mFolderName = (TextView) convertView.findViewById(R.id.tv_folder_name);
            holder.mFolderSize = (TextView) convertView.findViewById(R.id.tv_folder_size);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PhotoFolderInfo photoFolderInfo = mAllPhotoFolderList.get(position);
        String photoPath = photoFolderInfo.getCoverPhoto().getPath();
        if (!TextUtils.isEmpty(photoPath) && (photoPath.endsWith("heic") || photoPath.endsWith("heif"))) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                holder.mFolderCover.setImageBitmap(bitmap);
            } catch (Throwable e) {
                holder.mFolderCover.setImageResource(R.drawable.tools_shape_photo_default_imge);
            }
        } else {
            ImageLoaderWapper.getInstance().load(holder.mFolderCover, "file://" + photoPath);
        }
        holder.mFolderName.setText(photoFolderInfo.getFolderName());
        StringBuilder builder = new StringBuilder();
        builder.append("（").append(photoFolderInfo.getPhotoList().size()).append("）");
        holder.mFolderSize.setText(builder.toString());

        return convertView;
    }

    private static class ViewHolder {
        public SimpleDraweeView mFolderCover;
        public TextView mFolderName;
        public TextView mFolderSize;
    }
}

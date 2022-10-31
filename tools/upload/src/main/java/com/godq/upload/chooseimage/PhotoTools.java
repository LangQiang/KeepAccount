package com.godq.upload.chooseimage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：aprz on 2016/4/11.
 * 邮箱：aprz512@163.com
 */
public class PhotoTools {

    /**
     * 把剪裁好的本地图片文件，转为uri回调给调用方
     *
     * @param files
     * @return
     */
    public static ArrayList<Uri> transforCorpPhotoToUri(ArrayList<File> files) {
        if (files != null && files.size() > 0) {
            ArrayList<Uri> uris = new ArrayList<Uri>(files.size());
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                Uri uri = Uri.fromFile(file);
                uris.add(uri);
            }
            return uris;
        }
        return null;
    }

    /**
     * 把选择的 本地gif/图片/视频 转为uri回调给调用方
     *
     * @param infos
     * @return
     */
    public static ArrayList<Uri> transforLocalPathToUri(ArrayList<PhotoInfo> infos) {
        if (infos != null && infos.size() > 0) {
            ArrayList<Uri> uris = new ArrayList<>(infos.size());
            for (int i = 0; i < infos.size(); i++) {
                PhotoInfo info = infos.get(i);
                Uri uri = Uri.parse(transforLocalPath(info.getPath()));
                uris.add(uri);
            }
            return uris;
        }
        return null;
    }

    public static String transforLocalPath(String localPath){
        return "file://"+localPath;
    }

    /**
     * 处理图片选择后返回给调用处的数据
     *
     * @param activity
     * @param photos
     * @param cover
     */
    public static void saveResultForActivity(Activity activity, ArrayList<Uri> photos, Uri cover) {
        if (activity != null) {
            Intent data = new Intent();
            if (photos != null) {
                data.putParcelableArrayListExtra("data", photos);
            }
            if (cover != null) {
                data.putExtra("cover", cover);
            }
            if (photos == null && cover == null) {
                activity.setResult(KSingChooseImageUtil.GALLERY_RESULT_CODE);
            } else {
                activity.setResult(KSingChooseImageUtil.GALLERY_RESULT_CODE, data);
            }
        }
    }

    /**
     * 仅根据文件名判断是否为heic, heif图片格式，不考虑安全性，速度快
     * 解析文件结构判断的话，可以在文件头major_brand字段中找到MIME-subType。被标记为heif,heif-sequence,heic
     * @return
     */
    private static boolean isHEIX(File file) {
        boolean result = false;
        if (file != null && file.exists() && !file.isDirectory()) {
            String fileName = file.getName();
            if (!TextUtils.isEmpty(fileName)) {
                int peg = fileName.lastIndexOf(".");
                if (peg != -1) {
                    try {
                        String type = fileName.substring(peg + 1);
                        if (!TextUtils.isEmpty(type)) {
                            result = type.equalsIgnoreCase("heif") || type.equalsIgnoreCase("heic");
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取所有图片信息 以及 将图片以文件夹进行分类
     *
     * @param context context
     * @return 图片信息列表 第一个存放的是所有文件夹图片的信息 其余的存放的是各个文件夹图片的信息
     */
    public static List<PhotoFolderInfo> getAllPhotoFolder(Context context) {
        return getAllPhotoFolder(context, true,null);
    }

    private static final int[] sTempSize = new int[2];

    /**
     * 获取所有视频信息 以及 将视频以文件夹进行分类
     *
     * @param context      context
     * @param formatFilter 只显示给定格式的视频
     * @return 视频信息列表 第一个存放的是所有文件夹中的视频信息，其余的存放的是各个文件夹中的视频信息
     */
    public static List<PhotoFolderInfo.VideoFolderInfo> getAllVideoFolder(Context context, String[] formatFilter,OnItemFilter itemFilter) {
        final boolean needFilterFormat = null != formatFilter && formatFilter.length > 0;
        final boolean isAboveJELLY_BEAN = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
        String[] projectionVideos;
        if (isAboveJELLY_BEAN) {
            projectionVideos = new String[]{
                    MediaStore.Video.Media._ID, //唯一id
                    MediaStore.Video.Media.BUCKET_ID,//所在"文件夹"的id
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,//所在"文件夹"的名称
                    MediaStore.Video.Media.DATA,//在磁盘上的路径
                    MediaStore.Video.Media.DATE_TAKEN,//创建时间
                    MediaStore.Video.Media.DURATION,//视频时长
                    MediaStore.Video.Media.WIDTH,//The width of the video in pixels.
                    MediaStore.Video.Media.HEIGHT,//The height of the video in pixels.
                    MediaStore.Video.Thumbnails.DATA//视频缩略图
            };
        } else {
            projectionVideos = new String[]{
                    MediaStore.Video.Media._ID, //唯一id
                    MediaStore.Video.Media.BUCKET_ID,//所在"文件夹"的id
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,//所在"文件夹"的名称
                    MediaStore.Video.Media.DATA,//在磁盘上的路径
                    MediaStore.Video.Media.DATE_TAKEN,//创建时间
                    MediaStore.Video.Media.DURATION,//视频时长
                    MediaStore.Video.Thumbnails.DATA//视频缩略图
            };
        }


        final ArrayList<PhotoFolderInfo.VideoFolderInfo> alVideoFolderList = new ArrayList<>();//存放所有包含视频的文件夹，第一个元素是所有文件夹下的视频
        SparseArray<PhotoFolderInfo.VideoFolderInfo> bucketMap = new SparseArray<>();
        Cursor cursor = null;
        //所有视频，会把所有文件夹下的视频放进来
        PhotoFolderInfo.VideoFolderInfo allPhotoFolderInfo = new PhotoFolderInfo.VideoFolderInfo();
        allPhotoFolderInfo.setFolderId(0);
        allPhotoFolderInfo.setFolderName("视频");
        allPhotoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
        alVideoFolderList.add(0, allPhotoFolderInfo);
        try {
            final ContentResolver contentResolver = context.getContentResolver();
            cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    , projectionVideos, "", null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
            if (cursor != null) {
                final int bucketNameColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                final int bucketIdColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                while (cursor.moveToNext()) {
                    final int dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    final String path = cursor.getString(dataColumn);

                    //判断格式是否支持
                    if (TextUtils.isEmpty(path)) {
                        continue;
                    }
                    if (needFilterFormat) {
                        String videoFormat = path.subSequence(path.lastIndexOf('.') + 1, path.length()).toString();
                        boolean isOk = false;
                        for (String one : formatFilter) {
                            if (TextUtils.isEmpty(one)) {
                                continue;
                            }
                            if (one.equalsIgnoreCase(videoFormat)) {
                                isOk = true;
                                break;
                            }
                        }
                        if (!isOk) {
                            continue;
                        }
                    }

                    final int bucketId = cursor.getInt(bucketIdColumn);
                    final int imageIdColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    final int thumbImageColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
                    final int durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
//                    final int withColumn = isAboveJELLY_BEAN ? cursor.getColumnIndex(MediaStore.Video.Media.WIDTH) : -1;
//                    final int heightColumn = isAboveJELLY_BEAN ? cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT) : -1;

                    final String bucketName = cursor.getString(bucketNameColumn);
                    final int imageId = cursor.getInt(imageIdColumn);
                    final String thumb = cursor.getString(thumbImageColumn);
                    final long duration = cursor.getLong(durationColumn);
//                    final int with = isAboveJELLY_BEAN ? cursor.getInt(withColumn) : 0;
//                    final int height = isAboveJELLY_BEAN ? cursor.getInt(heightColumn) : 0;
                    File file = new File(path);
                    if (file.exists() && file.length() > 0) {
                        final PhotoInfo.VideoInfo videoInfo = new PhotoInfo.VideoInfo();
                        videoInfo.setPhotoId(imageId);
                        videoInfo.setPath(path);
                        videoInfo.setFileSize(file.length());
                        videoInfo.setThumbPath(thumb);
                        videoInfo.setDuration(duration);
                        videoInfo.setWidth(0);
                        videoInfo.setHeight(0);

                        boolean canShow = true;
                        if(null != itemFilter){
                            canShow = itemFilter.showItem(videoInfo);
                        }
                        if(!canShow){
                            continue;
                        }

                        if (allPhotoFolderInfo.getCoverPhoto() == null) {
                            allPhotoFolderInfo.setCoverPhoto(videoInfo);
                        }
                        //添加到所有图片
                        allPhotoFolderInfo.getPhotoList().add(videoInfo);

                        //生成包含视频的文件夹信息
                        PhotoFolderInfo.VideoFolderInfo photoFolderInfo = bucketMap.get(bucketId);
                        if (photoFolderInfo == null) {
                            photoFolderInfo = new PhotoFolderInfo.VideoFolderInfo();
                            photoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
                            photoFolderInfo.setFolderId(bucketId);
                            photoFolderInfo.setFolderName(bucketName);
                            photoFolderInfo.setCoverPhoto(videoInfo);
                            bucketMap.put(bucketId, photoFolderInfo);
                            alVideoFolderList.add(photoFolderInfo);//存放到文件夹分类的集合中
                        }
                        photoFolderInfo.getPhotoList().add(videoInfo);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (allPhotoFolderInfo.getPhotoList() == null || allPhotoFolderInfo.getPhotoList().size() == 0) {//没有视频
            alVideoFolderList.clear();
        }
        return alVideoFolderList;
    }

    /**
     * 等比例缩放，例如有一张图片它的宽高为：1080*720，然后需要将它放到 480*480 的区域并且不能拉伸，那么调用此方法后就会获得图片等比缩放后的宽高.
     *
     * @param oriWidth  原始宽度
     * @param oriHeight 原始高度
     * @param desWidth  目标最大宽度
     * @param desHeight 目标最大高度
     * @param bigSize   true的话，会依据大比例来缩放，此时会原尺寸等比缩放后会宽高会整体超出 给定宽高；false相反。
     * @return 将原始宽度按照目标宽高等比缩放后的宽高
     */
    public static int[] scaleAsRatio(float oriWidth, float oriHeight, float desWidth, float desHeight, boolean bigSize) {
        float width = desWidth;
        float height = desHeight;

        if (bigSize) {
            if (oriWidth * height > width * oriHeight) {
                width = height * oriWidth / oriHeight;
            } else if (oriWidth * height < width * oriHeight) {
                //Log.i("@@@", "image too tall, correcting");
                height = width * oriHeight / oriWidth;
            }
        } else {
            if (oriWidth * height < width * oriHeight) {
                width = height * oriWidth / oriHeight;
            } else if (oriWidth * height > width * oriHeight) {
                //Log.i("@@@", "image too tall, correcting");
                height = width * oriHeight / oriWidth;
            }
        }
        return new int[]{(int) width, (int) height};
    }

    /**
     * 获取所有图片信息 以及 将图片以文件夹进行分类
     *
     * @param context    context
     * @param isShowGif  是否加载显示gif的标识
     * @param itemFilter 可以为null
     * @return 图片信息列表 第一个存放的是所有文件夹图片的信息 其余的存放的是各个文件夹图片的信息
     */
    public static List<PhotoFolderInfo> getAllPhotoFolder(Context context, boolean isShowGif, @Nullable OnItemFilter itemFilter) {
        List<PhotoFolderInfo> allFolderList = new ArrayList<>();
        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        };
        final ArrayList<PhotoFolderInfo> allPhotoFolderList = new ArrayList<>();
        HashMap<Integer, PhotoFolderInfo> bucketMap = new HashMap<>();
        Cursor cursor = null;
        //所有图片
        PhotoFolderInfo allPhotoFolderInfo = new PhotoFolderInfo();
        allPhotoFolderInfo.setFolderId(0);
        allPhotoFolderInfo.setFolderName("所有照片");
        allPhotoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
        allPhotoFolderList.add(0, allPhotoFolderInfo);
        try {
            cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , projectionPhotos, "", null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (cursor != null) {
                BitmapFactory.Options sizeOpts = null;
                int bucketNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                final int bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                while (cursor.moveToNext()) {
                    int bucketId = cursor.getInt(bucketIdColumn);
                    String bucketName = cursor.getString(bucketNameColumn);
                    final int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    final int imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    final int imageWidthColumn = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
                    final int imageHeightColumn = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);
                    //int thumbImageColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                    final int imageId = cursor.getInt(imageIdColumn);
                    final String path = cursor.getString(dataColumn);
                    int width = cursor.getInt(imageWidthColumn);
                    int height = cursor.getInt(imageHeightColumn);
                    if (width == 0 || height == 0) {
                        if (null == sizeOpts) {
                            sizeOpts = new BitmapFactory.Options();
                        }
                        try {
                            getImageWithHeight(sizeOpts, path);
                            width = sTempSize[0];
                            height = sTempSize[1];
                        } catch (Exception ignore) {
                        }
                    }
                    //final String thumb = cursor.getString(thumbImageColumn);
                    File file = new File(path);
                    if (file.exists() && file.length() > 0 && !isHEIX(file)) {
                        final PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setPhotoId(imageId);
                        photoInfo.setPath(path);
                        photoInfo.setFileSize(file.length());
                        photoInfo.setWidth(width);
                        photoInfo.setHeight(height);
                        if (path != null && path.toLowerCase().endsWith(".gif")) {
                            photoInfo.setGif(true);
                            if (isShowGif == false) {
                                continue;
                            }
                        }
                        boolean canShow = true;
                        if (null != itemFilter) {
                            canShow = itemFilter.showItem(photoInfo);
                        }
                        if (!canShow) {
                            continue;
                        }
                        //photoInfo.setThumbPath(thumb);
                        if (allPhotoFolderInfo.getCoverPhoto() == null) {
                            allPhotoFolderInfo.setCoverPhoto(photoInfo);
                        }
                        //添加到所有图片
                        allPhotoFolderInfo.getPhotoList().add(photoInfo);

                        //通过bucketId获取文件夹
                        PhotoFolderInfo photoFolderInfo = bucketMap.get(bucketId);

                        if (photoFolderInfo == null) {
                            photoFolderInfo = new PhotoFolderInfo();
                            photoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
                            photoFolderInfo.setFolderId(bucketId);
                            photoFolderInfo.setFolderName(bucketName);
                            photoFolderInfo.setCoverPhoto(photoInfo);
                            bucketMap.put(bucketId, photoFolderInfo);
                            allPhotoFolderList.add(photoFolderInfo);
                        }
                        photoFolderInfo.getPhotoList().add(photoInfo);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (allPhotoFolderInfo.getPhotoList() == null || allPhotoFolderInfo.getPhotoList().size() == 0) {//没有图片
            allFolderList.clear();
        } else {
            allFolderList.addAll(allPhotoFolderList);
        }
        return allFolderList;
    }

    @NonNull
    public static int[] getImageWithHeight(@Nullable BitmapFactory.Options options, @NonNull String imagePath) throws Exception {
        if (null == options) {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
        }
        BitmapFactory.decodeFile(imagePath, options);
        sTempSize[0] = options.outWidth;
        sTempSize[1] = options.outHeight;
        return sTempSize;
    }

    /**
     * @return true是 VideoInfo；false不是
     */
    public static boolean isVideoItem(PhotoInfo photoInfo) {
        return photoInfo instanceof PhotoInfo.VideoInfo;
    }

    public interface OnItemFilter {
        /**
         * @return true显示 photoInfo，false 不显示
         * */
        boolean showItem(PhotoInfo photoInfo);
    }//OnItemFilter
}

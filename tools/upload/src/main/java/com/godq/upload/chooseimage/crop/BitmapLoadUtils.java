package com.godq.upload.chooseimage.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import com.godq.upload.chooseimage.IOUtils;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 作者：aprz on 2016/4/1.
 * 邮箱：aprz512@163.com
 */
public class BitmapLoadUtils {

    private static final String TAG = "BitmapLoadUtils";

    /**
     * 在子线程里面加载指定 Uri 的图片，并对图片进行压缩
     *
     * @param context        context
     * @param uri            图片 uri
     * @param requiredWidth  给定图片宽度
     * @param requiredHeight 给定图片高度
     * @param loadCallback   图片加载完成时的回调
     */
    public static void decodeBitmapInBackground(Context context, Uri uri,
                                                int requiredWidth, int requiredHeight,
                                                BitmapLoadCallback loadCallback) {
        new BitmapWorkerTask(context, uri, requiredWidth, requiredHeight, loadCallback).execute();
    }

    /**
     * 将给定的 Bitmap 进行矩阵映射，返回映射之后的图片
     *
     * @param bitmap          图片
     * @param transformMatrix 变形矩阵
     * @return 变形之后的矩阵
     */
    public static Bitmap transformBitmap(Bitmap bitmap, Matrix transformMatrix) {
        try {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformMatrix, true);
            if (bitmap != converted) {
                bitmap.recycle();
                bitmap = converted;
            }
        } catch (OutOfMemoryError error) {
            Log.e(TAG, "transformBitmap: ", error);
        }
        return bitmap;
    }

    /**
     * 计算压缩比例 计算出来的值 是 2 的幂次方，所以压缩后的宽和高小于或者等于需求的宽和高
     *
     * @param options   图片参数
     * @param reqWidth  需要的宽度
     * @param reqHeight 需要的高度
     * @return 压缩比
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width lower or equal to the requested height and width.
            while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 获取图片的 EXIF 方向
     *
     * @param context  context参数
     * @param imageUri 图片的uri
     * @return 图片的 EXIF 方向
     */
    private static int getExifOrientation(Context context, Uri imageUri) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(imageUri);
            if (stream == null) {
                return orientation;
            }
            orientation = new ImageHeaderParser(stream).getOrientation();
        } catch (IOException e) {
            Log.e(TAG, "getExifOrientation: " + imageUri.toString(), e);
        } finally {
            IOUtils.closeIoStream(stream);
        }
        return orientation;
    }

    /**
     * 根据图片的 EXIF 方向得到需要旋转的角度
     * ExifInterface.ORIENTATION_ROTATE_90 表示需要将图片顺时针方向旋转 90 度，才能使图片正常
     *
     * @param exifOrientation 图片的 EXIF 参数
     * @return 旋转角度
     */
    private static int exifToDegrees(int exifOrientation) {
        int rotation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }
        return rotation;
    }

    /**
     * 根据图片的 EXIF 方向，判断图片是否倒置
     *
     * @param exifOrientation 图片的 EXIF
     * @return -1 图片倒置 1 图片正常
     */
    private static int exifToTranslation(int exifOrientation) {
        int translation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                translation = -1;
                break;
            default:
                translation = 1;
        }
        return translation;
    }

    /**
     * 图片加载的回调接口
     */
    public interface BitmapLoadCallback {
        void onBitmapLoaded(Bitmap bitmap);

        void onFailure(Exception bitmapWorkerException);
    }

    /**
     * 图片处理结果封装类
     */
    static class BitmapWorkerResult {

        Bitmap mBitmapResult;
        Exception mBitmapWorkerException;

        public BitmapWorkerResult(Bitmap bitmapResult, Exception bitmapWorkerException) {
            mBitmapResult = bitmapResult;
            mBitmapWorkerException = bitmapWorkerException;
        }

    }

    /**
     * 图片加载实现类
     */
    static class BitmapWorkerTask extends AsyncTask<Void, Void, BitmapWorkerResult> {

        private final Context mContext;
        private final Uri mUri;
        private final int mRequiredWidth;
        private final int mRequiredHeight;

        private final BitmapLoadCallback mBitmapLoadCallback;

        public BitmapWorkerTask(Context context, Uri uri,
                                int requiredWidth, int requiredHeight,
                                BitmapLoadCallback loadCallback) {
            mContext = context;
            mUri = uri;
            mRequiredWidth = requiredWidth;
            mRequiredHeight = requiredHeight;
            mBitmapLoadCallback = loadCallback;
        }

        @Override
        protected BitmapWorkerResult doInBackground(Void... params) {
            if (mUri == null) {
                return new BitmapWorkerResult(null, new NullPointerException("Uri cannot be null"));
            }

            final ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(mUri, "r");
            } catch (FileNotFoundException e) {
                return new BitmapWorkerResult(null, e);
            }

            final FileDescriptor fileDescriptor;
            if (parcelFileDescriptor != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            } else {
                return new BitmapWorkerResult(null, new NullPointerException("ParcelFileDescriptor was null for given Uri"));
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            if (options.outWidth == -1 || options.outHeight == -1) {
                return new BitmapWorkerResult(null, new IllegalArgumentException("Bounds for bitmap could not be retrieved from Uri"));
            }

            options.inSampleSize = calculateInSampleSize(options, mRequiredWidth, mRequiredHeight);
            options.inJustDecodeBounds = false;

            Bitmap decodeSampledBitmap = null;

            boolean decodeAttemptSuccess = false;
            while (!decodeAttemptSuccess) {
                try {
                    decodeSampledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                    decodeAttemptSuccess = true;
                } catch (OutOfMemoryError error) {
                    Log.e(TAG, "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                    options.inSampleSize++;
                }
            }

            if (decodeSampledBitmap == null) {
                return new BitmapWorkerResult(null, new IllegalArgumentException("Bitmap could not be decoded from Uri"));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                IOUtils.closeIoStream(parcelFileDescriptor);
            }

            int exifOrientation = getExifOrientation(mContext, mUri);
            int exifDegrees = exifToDegrees(exifOrientation);
            int exifTranslation = exifToTranslation(exifOrientation);

            Matrix matrix = new Matrix();
            if (exifDegrees != 0) {
                matrix.preRotate(exifDegrees);
            }
            if (exifTranslation != 1) {
                matrix.postScale(exifTranslation, 1);
            }
            if (!matrix.isIdentity()) {
                return new BitmapWorkerResult(transformBitmap(decodeSampledBitmap, matrix), null);
            }

            return new BitmapWorkerResult(decodeSampledBitmap, null);
        }

        @Override
        protected void onPostExecute(BitmapWorkerResult result) {
            if (result.mBitmapWorkerException == null) {
                mBitmapLoadCallback.onBitmapLoaded(result.mBitmapResult);
            } else {
                mBitmapLoadCallback.onFailure(result.mBitmapWorkerException);
            }
        }
    }
}

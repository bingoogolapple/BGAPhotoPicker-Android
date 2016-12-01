package cn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午5:45
 * 描述:
 */
public class BGAImageCaptureManager {
    private final static String CAPTURED_PHOTO_PATH_KEY = "CAPTURED_PHOTO_PATH_KEY";
    private static final SimpleDateFormat PICTURE_NAME_POSTFIX_SDF = new SimpleDateFormat("yyyy-MM-dd_HH-mm_ss", Locale.CHINESE);
    private String mCurrentPhotoPath;
    private Context mContext;
    private File mImageDir;

    /**
     * @param context
     * @param imageDir 拍照后图片保存的目录
     */
    public BGAImageCaptureManager(Context context, File imageDir) {
        mContext = context;
        mImageDir = imageDir;
        if (!mImageDir.exists()) {
            mImageDir.mkdirs();
        }
    }

    private File createCaptureFile() throws IOException {
        File captureFile = File.createTempFile("Capture_" + PICTURE_NAME_POSTFIX_SDF.format(new Date()), ".jpg", mImageDir);
        mCurrentPhotoPath = captureFile.getAbsolutePath();
        return captureFile;
    }

    /**
     * 获取拍照意图
     *
     * @return
     * @throws IOException
     */
    public Intent getTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = createCaptureFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        return takePictureIntent;
    }

    /**
     * 刷新图库
     */
    public void refreshGallery() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(new File(mCurrentPhotoPath)));
            mContext.sendBroadcast(mediaScanIntent);
            mCurrentPhotoPath = null;
        }
    }

    /**
     * 删除拍摄的照片
     */
    public void deletePhotoFile() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            try {
                File photoFile = new File(mCurrentPhotoPath);
                photoFile.deleteOnExit();
                mCurrentPhotoPath = null;
            } catch (Exception e) {
            }
        }
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
    }

    private File createCropFile() throws IOException {
        File cropFile = File.createTempFile("Crop_" + PICTURE_NAME_POSTFIX_SDF.format(new Date()), ".png", mContext.getExternalCacheDir());
        mCurrentPhotoPath = cropFile.getAbsolutePath();
        return cropFile;
    }

    /**
     * 获取裁剪图片的 intent
     *
     * @return
     */
    public Intent getCropIntent(String inputFilePath, int width, int height) throws IOException {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(inputFilePath)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createCropFile()));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }
}
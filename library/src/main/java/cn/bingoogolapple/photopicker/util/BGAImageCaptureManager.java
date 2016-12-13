/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bingoogolapple.photopicker.util;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
    private final static String CAPTURE_PHOTO_PATH_KEY = "CAPTURE_PHOTO_PATH_KEY";
    private static final SimpleDateFormat PICTURE_NAME_POSTFIX_SDF = new SimpleDateFormat("yyyy-MM-dd_HH-mm_ss", Locale.CHINESE);
    private String mCurrentPhotoPath;
    private File mImageDir;

    /**
     * @param imageDir 拍照后图片保存的目录
     */
    public BGAImageCaptureManager(File imageDir) {
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
        if (takePictureIntent.resolveActivity(BGAPhotoPickerUtil.sApp.getPackageManager()) != null) {
            File photoFile = createCaptureFile();
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, photoFile.getAbsolutePath());
                    Uri uri = BGAPhotoPickerUtil.sApp.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
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
            BGAPhotoPickerUtil.sApp.sendBroadcast(mediaScanIntent);
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
            savedInstanceState.putString(CAPTURE_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURE_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURE_PHOTO_PATH_KEY);
        }
    }

    private File createCropFile() throws IOException {
        File cropFile = File.createTempFile("Crop_" + PICTURE_NAME_POSTFIX_SDF.format(new Date()), ".png", BGAPhotoPickerUtil.sApp.getExternalCacheDir());
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
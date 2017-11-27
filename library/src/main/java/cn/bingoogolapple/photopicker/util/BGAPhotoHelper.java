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

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午5:45
 * 描述:
 */
public class BGAPhotoHelper {
    private final static String STATE_CAMERA_FILE_PATH = "STATE_CAMERA_FILE_PATH";
    private final static String STATE_CROP_FILE_PATH = "STATE_CROP_FILE_PATH";
    private static final SimpleDateFormat PHOTO_NAME_POSTFIX_SDF = new SimpleDateFormat("yyyy-MM-dd_HH-mm_ss", Locale.getDefault());
    private File mCameraFileDir;
    private String mCameraFilePath;
    private String mCropFilePath;

    /**
     * @param cameraFileDir 拍照后图片保存的目录
     */
    public BGAPhotoHelper(File cameraFileDir) {
        mCameraFileDir = cameraFileDir;
        if (!mCameraFileDir.exists()) {
            mCameraFileDir.mkdirs();
        }
    }

    /**
     * 创建用于保存拍照生成的图片文件
     *
     * @return
     * @throws IOException
     */
    private File createCameraFile() throws IOException {
        File captureFile = File.createTempFile(
                "Capture_" + PHOTO_NAME_POSTFIX_SDF.format(new Date()),
                ".jpg",
                mCameraFileDir);
        mCameraFilePath = captureFile.getAbsolutePath();
        return captureFile;
    }

    /**
     * 创建用于保存裁剪生成的图片文件
     *
     * @return
     * @throws IOException
     */
    private File createCropFile() throws IOException {
        File cropFile = File.createTempFile(
                "Crop_" + PHOTO_NAME_POSTFIX_SDF.format(new Date()),
                ".jpg",
                BGABaseAdapterUtil.getApp().getExternalCacheDir());
        mCropFilePath = cropFile.getAbsolutePath();
        return cropFile;
    }

    /**
     * 获取从系统相册选图片意图
     *
     * @return
     */
    public Intent getChooseSystemGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return intent;
    }

    /**
     * 获取拍照意图
     *
     * @return
     * @throws IOException
     */
    public Intent getTakePhotoIntent() throws IOException {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, BGAPhotoHelper.createFileUri(createCameraFile()));
        return takePhotoIntent;
    }

    /**
     * 刷新图库
     */
    public void refreshGallery() {
        if (!TextUtils.isEmpty(mCameraFilePath)) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(createFileUri(new File(mCameraFilePath)));
            BGABaseAdapterUtil.getApp().sendBroadcast(mediaScanIntent);
            mCameraFilePath = null;
        }
    }

    /**
     * 删除拍摄的照片
     */
    public void deleteCameraFile() {
        deleteFile(mCameraFilePath);
        mCameraFilePath = null;
    }

    /**
     * 删除裁剪的照片
     */
    public void deleteCropFile() {
        deleteFile(mCropFilePath);
        mCropFilePath = null;
    }

    private void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File photoFile = new File(filePath);
            photoFile.deleteOnExit();
        }
    }

    public String getCameraFilePath() {
        return mCameraFilePath;
    }

    public String getCropFilePath() {
        return mCropFilePath;
    }

    /**
     * 获取裁剪图片的 intent
     *
     * @return
     */
    public Intent getCropIntent(String inputFilePath, int width, int height) throws IOException {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(BGAPhotoHelper.createFileUri(new File(inputFilePath)), "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createCropFile()));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    /**
     * 根据文件创建 Uri
     *
     * @param file
     * @return
     */
    public static Uri createFileUri(File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String authority = BGABaseAdapterUtil.getApp().getApplicationInfo().packageName + ".bga_photo_picker.file_provider";
            return BGAPhotoFileProvider.getUriForFile(BGABaseAdapterUtil.getApp(), authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 从 Uri 中获取文件路劲
     *
     * @param uri
     * @return
     */
    public static String getFilePathFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        String scheme = uri.getScheme();
        String filePath = null;
        if (TextUtils.isEmpty(scheme) || TextUtils.equals(ContentResolver.SCHEME_FILE, scheme)) {
            filePath = uri.getPath();
        } else if (TextUtils.equals(ContentResolver.SCHEME_CONTENT, scheme)) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            Cursor cursor = BGABaseAdapterUtil.getApp().getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    if (columnIndex > -1) {
                        filePath = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
        }
        return filePath;
    }

    public static void onRestoreInstanceState(BGAPhotoHelper photoHelper, Bundle savedInstanceState) {
        if (photoHelper != null && savedInstanceState != null) {
            photoHelper.mCameraFilePath = savedInstanceState.getString(STATE_CAMERA_FILE_PATH);
            photoHelper.mCropFilePath = savedInstanceState.getString(STATE_CROP_FILE_PATH);
        }
    }

    public static void onSaveInstanceState(BGAPhotoHelper photoHelper, Bundle savedInstanceState) {
        if (photoHelper != null && savedInstanceState != null) {
            savedInstanceState.putString(STATE_CAMERA_FILE_PATH, photoHelper.mCameraFilePath);
            savedInstanceState.putString(STATE_CROP_FILE_PATH, photoHelper.mCropFilePath);
        }
    }
}
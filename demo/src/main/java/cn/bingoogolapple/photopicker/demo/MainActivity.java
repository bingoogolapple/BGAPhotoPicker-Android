package cn.bingoogolapple.photopicker.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_SINGLE_CHOICE_PERMISSION = 0;
    private static final int REQUEST_CODE_MULTIPLE_CHOICE_PERMISSION = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW_PERMISSION = 2;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    private ArrayList<String> mSelectedImages = new ArrayList<>();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_main_single_choice) {
            singleChoice();
        } else if (v.getId() == R.id.tv_main_multiple_choice) {
            multipleChoice();
        } else if (v.getId() == R.id.tv_main_photo_preview) {
            photoPreview();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_SINGLE_CHOICE_PERMISSION)
    private void singleChoice() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File testTakePhotoDir = new File(Environment.getExternalStorageDirectory(), "testTakePhoto");
            startActivityForResult(BGAPhotoPickerActivity.newIntent(this, testTakePhotoDir, 1, null), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "选择图片需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_SINGLE_CHOICE_PERMISSION, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_MULTIPLE_CHOICE_PERMISSION)
    private void multipleChoice() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File testTakePhotoDir = new File(Environment.getExternalStorageDirectory(), "testTakePhoto");
            startActivityForResult(BGAPhotoPickerActivity.newIntent(this, testTakePhotoDir, 3, null), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "选择图片需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_MULTIPLE_CHOICE_PERMISSION, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PHOTO_PREVIEW_PERMISSION)
    private void photoPreview() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File testDownloadDir = new File(Environment.getExternalStorageDirectory(), "testDownload");
            if (mSelectedImages.size() == 1) {
                startActivity(BGAPhotoPreviewActivity.newIntent(this, testDownloadDir, mSelectedImages.get(0)));
            } else if (mSelectedImages.size() > 1) {
                startActivity(BGAPhotoPreviewActivity.newIntent(this, testDownloadDir, mSelectedImages, 0));
            }
        } else {
            EasyPermissions.requestPermissions(this, "查看图片需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_PHOTO_PREVIEW_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_SINGLE_CHOICE_PERMISSION || requestCode == REQUEST_CODE_MULTIPLE_CHOICE_PERMISSION) {
            Toast.makeText(this, "您拒绝了「选择图片」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            mSelectedImages = BGAPhotoPickerActivity.getSelectedImages(data);
        }
    }
}
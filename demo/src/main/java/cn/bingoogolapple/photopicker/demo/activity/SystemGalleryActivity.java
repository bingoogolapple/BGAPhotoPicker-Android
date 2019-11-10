package cn.bingoogolapple.photopicker.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.demo.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 你自己项目里「可以不继承 BGAPPToolbarActivity」，我在这里继承 BGAPPToolbarActivity 只是为了方便写 Demo
 */
public class SystemGalleryActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_PERMISSION_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO = 2;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_CROP = 3;

    private ImageView mAvatarIv;

    private BGAPhotoHelper mPhotoHelper;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_system_gallery_crop);
        mAvatarIv = findViewById(R.id.iv_system_gallery_crop_avatar);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("系统相册选择图片、裁剪");

        // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");
        mPhotoHelper = new BGAPhotoHelper(takePhotoDir);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BGAPhotoHelper.onSaveInstanceState(mPhotoHelper, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BGAPhotoHelper.onRestoreInstanceState(mPhotoHelper, savedInstanceState);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_system_gallery_crop_choose) {
            choosePhoto();
        } else if (v.getId() == R.id.tv_system_gallery_crop_take_photo) {
            takePhoto();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_CHOOSE_PHOTO)
    public void choosePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivityForResult(mPhotoHelper.getChooseSystemGalleryIntent(), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "请开起存储空间权限，以正常使用 Demo", REQUEST_CODE_PERMISSION_CHOOSE_PHOTO, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    public void takePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                startActivityForResult(mPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
            } catch (Exception e) {
                BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
            }
        } else {
            EasyPermissions.requestPermissions(this, "请开起存储空间和相机权限，以正常使用 Demo", REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                try {
                    startActivityForResult(mPhotoHelper.getCropIntent(mPhotoHelper.getFilePathFromUri(data.getData()), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    mPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                try {
                    startActivityForResult(mPhotoHelper.getCropIntent(mPhotoHelper.getCameraFilePath(), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    mPhotoHelper.deleteCameraFile();
                    mPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CROP) {
                BGAImage.display(mAvatarIv, R.mipmap.bga_pp_ic_holder_light, mPhotoHelper.getCropFilePath(), BGABaseAdapterUtil.dp2px(200));
            }
        } else {
            if (requestCode == REQUEST_CODE_CROP) {
                mPhotoHelper.deleteCameraFile();
                mPhotoHelper.deleteCropFile();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
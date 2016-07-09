package cn.bingoogolapple.photopicker.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 你自己项目里「可以不继承 BGAPPToolbarActivity」，我在这里继承 BGAPPToolbarActivity 只是为了方便写 Demo
 */
public class MomentAddActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PREVIEW = 2;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";

    private static final int MAX_PHOTO_COUNT = 9;

    private CheckBox mSingleChoiceCb;
    private CheckBox mTakePhotoCb;
    private CheckBox mPlusCb;

    private EditText mContentEt;
    private BGASortableNinePhotoLayout mPhotosSnpl;

    public static Moment getMoment(Intent intent) {
        return intent.getParcelableExtra(EXTRA_MOMENT);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_moment_edit);
        mSingleChoiceCb = getViewById(R.id.cb_moment_add_single_choice);
        mTakePhotoCb = getViewById(R.id.cb_moment_add_take_photo);
        mPlusCb = getViewById(R.id.cb_moment_add_plus);

        mContentEt = getViewById(R.id.et_moment_add_content);
        mPhotosSnpl = getViewById(R.id.snpl_moment_add_photos);
    }

    @Override
    protected void setListener() {
        mPlusCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mPhotosSnpl.setIsPlusSwitchOpened(true);
                } else {
                    mPhotosSnpl.setIsPlusSwitchOpened(false);
                }
            }
        });

        mPhotosSnpl.setDelegate(new BGASortableNinePhotoLayout.Delegate() {
            @Override
            public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, List<String> models) {
                choicePhoto();
            }

            @Override
            public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, List<String> models) {
                mPhotosSnpl.removeItem(position);
            }

            @Override
            public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, List<String> models) {
                photoPreview();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_moment_add_choice_photo) {
            choicePhoto();
        } else if (v.getId() == R.id.tv_moment_add_publish) {
            if (mPhotosSnpl.getItemCount() > 0) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_MOMENT, new Moment(mContentEt.getText().toString().trim(), mPhotosSnpl.getData()));
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void choicePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后图片的存放目录，替换成你自己的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(this, mTakePhotoCb.isChecked() ? takePhotoDir : null, mSingleChoiceCb.isChecked() ? 1 : MAX_PHOTO_COUNT, mPhotosSnpl.getData()), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PREVIEW)
    private void photoPreview() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, MAX_PHOTO_COUNT, mPhotosSnpl.getData(), mPhotosSnpl.getData(), mPhotosSnpl.getCurrentClickItemPosition(), false), REQUEST_CODE_PHOTO_PREVIEW);
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_PERMISSION_PHOTO_PREVIEW, perms);
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
        if (requestCode == REQUEST_CODE_PERMISSION_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_PERMISSION_PHOTO_PREVIEW) {
            Toast.makeText(this, "您拒绝了「图片预览」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedImages(data));
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }
    }
}
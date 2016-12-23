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
package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPickerAdapter;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.model.BGAImageFolderModel;
import cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGAImageCaptureManager;
import cn.bingoogolapple.photopicker.util.BGALoadPhotoTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午2:55
 * 描述:图片选择界面
 */
public class BGAPhotoPickerActivity extends BGAPPToolbarActivity implements BGAOnItemChildClickListener, BGAAsyncTask.Callback<ArrayList<BGAImageFolderModel>> {
    private static final String EXTRA_IMAGE_DIR = "EXTRA_IMAGE_DIR";
    private static final String EXTRA_SELECTED_IMAGES = "EXTRA_SELECTED_IMAGES";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_PAUSE_ON_SCROLL = "EXTRA_PAUSE_ON_SCROLL";

    /**
     * 拍照的请求码
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    /**
     * 预览照片的请求码
     */
    private static final int REQUEST_CODE_PREVIEW = 2;

    private static final int SPAN_COUNT = 3;

    private TextView mTitleTv;
    private ImageView mArrowIv;
    private TextView mSubmitTv;
    private RecyclerView mContentRv;

    private BGAImageFolderModel mCurrentImageFolderModel;

    /**
     * 是否可以拍照
     */
    private boolean mTakePhotoEnabled;
    /**
     * 最多选择多少张图片，默认等于1，为单选
     */
    private int mMaxChooseCount = 1;
    /**
     * 右上角按钮文本
     */
    private String mTopRightBtnText;
    /**
     * 图片目录数据集合
     */
    private ArrayList<BGAImageFolderModel> mImageFolderModels;

    private BGAPhotoPickerAdapter mPicAdapter;

    private BGAImageCaptureManager mImageCaptureManager;

    private BGAPhotoFolderPw mPhotoFolderPw;

    private BGALoadPhotoTask mLoadPhotoTask;
    private AppCompatDialog mLoadingDialog;

    private BGAOnNoDoubleClickListener mOnClickShowPhotoFolderListener = new BGAOnNoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (mImageFolderModels != null && mImageFolderModels.size() > 0) {
                showPhotoFolderPw();
            }
        }
    };

    /**
     * @param context        应用程序上下文
     * @param imageDir       拍照后图片保存的目录。如果传null表示没有拍照功能，如果不为null则具有拍照功能，
     * @param maxChooseCount 图片选择张数的最大值
     * @param selectedImages 当前已选中的图片路径集合，可以传null
     * @param pauseOnScroll  滚动列表时是否暂停加载图片
     * @return
     */
    public static Intent newIntent(Context context, File imageDir, int maxChooseCount, ArrayList<String> selectedImages, boolean pauseOnScroll) {
        Intent intent = new Intent(context, BGAPhotoPickerActivity.class);
        intent.putExtra(EXTRA_IMAGE_DIR, imageDir);
        intent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount);
        intent.putStringArrayListExtra(EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(EXTRA_PAUSE_ON_SCROLL, pauseOnScroll);
        return intent;
    }

    /**
     * 获取已选择的图片集合
     *
     * @param intent
     * @return
     */
    public static ArrayList<String> getSelectedImages(Intent intent) {
        return intent.getStringArrayListExtra(EXTRA_SELECTED_IMAGES);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.bga_pp_activity_photo_picker);
        mContentRv = getViewById(R.id.rv_photo_picker_content);
    }

    @Override
    protected void setListener() {
        mPicAdapter = new BGAPhotoPickerAdapter(mContentRv);
        mPicAdapter.setOnItemChildClickListener(this);

        if (getIntent().getBooleanExtra(EXTRA_PAUSE_ON_SCROLL, false)) {
            mContentRv.addOnScrollListener(new BGARVOnScrollListener(this));
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // 获取拍照图片保存目录
        File imageDir = (File) getIntent().getSerializableExtra(EXTRA_IMAGE_DIR);
        if (imageDir != null) {
            mTakePhotoEnabled = true;
            mImageCaptureManager = new BGAImageCaptureManager(imageDir);
        }
        // 获取图片选择的最大张数
        mMaxChooseCount = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        if (mMaxChooseCount < 1) {
            mMaxChooseCount = 1;
        }

        // 获取右上角按钮文本
        mTopRightBtnText = getString(R.string.bga_pp_confirm);

        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        mContentRv.setLayoutManager(layoutManager);
        mContentRv.addItemDecoration(new BGASpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider)));

        ArrayList<String> selectedImages = getIntent().getStringArrayListExtra(EXTRA_SELECTED_IMAGES);
        if (selectedImages != null && selectedImages.size() > mMaxChooseCount) {
            String selectedPhoto = selectedImages.get(0);
            selectedImages.clear();
            selectedImages.add(selectedPhoto);
        }

        mContentRv.setAdapter(mPicAdapter);
        mPicAdapter.setSelectedImages(selectedImages);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingDialog();
        mLoadPhotoTask = new BGALoadPhotoTask(this, this, mTakePhotoEnabled).perform();
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AppCompatDialog(this);
            mLoadingDialog.setContentView(R.layout.bga_pp_dialog_loading);
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_picker_title);
        View actionView = menuItem.getActionView();

        mTitleTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_title);
        mArrowIv = (ImageView) actionView.findViewById(R.id.iv_photo_picker_arrow);
        mSubmitTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_submit);

        mTitleTv.setOnClickListener(mOnClickShowPhotoFolderListener);
        mArrowIv.setOnClickListener(mOnClickShowPhotoFolderListener);
        mSubmitTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                returnSelectedImages(mPicAdapter.getSelectedImages());
            }
        });

        mTitleTv.setText(R.string.bga_pp_all_image);
        if (mCurrentImageFolderModel != null) {
            mTitleTv.setText(mCurrentImageFolderModel.name);
        }

        renderTopRightBtn();

        return true;
    }

    /**
     * 返回已选中的图片集合
     *
     * @param selectedImages
     */
    private void returnSelectedImages(ArrayList<String> selectedImages) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_IMAGES, selectedImages);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showPhotoFolderPw() {
        if (mPhotoFolderPw == null) {
            mPhotoFolderPw = new BGAPhotoFolderPw(this, mToolbar, new BGAPhotoFolderPw.Delegate() {
                @Override
                public void onSelectedFolder(int position) {
                    reloadPhotos(position);
                }

                @Override
                public void executeDismissAnim() {
                    ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(0).start();
                }
            });
        }
        mPhotoFolderPw.setData(mImageFolderModels);
        mPhotoFolderPw.show();

        ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(-180).start();
    }

    /**
     * 显示只能选择 mMaxChooseCount 张图的提示
     */
    private void toastMaxCountTip() {
        BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mImageCaptureManager.getCurrentPhotoPath());
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, 1, photos, photos, 0, true), REQUEST_CODE_PREVIEW);
            } else if (requestCode == REQUEST_CODE_PREVIEW) {
                if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
                    // 从拍照预览界面返回，刷新图库
                    mImageCaptureManager.refreshGallery();
                }

                returnSelectedImages(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_PREVIEW) {
            if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
                // 从拍照预览界面返回，删除之前拍的照片
                mImageCaptureManager.deletePhotoFile();
            } else {
                mPicAdapter.setSelectedImages(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
                renderTopRightBtn();
            }
        }
    }

    /**
     * 渲染右上角按钮
     */
    private void renderTopRightBtn() {
        if (mPicAdapter.getSelectedCount() == 0) {
            mSubmitTv.setEnabled(false);
            mSubmitTv.setText(mTopRightBtnText);
        } else {
            mSubmitTv.setEnabled(true);
            mSubmitTv.setText(mTopRightBtnText + "(" + mPicAdapter.getSelectedCount() + "/" + mMaxChooseCount + ")");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTakePhotoEnabled) {
            mImageCaptureManager.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mTakePhotoEnabled) {
            mImageCaptureManager.onRestoreInstanceState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int position) {
        if (view.getId() == R.id.iv_item_photo_camera_camera) {
            handleTakePhoto();
        } else if (view.getId() == R.id.iv_item_photo_picker_photo) {
            changeToPreview(position);
        } else if (view.getId() == R.id.iv_item_photo_picker_flag) {
            handleClickSelectFlagIv(position);
        }
    }

    /**
     * 处理拍照
     */
    private void handleTakePhoto() {
        if (mMaxChooseCount == 1) {
            // 单选
            takePhoto();
        } else if (mPicAdapter.getSelectedCount() == mMaxChooseCount) {
            toastMaxCountTip();
        } else {
            takePhoto();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            startActivityForResult(mImageCaptureManager.getTakePictureIntent(), REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception e) {
            BGAPhotoPickerUtil.show(R.string.bga_pp_photo_not_support);
        }
    }

    /**
     * 跳转到图片选择预览界面
     *
     * @param position 当前点击的item的索引位置
     */
    private void changeToPreview(int position) {
        int currentPosition = position;
        if (mCurrentImageFolderModel.isTakePhotoEnabled()) {
            currentPosition--;
        }
        startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mMaxChooseCount, mPicAdapter
                        .getSelectedImages(), (ArrayList<String>) mPicAdapter.getData(), currentPosition, false),
                REQUEST_CODE_PREVIEW);
    }

    /**
     * 处理点击选择按钮事件
     *
     * @param position 当前点击的item的索引位置
     */
    private void handleClickSelectFlagIv(int position) {
        String currentImage = mPicAdapter.getItem(position);
        if (mMaxChooseCount == 1) {
            // 单选

            if (mPicAdapter.getSelectedCount() > 0) {
                String selectedImage = mPicAdapter.getSelectedImages().remove(0);
                if (TextUtils.equals(selectedImage, currentImage)) {
                    mPicAdapter.notifyItemChanged(position);
                } else {
                    int preSelectedImagePosition = mPicAdapter.getData().indexOf(selectedImage);
                    mPicAdapter.notifyItemChanged(preSelectedImagePosition);
                    mPicAdapter.getSelectedImages().add(currentImage);
                    mPicAdapter.notifyItemChanged(position);
                }
            } else {
                mPicAdapter.getSelectedImages().add(currentImage);
                mPicAdapter.notifyItemChanged(position);
            }
            renderTopRightBtn();
        } else {
            // 多选

            if (!mPicAdapter.getSelectedImages().contains(currentImage) && mPicAdapter.getSelectedCount() == mMaxChooseCount) {
                toastMaxCountTip();
            } else {
                if (mPicAdapter.getSelectedImages().contains(currentImage)) {
                    mPicAdapter.getSelectedImages().remove(currentImage);
                } else {
                    mPicAdapter.getSelectedImages().add(currentImage);
                }
                mPicAdapter.notifyItemChanged(position);

                renderTopRightBtn();
            }
        }
    }

    private void reloadPhotos(int position) {
        if (position < mImageFolderModels.size()) {
            mCurrentImageFolderModel = mImageFolderModels.get(position);
            if (mTitleTv != null) {
                mTitleTv.setText(mCurrentImageFolderModel.name);
            }

            mPicAdapter.setImageFolderModel(mCurrentImageFolderModel);
        }
    }

    @Override
    public void onPostExecute(ArrayList<BGAImageFolderModel> imageFolderModels) {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
        mImageFolderModels = imageFolderModels;
        reloadPhotos(mPhotoFolderPw == null ? 0 : mPhotoFolderPw.getCurrentPosition());
    }

    @Override
    public void onTaskCancelled() {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
    }

    private void cancelLoadPhotoTask() {
        if (mLoadPhotoTask != null) {
            mLoadPhotoTask.cancelTask();
            mLoadPhotoTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        cancelLoadPhotoTask();

        super.onDestroy();
    }
}
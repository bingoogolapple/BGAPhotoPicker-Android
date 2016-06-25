package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAImageFolderModel;
import cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw;
import cn.bingoogolapple.photopicker.util.BGAImageCaptureManager;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午2:55
 * 描述:图片选择界面
 */
public class BGAPhotoPickerActivity extends BGAPPToolbarActivity implements BGAOnItemChildClickListener {
    private static final String EXTRA_IMAGE_DIR = "EXTRA_IMAGE_DIR";
    private static final String EXTRA_SELECTED_IMAGES = "EXTRA_SELECTED_IMAGES";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_TOP_RIGHT_BTN_TEXT = "EXTRA_TOP_RIGHT_BTN_TEXT";

    /**
     * 拍照的请求码
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    /**
     * 预览照片的请求码
     */
    private static final int REQUEST_CODE_PREVIEW = 2;

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

    private PhotoPickerAdapter mPicAdapter;

    private BGAImageCaptureManager mImageCaptureManager;

    private BGAPhotoFolderPw mPhotoFolderPw;
    /**
     * 上一次显示图片目录的时间戳，防止短时间内重复点击图片目录菜单时界面错乱
     */
    private long mLastShowPhotoFolderTime;

    /**
     * @param context         应用程序上下文
     * @param imageDir        拍照后图片保存的目录。如果传null表示没有拍照功能，如果不为null则具有拍照功能，
     * @param maxChooseCount  图片选择张数的最大值
     * @param selectedImages  当前已选中的图片路径集合，可以传null
     * @param topRightBtnText 右上角按钮的文本
     * @return
     */
    public static Intent newIntent(Context context, File imageDir, int maxChooseCount, ArrayList<String> selectedImages, String topRightBtnText) {
        Intent intent = new Intent(context, BGAPhotoPickerActivity.class);
        intent.putExtra(EXTRA_IMAGE_DIR, imageDir);
        intent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount);
        intent.putStringArrayListExtra(EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(EXTRA_TOP_RIGHT_BTN_TEXT, topRightBtnText);
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
        mPicAdapter = new PhotoPickerAdapter(mContentRv);
        mPicAdapter.setOnItemChildClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // 获取拍照图片保存目录
        File imageDir = (File) getIntent().getSerializableExtra(EXTRA_IMAGE_DIR);
        if (imageDir != null) {
            mTakePhotoEnabled = true;
            mImageCaptureManager = new BGAImageCaptureManager(this, imageDir);
        }
        // 获取图片选择的最大张数
        mMaxChooseCount = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        if (mMaxChooseCount < 1) {
            mMaxChooseCount = 1;
        }

        // 获取右上角按钮文本
        mTopRightBtnText = getIntent().getStringExtra(EXTRA_TOP_RIGHT_BTN_TEXT);

        GridLayoutManager layoutManager = new GridLayoutManager(this, BGASpaceItemDecoration.SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        mContentRv.setLayoutManager(layoutManager);
        mContentRv.addItemDecoration(new BGASpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.bga_pp_size_level1) / 4));


        mContentRv.setAdapter(mPicAdapter);
        mPicAdapter.setSelectedImages(getIntent().getStringArrayListExtra(EXTRA_SELECTED_IMAGES));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDatas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_picker_title);
        View actionView = menuItem.getActionView();

        mTitleTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_title);
        mArrowIv = (ImageView) actionView.findViewById(R.id.iv_photo_picker_arrow);
        mSubmitTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_submit);

        mTitleTv.setOnClickListener(this);
        mArrowIv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);

        if (mCurrentImageFolderModel != null) {
            mTitleTv.setText(mCurrentImageFolderModel.name);
        }

        renderTopRightBtn();

        return true;
    }

    @Override
    public void onClick(View v) {
        if ((v.getId() == R.id.tv_photo_picker_title || v.getId() == R.id.iv_photo_picker_arrow) && System.currentTimeMillis() - mLastShowPhotoFolderTime > BGAPhotoFolderPw.ANIM_DURATION) {
            showPhotoFolderPw();
            mLastShowPhotoFolderTime = System.currentTimeMillis();
        } else if (v.getId() == R.id.tv_photo_picker_submit) {
            returnSelectedImages(mPicAdapter.getSelectedImages());
        }
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
        mPhotoFolderPw.setDatas(mImageFolderModels);
        mPhotoFolderPw.show();

        ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(-180).start();
    }

    /**
     * 显示只能选择 mMaxChooseCount 张图的提示
     */
    private void toastMaxCountTip() {
        BGAPhotoPickerUtil.show(this, getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            startActivityForResult(mImageCaptureManager.getTakePictureIntent(), REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception e) {
            BGAPhotoPickerUtil.show(this, R.string.bga_pp_photo_not_support);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                // 更新图库
                mImageCaptureManager.refreshGallery();

                String photoPath = mImageCaptureManager.getCurrentPhotoPath();
                mPicAdapter.getSelectedImages().add(photoPath);
                mPicAdapter.getDatas().add(0, photoPath);
                renderTopRightBtn();

                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mMaxChooseCount, mPicAdapter.getSelectedImages(), (ArrayList<String>) mPicAdapter.getDatas(), 0, mTopRightBtnText, true), REQUEST_CODE_PREVIEW);
            } else if (requestCode == REQUEST_CODE_PREVIEW) {
                returnSelectedImages(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_PREVIEW) {
            mPicAdapter.setSelectedImages(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
            renderTopRightBtn();
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

    private void loadDatas() {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");

        BGAImageFolderModel allImageFolderModel = new BGAImageFolderModel(mTakePhotoEnabled);

        HashMap<String, BGAImageFolderModel> imageFolderModelMap = new HashMap<>();
        BGAImageFolderModel otherImageFolderModel = null;
        if (cursor != null && cursor.getCount() > 0) {
            boolean firstInto = true;
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                if (!TextUtils.isEmpty(imagePath)) {
                    if (firstInto) {
                        allImageFolderModel.name = getString(R.string.bga_pp_all_image);
                        allImageFolderModel.coverPath = imagePath;
                        firstInto = false;
                    }
                    // 所有图片目录每次都添加
                    allImageFolderModel.addLastImage(imagePath);

                    String folderPath = null;
                    // 其他图片目录
                    File folder = new File(imagePath).getParentFile();
                    if (folder != null) {
                        folderPath = folder.getAbsolutePath();
                    }

                    if (TextUtils.isEmpty(folderPath)) {
                        int end = imagePath.lastIndexOf(File.separator);
                        if (end != -1) {
                            folderPath = imagePath.substring(0, end);
                        }
                    }

                    if (!TextUtils.isEmpty(folderPath)) {
                        if (imageFolderModelMap.containsKey(folderPath)) {
                            otherImageFolderModel = imageFolderModelMap.get(folderPath);
                        } else {
                            String folderName = folderPath.substring(folderPath.lastIndexOf(File.separator) + 1);
                            if (TextUtils.isEmpty(folderName)) {
                                folderName = "/";
                            }
                            otherImageFolderModel = new BGAImageFolderModel(folderName, imagePath);
                            imageFolderModelMap.put(folderPath, otherImageFolderModel);
                        }
                        otherImageFolderModel.addLastImage(imagePath);
                    }
                }
            }
            cursor.close();
        }

        mImageFolderModels = new ArrayList();
        // 添加所有图片目录
        mImageFolderModels.add(allImageFolderModel);

        // 添加其他图片目录
        Iterator<Map.Entry<String, BGAImageFolderModel>> iterator = imageFolderModelMap.entrySet().iterator();
        while (iterator.hasNext()) {
            mImageFolderModels.add(iterator.next().getValue());
        }

        reloadPhotos(mPhotoFolderPw == null ? 0 : mPhotoFolderPw.getCurrentPosition());
    }

    private void reloadPhotos(int position) {
        mCurrentImageFolderModel = mImageFolderModels.get(position);
        if (mTitleTv != null) {
            mTitleTv.setText(mCurrentImageFolderModel.name);
        }
        mPicAdapter.setDatas(mCurrentImageFolderModel.getImages());
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int position) {
        if (view.getId() == R.id.iv_item_photo_picker_flag) {
            String image = mPicAdapter.getItem(position);
            if (!mPicAdapter.getSelectedImages().contains(image) && mPicAdapter.getSelectedCount() == mMaxChooseCount) {
                toastMaxCountTip();
            } else {
                if (mPicAdapter.getSelectedImages().contains(image)) {
                    mPicAdapter.getSelectedImages().remove(image);
                } else {
                    mPicAdapter.getSelectedImages().add(image);
                }
                mPicAdapter.notifyItemChanged(position);

                renderTopRightBtn();
            }
        } else if (view.getId() == R.id.iv_item_photo_picker_photo) {
            if (mCurrentImageFolderModel.isTakePhotoEnabled() && position == 0) {
                if (mPicAdapter.getSelectedCount() == mMaxChooseCount) {
                    toastMaxCountTip();
                } else {
                    takePhoto();
                }
            } else {
                int currentPosition = position;
                if (mCurrentImageFolderModel.isTakePhotoEnabled()) {
                    currentPosition--;
                }
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mMaxChooseCount, mPicAdapter.getSelectedImages(), (ArrayList<String>) mPicAdapter.getDatas(), currentPosition, mTopRightBtnText, false), REQUEST_CODE_PREVIEW);
            }
        }
    }

    private class PhotoPickerAdapter extends BGARecyclerViewAdapter<String> {
        private ArrayList<String> mSelectedImages = new ArrayList<>();
        private int mImageWidth;
        private int mImageHeight;

        public PhotoPickerAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.bga_pp_item_photo_picker);
            mImageWidth = BGAPhotoPickerUtil.getScreenWidth(recyclerView.getContext()) / 9;
            mImageHeight = mImageWidth;
        }

        @Override
        public void setItemChildListener(BGAViewHolderHelper helper) {
            helper.setItemChildClickListener(R.id.iv_item_photo_picker_flag);
            helper.setItemChildClickListener(R.id.iv_item_photo_picker_photo);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            if (mCurrentImageFolderModel.isTakePhotoEnabled() && position == 0) {
                helper.setVisibility(R.id.tv_item_photo_picker_tip, View.VISIBLE);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setScaleType(ImageView.ScaleType.CENTER);
                helper.setImageResource(R.id.iv_item_photo_picker_photo, R.mipmap.bga_pp_ic_gallery_camera);

                helper.setVisibility(R.id.iv_item_photo_picker_flag, View.INVISIBLE);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(null);
            } else {
                helper.setVisibility(R.id.tv_item_photo_picker_tip, View.INVISIBLE);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setScaleType(ImageView.ScaleType.CENTER_CROP);
                BGAImage.displayImage(helper.getImageView(R.id.iv_item_photo_picker_photo), model, R.mipmap.bga_pp_ic_holder_dark,R.mipmap.bga_pp_ic_holder_dark, mImageWidth, mImageHeight, null);

                helper.setVisibility(R.id.iv_item_photo_picker_flag, View.VISIBLE);

                if (mSelectedImages.contains(model)) {
                    helper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.bga_pp_ic_cb_checked);
                    helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(getResources().getColor(R.color.bga_pp_photo_selected_color));
                } else {
                    helper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.bga_pp_ic_cb_normal);
                    helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(null);
                }
            }
        }

        public void setSelectedImages(ArrayList<String> selectedImages) {
            if (selectedImages != null) {
                mSelectedImages = selectedImages;
            }
            notifyDataSetChanged();
        }

        public ArrayList<String> getSelectedImages() {
            return mSelectedImages;
        }

        public int getSelectedCount() {
            return mSelectedImages.size();
        }
    }
}
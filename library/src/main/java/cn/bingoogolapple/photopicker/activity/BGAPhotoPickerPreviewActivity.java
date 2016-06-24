package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午2:57
 * 描述:
 */
public class BGAPhotoPickerPreviewActivity extends BGAPPToolbarActivity {
    private static final String EXTRA_PREVIEW_IMAGES = "EXTRA_PREVIEW_IMAGES";
    private static final String EXTRA_SELECTED_IMAGES = "EXTRA_SELECTED_IMAGES";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_TOP_RIGHT_BTN_TEXT = "EXTRA_TOP_RIGHT_BTN_TEXT";
    private static final String EXTRA_IS_FROM_TAKE_PHOTO = "EXTRA_IS_FROM_TAKE_PHOTO";

    private RelativeLayout mTitleRl;
    private TextView mTitleTv;
    private TextView mSubmitTv;
    private BGAHackyViewPager mContentHvp;
    private RelativeLayout mChooseRl;
    private TextView mChooseTv;

    private ArrayList<String> mSelectedImages;
    private ArrayList<String> mPreviewImages;
    private int mMaxChooseCount = 1;
    /**
     * 右上角按钮文本
     */
    private String mTopRightBtnText;

    private boolean mIsHidden = false;
    /**
     * 上一次标题栏显示或隐藏的时间戳
     */
    private long mLastShowHiddenTime;
    /**
     * 是否是拍完照后跳转过来
     */
    private boolean mIsFromTakePhoto;

    /**
     * @param context         应用程序上下文
     * @param maxChooseCount  图片选择张数的最大值
     * @param selectedImages  当前已选中的图片路径集合，可以传null
     * @param previewImages   当前预览的图片目录里的图片路径集合
     * @param currentPosition 当前预览图片的位置
     * @param topRightBtnText 右上角按钮的文本
     * @param isFromTakePhoto 是否是拍完照后跳转过来
     * @return
     */
    public static Intent newIntent(Context context, int maxChooseCount, ArrayList<String> selectedImages, ArrayList<String> previewImages, int currentPosition, String topRightBtnText, boolean isFromTakePhoto) {
        Intent intent = new Intent(context, BGAPhotoPickerPreviewActivity.class);
        intent.putStringArrayListExtra(EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putStringArrayListExtra(EXTRA_PREVIEW_IMAGES, previewImages);
        intent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount);
        intent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_TOP_RIGHT_BTN_TEXT, topRightBtnText);
        intent.putExtra(EXTRA_IS_FROM_TAKE_PHOTO, isFromTakePhoto);
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
        setContentView(R.layout.bga_pp_activity_photo_picker_preview);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

}
package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGASavePhotoTask;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import cn.bingoogolapple.photopicker.widget.BGAImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午2:59
 * 描述:图片预览界面
 */
public class BGAPhotoPreviewActivity extends BGAPPToolbarActivity implements PhotoViewAttacher.OnViewTapListener, BGAAsyncTask.Callback<Void> {
    private static final String EXTRA_SAVE_IMG_DIR = "EXTRA_SAVE_IMG_DIR";
    private static final String EXTRA_PREVIEW_IMAGES = "EXTRA_PREVIEW_IMAGES";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_IS_SINGLE_PREVIEW = "EXTRA_IS_SINGLE_PREVIEW";
    private static final String EXTRA_PHOTO_PATH = "EXTRA_PHOTO_PATH";

    private TextView mTitleTv;
    private ImageView mDownloadIv;
    private BGAHackyViewPager mContentHvp;

    private ArrayList<String> mPreviewImages;
    private boolean mIsSinglePreview;

    private File mSaveImgDir;

    private boolean mIsHidden = false;
    private BGASavePhotoTask mSavePhotoTask;

    /**
     * 上一次标题栏显示或隐藏的时间戳
     */
    private long mLastShowHiddenTime;

    /**
     * 获取查看图片的intent
     *
     * @param context
     * @param saveImgDir      保存图片的目录，如果传null，则没有保存图片功能
     * @param previewImages   当前预览的图片目录里的图片路径集合
     * @param currentPosition 当前预览图片的位置
     * @return
     */
    public static Intent newIntent(Context context, File saveImgDir, ArrayList<String> previewImages, int currentPosition) {
        Intent intent = new Intent(context, BGAPhotoPreviewActivity.class);
        intent.putExtra(EXTRA_SAVE_IMG_DIR, saveImgDir);
        intent.putStringArrayListExtra(EXTRA_PREVIEW_IMAGES, previewImages);
        intent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_IS_SINGLE_PREVIEW, false);
        return intent;
    }

    /**
     * 获取查看图片的intent
     *
     * @param context
     * @param saveImgDir 保存图片的目录，如果传null，则没有保存图片功能
     * @param photoPath  图片路径
     * @return
     */
    public static Intent newIntent(Context context, File saveImgDir, String photoPath) {
        Intent intent = new Intent(context, BGAPhotoPreviewActivity.class);
        intent.putExtra(EXTRA_SAVE_IMG_DIR, saveImgDir);
        intent.putExtra(EXTRA_PHOTO_PATH, photoPath);
        intent.putExtra(EXTRA_CURRENT_POSITION, 0);
        intent.putExtra(EXTRA_IS_SINGLE_PREVIEW, true);
        return intent;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setNoLinearContentView(R.layout.bga_pp_activity_photo_preview);
        mContentHvp = getViewById(R.id.hvp_photo_preview_content);
    }

    @Override
    protected void setListener() {
        mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                renderTitleTv();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mSaveImgDir = (File) getIntent().getSerializableExtra(EXTRA_SAVE_IMG_DIR);


        mPreviewImages = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_IMAGES);

        mIsSinglePreview = getIntent().getBooleanExtra(EXTRA_IS_SINGLE_PREVIEW, false);
        if (mIsSinglePreview) {
            mPreviewImages = new ArrayList<>();
            mPreviewImages.add(getIntent().getStringExtra(EXTRA_PHOTO_PATH));
        }

        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);
        mContentHvp.setAdapter(new ImagePageAdapter());
        mContentHvp.setCurrentItem(currentPosition);


        // 过2秒隐藏标题栏
        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenTitlebar();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_preview_title);
        View actionView = menuItem.getActionView();

        mTitleTv = (TextView) actionView.findViewById(R.id.tv_photo_preview_title);
        mDownloadIv = (ImageView) actionView.findViewById(R.id.iv_photo_preview_download);
        mDownloadIv.setOnClickListener(this);

        if (mSaveImgDir == null) {
            mDownloadIv.setVisibility(View.GONE);
        }

        renderTitleTv();

        return true;
    }

    private void renderTitleTv() {
        if (mTitleTv == null) {
            return;
        }

        if (mIsSinglePreview) {
            mTitleTv.setText(R.string.bga_pp_view_photo);
        } else {
            mTitleTv.setText((mContentHvp.getCurrentItem() + 1) + "/" + mPreviewImages.size());
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();
            if (mIsHidden) {
                showTitlebar();
            } else {
                hiddenTitlebar();
            }
        }
    }

    private void showTitlebar() {
        ViewCompat.animate(mToolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                mIsHidden = false;
            }
        }).start();
    }

    private void hiddenTitlebar() {
        ViewCompat.animate(mToolbar).translationY(-mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                mIsHidden = true;
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_photo_preview_download) {
            if (mSavePhotoTask == null) {
                savePic();
            }
        }
    }

    private synchronized void savePic() {
        if (mSavePhotoTask != null) {
            return;
        }

        final String url = mPreviewImages.get(mContentHvp.getCurrentItem());
        File file;
        if (url.startsWith("file")) {
            file = new File(url.replace("file://", ""));
            if (file.exists()) {
                BGAPhotoPickerUtil.showSafe(BGAPhotoPreviewActivity.this, getString(R.string.bga_pp_save_img_success_folder, file.getParentFile().getAbsolutePath()));
                return;
            }
        }

        // 通过MD5加密url生成文件名，避免多次保存同一张图片
        file = new File(mSaveImgDir, BGAPhotoPickerUtil.md5(url) + ".png");
        if (file.exists()) {
            BGAPhotoPickerUtil.showSafe(this, getString(R.string.bga_pp_save_img_success_folder, mSaveImgDir.getAbsolutePath()));
            return;
        }

        mSavePhotoTask = new BGASavePhotoTask(this, this.getApplication(), file);
        BGAImage.downloadImage(this, url, new BGAImageLoader.DownloadDelegate() {
            @Override
            public void onSuccess(String url, Bitmap bitmap) {
                mSavePhotoTask.setBitmapAndPerform(bitmap);
            }

            @Override
            public void onFailed(String url) {
                mSavePhotoTask = null;
                BGAPhotoPickerUtil.show(BGAPhotoPreviewActivity.this, R.string.bga_pp_save_img_failure);
            }
        });
    }

    @Override
    public void onPostExecute(Void aVoid) {
        mSavePhotoTask = null;
    }

    @Override
    public void onCancled() {
        mSavePhotoTask = null;
    }

    @Override
    protected void onDestroy() {
        if (mSavePhotoTask != null) {
            mSavePhotoTask.cancelTask();
            mSavePhotoTask = null;
        }
        super.onDestroy();
    }

    private class ImagePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPreviewImages.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            BGAImageView imageView = new BGAImageView(container.getContext());
            container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
            photoViewAttacher.setOnViewTapListener(BGAPhotoPreviewActivity.this);
            imageView.setDelegate(new BGAImageView.Delegate() {
                @Override
                public void onDrawableChanged() {
                    photoViewAttacher.update();
                }
            });

            BGAImage.displayImage(imageView, mPreviewImages.get(position), R.mipmap.bga_pp_ic_holder_dark, R.mipmap.bga_pp_ic_holder_dark, BGAPhotoPickerUtil.getScreenWidth(imageView.getContext()), BGAPhotoPickerUtil.getScreenHeight(imageView.getContext()), null);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
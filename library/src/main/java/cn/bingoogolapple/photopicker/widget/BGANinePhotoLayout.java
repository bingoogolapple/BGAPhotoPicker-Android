package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/8 下午2:41
 * 描述:
 */
public class BGANinePhotoLayout extends FrameLayout {
    private PicAdapter mPicAdapter;
    private ImageView mPicIv;
    private BGAHeightWrapGridView mPicGv;
    private Delegate mDelegate;
    private int mCurrentClickItemPosition;

    public BGANinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPicIv = new ImageView(context);
        mPicIv.setClickable(true);
        mPicIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPicGv = new BGAHeightWrapGridView(context);
        int spacing = context.getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider);
        mPicGv.setHorizontalSpacing(spacing);
        mPicGv.setVerticalSpacing(spacing);
        mPicGv.setNumColumns(3);
        mPicAdapter = new PicAdapter(context);
        mPicAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup parent, View childView, int position) {
                if (childView.getId() == R.id.iv_item_nine_photo_photo) {
                    mCurrentClickItemPosition = position;
                    if (mDelegate != null) {
                        mDelegate.onClickNinePhotoItem(BGANinePhotoLayout.this, childView, position, mPicAdapter.getItem(position), mPicAdapter.getDatas());
                    }
                }
            }
        });
        mPicAdapter.setOnItemChildLongClickListener(new BGAOnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(ViewGroup parent, View childView, int position) {
                if (childView.getId() == R.id.iv_item_nine_photo_photo) {
                    mCurrentClickItemPosition = position;
                    if (mDelegate != null) {
                        return mDelegate.onLongClickNinePhotoItem(BGANinePhotoLayout.this, childView, position, mPicAdapter.getItem(position), mPicAdapter.getDatas());
                    }
                }
                return false;
            }
        });
        mPicGv.setAdapter(mPicAdapter);

        addView(mPicIv);
        addView(mPicGv);
    }

    public void setData(final ArrayList<String> photos) {
        int itemWidth = BGAPhotoPickerUtil.getScreenWidth(getContext()) / 4;
        if (photos.size() == 0) {
            setVisibility(GONE);
        } else if (photos.size() == 1) {
            setVisibility(VISIBLE);
            mPicGv.setVisibility(GONE);
            mPicAdapter.setDatas(photos);
            mPicIv.setVisibility(VISIBLE);

            ViewGroup.LayoutParams layoutParams = mPicIv.getLayoutParams();
            layoutParams.width = itemWidth * 2;
            layoutParams.height = itemWidth * 2;

            mPicIv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentClickItemPosition = 0;
                    if (mDelegate != null) {
                        mDelegate.onClickNinePhotoItem(BGANinePhotoLayout.this, v, mCurrentClickItemPosition, photos.get(mCurrentClickItemPosition), photos);
                    }
                }
            });
            mPicIv.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mCurrentClickItemPosition = 0;
                    if (mDelegate != null) {
                        return mDelegate.onLongClickNinePhotoItem(BGANinePhotoLayout.this, view, mCurrentClickItemPosition, mPicAdapter.getItem(mCurrentClickItemPosition), mPicAdapter.getDatas());
                    }
                    return false;
                }
            });

            BGAImage.displayImage(mPicIv, photos.get(0), R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, itemWidth, itemWidth, null);
        } else {
            setVisibility(VISIBLE);
            mPicIv.setVisibility(GONE);
            mPicGv.setVisibility(VISIBLE);

            ViewGroup.LayoutParams layoutParams = mPicGv.getLayoutParams();
            if (photos.size() == 2) {
                mPicGv.setNumColumns(2);
                layoutParams.width = itemWidth * 2;
                layoutParams.height = itemWidth * 1;
            } else if (photos.size() == 4) {
                mPicGv.setNumColumns(2);
                layoutParams.width = itemWidth * 2;
                layoutParams.height = itemWidth * 2;
            } else {
                mPicGv.setNumColumns(3);
                layoutParams.width = itemWidth * 3;
                layoutParams.height = itemWidth * 3;
            }
            mPicGv.setLayoutParams(layoutParams);
            mPicAdapter.setDatas(photos);
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ArrayList<String> getData() {
        return (ArrayList<String>) mPicAdapter.getDatas();
    }

    public int getItemCount() {
        return mPicAdapter.getCount();
    }

    public String getCurrentClickItem() {
        return mPicAdapter.getItem(mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return mCurrentClickItemPosition;
    }

    private static class PicAdapter extends BGAAdapterViewAdapter<String> {
        private int mImageWidth;
        private int mImageHeight;

        public PicAdapter(Context context) {
            super(context, R.layout.bga_pp_item_nine_photo);
            mImageWidth = BGAPhotoPickerUtil.getScreenWidth(context) / 6;
            mImageHeight = mImageWidth;
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper helper) {
            helper.setItemChildClickListener(R.id.iv_item_nine_photo_photo);
            helper.setItemChildLongClickListener(R.id.iv_item_nine_photo_photo);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            BGAImage.displayImage(helper.getImageView(R.id.iv_item_nine_photo_photo), model, R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, mImageWidth, mImageHeight, null);
        }
    }

    public interface Delegate {
        void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);

        boolean onLongClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);
    }
}

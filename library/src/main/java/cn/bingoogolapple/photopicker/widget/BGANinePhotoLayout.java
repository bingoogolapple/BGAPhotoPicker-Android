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
public class BGANinePhotoLayout extends FrameLayout implements BGAOnItemChildClickListener, BGAOnItemChildLongClickListener, View.OnClickListener, View.OnLongClickListener {
    private PhotoAdapter mPhotoAdapter;
    private ImageView mPhotoIv;
    private BGAHeightWrapGridView mPhotoGv;
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
        mPhotoIv = new ImageView(context);
        mPhotoIv.setClickable(true);
        mPhotoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPhotoIv.setOnClickListener(this);
        mPhotoIv.setOnLongClickListener(this);

        mPhotoGv = new BGAHeightWrapGridView(context);
        int spacing = context.getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider);
        mPhotoGv.setHorizontalSpacing(spacing);
        mPhotoGv.setVerticalSpacing(spacing);
        mPhotoGv.setNumColumns(3);
        mPhotoAdapter = new PhotoAdapter(context);
        mPhotoAdapter.setOnItemChildClickListener(this);
        mPhotoAdapter.setOnItemChildLongClickListener(this);
        mPhotoGv.setAdapter(mPhotoAdapter);

        addView(mPhotoIv);
        addView(mPhotoGv);
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.iv_item_nine_photo_photo) {
            mCurrentClickItemPosition = position;
            if (mDelegate != null) {
                mDelegate.onClickNinePhotoItem(this, childView, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getDatas());
            }
        }
    }

    @Override
    public boolean onItemChildLongClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.iv_item_nine_photo_photo) {
            mCurrentClickItemPosition = position;
            if (mDelegate != null) {
                return mDelegate.onLongClickNinePhotoItem(this, childView, position, mPhotoAdapter.getItem(position), mPhotoAdapter.getDatas());
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getDatas());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            return mDelegate.onLongClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getDatas());
        }
        return false;
    }

    public void setData(final ArrayList<String> photos) {
        int itemWidth = BGAPhotoPickerUtil.getScreenWidth(getContext()) / 4;
        if (photos.size() == 0) {
            setVisibility(GONE);
        } else if (photos.size() == 1) {
            setVisibility(VISIBLE);
            mPhotoGv.setVisibility(GONE);
            mPhotoAdapter.setDatas(photos);
            mPhotoIv.setVisibility(VISIBLE);

            ViewGroup.LayoutParams layoutParams = mPhotoIv.getLayoutParams();
            layoutParams.width = itemWidth * 2;
            layoutParams.height = itemWidth * 2;

            BGAImage.displayImage(mPhotoIv, photos.get(0), R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, itemWidth, itemWidth, null);
        } else {
            setVisibility(VISIBLE);
            mPhotoIv.setVisibility(GONE);
            mPhotoGv.setVisibility(VISIBLE);

            ViewGroup.LayoutParams layoutParams = mPhotoGv.getLayoutParams();
            if (photos.size() == 2) {
                mPhotoGv.setNumColumns(2);
                layoutParams.width = itemWidth * 2;
                layoutParams.height = itemWidth * 1;
            } else if (photos.size() == 4) {
                mPhotoGv.setNumColumns(2);
                layoutParams.width = itemWidth * 2;
                layoutParams.height = itemWidth * 2;
            } else {
                mPhotoGv.setNumColumns(3);
                layoutParams.width = itemWidth * 3;
                layoutParams.height = itemWidth * 3;
            }
            mPhotoGv.setLayoutParams(layoutParams);
            mPhotoAdapter.setDatas(photos);
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getDatas();
    }

    public int getItemCount() {
        return mPhotoAdapter.getCount();
    }

    public String getCurrentClickItem() {
        return mPhotoAdapter.getItem(mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return mCurrentClickItemPosition;
    }

    private static class PhotoAdapter extends BGAAdapterViewAdapter<String> {
        private int mImageWidth;
        private int mImageHeight;

        public PhotoAdapter(Context context) {
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

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
package cn.bingoogolapple.photopicker.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/8 下午2:41
 * 描述:
 */
public class BGANinePhotoLayout extends FrameLayout implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener, View.OnLongClickListener {
    private PhotoAdapter mPhotoAdapter;
    private BGAImageView mPhotoIv;
    private BGAHeightWrapGridView mPhotoGv;
    private Delegate mDelegate;
    private int mCurrentClickItemPosition;
    private Activity mActivity;
    private int mItemCornerRadius;
    private boolean mIsShowAsLargeWhenOnlyOne;

    public BGANinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mIsShowAsLargeWhenOnlyOne = true;

        initAttrs(context, attrs);

        mPhotoIv = new BGAImageView(context);
        mPhotoIv.setClickable(true);
        mPhotoIv.setOnClickListener(this);
        mPhotoIv.setOnLongClickListener(this);

        mPhotoGv = new BGAHeightWrapGridView(context);
        int spacing = context.getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider);
        mPhotoGv.setHorizontalSpacing(spacing);
        mPhotoGv.setVerticalSpacing(spacing);
        mPhotoGv.setNumColumns(3);
        mPhotoGv.setOnItemClickListener(this);
        mPhotoGv.setOnItemLongClickListener(this);
        mPhotoAdapter = new PhotoAdapter(context);
        mPhotoGv.setAdapter(mPhotoAdapter);

        addView(mPhotoIv, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mPhotoGv);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGANinePhotoLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGANinePhotoLayout_bga_npl_isShowAsLargeWhenOnlyOne) {
            mIsShowAsLargeWhenOnlyOne = typedArray.getBoolean(attr, mIsShowAsLargeWhenOnlyOne);
        } else if (attr == R.styleable.BGANinePhotoLayout_bga_npl_itemCornerRadius) {
            mItemCornerRadius = typedArray.getDimensionPixelSize(attr, 0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mCurrentClickItemPosition = position;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        mCurrentClickItemPosition = position;
        if (mDelegate != null) {
            return mDelegate.onLongClickNinePhotoItem(this, view, position, mPhotoAdapter.getItem(position), mPhotoAdapter.getData());
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            return mDelegate.onLongClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
        return false;
    }

    public void init(Activity activity) {
        mActivity = activity;
    }

    /**
     * 设置当只有一张图片时，是否显示成大图
     *
     * @param showAsLargeWhenOnlyOne
     */
    public void setShowAsLargeWhenOnlyOne(boolean showAsLargeWhenOnlyOne) {
        mIsShowAsLargeWhenOnlyOne = showAsLargeWhenOnlyOne;
    }

    /**
     * 设置 Item 条目圆角尺寸，默认为 0dp
     *
     * @param itemCornerRadius
     */
    public void setItemCornerRadius(int itemCornerRadius) {
        mItemCornerRadius = itemCornerRadius;
    }

    public void setData(ArrayList<String> photos) {
        if (mActivity == null) {
            if (getContext() instanceof Activity) {
                mActivity = (Activity) getContext();
            } else {
                throw new RuntimeException("请先调用 " + BGANinePhotoLayout.class.getSimpleName() + " 的 init 方法进行初始化");
            }
        }

        int itemWidth = BGAPhotoPickerUtil.getScreenWidth(getContext()) / 4;
        if (photos.size() == 0) {
            setVisibility(GONE);
        } else if (photos.size() == 1 && mIsShowAsLargeWhenOnlyOne) {
            setVisibility(VISIBLE);
            mPhotoGv.setVisibility(GONE);
            mPhotoAdapter.setData(photos);
            mPhotoIv.setVisibility(VISIBLE);

            mPhotoIv.setMaxWidth(itemWidth * 2);
            mPhotoIv.setMaxHeight(itemWidth * 2);

            if (mItemCornerRadius > 0) {
                mPhotoIv.setCornerRadius(mItemCornerRadius);
            }

            BGAImage.displayImage(mActivity, mPhotoIv, photos.get(0), R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, itemWidth * 2, itemWidth * 2, null);
        } else {
            setVisibility(VISIBLE);
            mPhotoIv.setVisibility(GONE);
            mPhotoGv.setVisibility(VISIBLE);

            ViewGroup.LayoutParams layoutParams = mPhotoGv.getLayoutParams();

            if (photos.size() == 1) {
                mPhotoGv.setNumColumns(1);
                layoutParams.width = itemWidth * 1;
            } else if (photos.size() == 2) {
                mPhotoGv.setNumColumns(2);
                layoutParams.width = itemWidth * 2;
            } else if (photos.size() == 4) {
                mPhotoGv.setNumColumns(2);
                layoutParams.width = itemWidth * 2;
            } else {
                mPhotoGv.setNumColumns(3);
                layoutParams.width = itemWidth * 3;
            }

            mPhotoGv.setLayoutParams(layoutParams);
            mPhotoAdapter.setData(photos);
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
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

    private class PhotoAdapter extends BGAAdapterViewAdapter<String> {
        private int mImageWidth;
        private int mImageHeight;

        public PhotoAdapter(Context context) {
            super(context, R.layout.bga_pp_item_nine_photo);
            mImageWidth = BGAPhotoPickerUtil.getScreenWidth(context) / 6;
            mImageHeight = mImageWidth;
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            if (mItemCornerRadius > 0) {
                BGAImageView imageView = helper.getView(R.id.iv_item_nine_photo_photo);
                imageView.setCornerRadius(mItemCornerRadius);
            }

            BGAImage.displayImage(mActivity, helper.getImageView(R.id.iv_item_nine_photo_photo), model, R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, mImageWidth, mImageHeight, null);
        }
    }

    public interface Delegate {
        void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);

        boolean onLongClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);
    }

    public abstract static class SimpleDelegate implements Delegate {
        @Override
        public boolean onLongClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
            return false;
        }
    }
}
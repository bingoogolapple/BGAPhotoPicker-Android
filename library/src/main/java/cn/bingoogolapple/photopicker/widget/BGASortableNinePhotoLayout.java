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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewHolder;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/8 下午4:51
 * 描述:拖拽排序九宫格图片控件
 */
public class BGASortableNinePhotoLayout extends RecyclerView implements BGAOnItemChildClickListener, BGAOnRVItemClickListener {
    private PhotoAdapter mPhotoAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Delegate mDelegate;
    private GridLayoutManager mGridLayoutManager;

    private boolean mIsPlusSwitchOpened;
    private boolean mIsSortable;
    private int mDeleteDrawableResId;
    private boolean mIsDeleteDrawableOverlapQuarter;
    private int mPhotoTopRightMargin;
    private int mMaxItemCount;
    private int mItemSpanCount;
    private int mPlusDrawableResId;
    private int mItemCornerRadius;
    private Activity mActivity;

    public BGASortableNinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultAttrs();
        initCustomAttrs(context, attrs);
        afterInitDefaultAndCustomAttrs();
    }

    private void initDefaultAttrs() {
        mIsPlusSwitchOpened = true;
        mIsSortable = true;
        mDeleteDrawableResId = R.mipmap.bga_pp_ic_delete;
        mIsDeleteDrawableOverlapQuarter = false;
        mMaxItemCount = 9;
        mItemSpanCount = 3;
        mPlusDrawableResId = R.mipmap.bga_pp_ic_plus;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGASortableNinePhotoLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_isPlusSwitchOpened) {
            mIsPlusSwitchOpened = typedArray.getBoolean(attr, mIsPlusSwitchOpened);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_isSortable) {
            mIsSortable = typedArray.getBoolean(attr, mIsSortable);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_deleteDrawable) {
            mDeleteDrawableResId = typedArray.getResourceId(attr, mDeleteDrawableResId);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_isDeleteDrawableOverlapQuarter) {
            mIsDeleteDrawableOverlapQuarter = typedArray.getBoolean(attr, mIsDeleteDrawableOverlapQuarter);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_maxItemCount) {
            mMaxItemCount = typedArray.getInteger(attr, mMaxItemCount);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_itemSpanCount) {
            mItemSpanCount = typedArray.getInteger(attr, mItemSpanCount);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_plusDrawable) {
            mPlusDrawableResId = typedArray.getResourceId(attr, mPlusDrawableResId);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_itemCornerRadius) {
            mItemCornerRadius = typedArray.getDimensionPixelSize(attr, 0);
        }
    }

    private void afterInitDefaultAndCustomAttrs() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(this);

        mGridLayoutManager = new GridLayoutManager(getContext(), mItemSpanCount);
        setLayoutManager(mGridLayoutManager);
        addItemDecoration(new BGASpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider)));

        calculatePhotoTopRightMargin();

        mPhotoAdapter = new PhotoAdapter(this);
        mPhotoAdapter.setOnItemChildClickListener(this);
        mPhotoAdapter.setOnRVItemClickListener(this);
        setAdapter(mPhotoAdapter);
    }

    public void init(Activity activity) {
        mActivity = activity;
        updateHeight();
    }

    /**
     * 设置是否可拖拽排序
     *
     * @param isSortable
     */
    public void setIsSortable(boolean isSortable) {
        mIsSortable = isSortable;
    }

    /**
     * 设置删除按钮图片资源id
     *
     * @param deleteDrawableResId
     */
    public void setDeleteDrawableResId(@DrawableRes int deleteDrawableResId) {
        mDeleteDrawableResId = deleteDrawableResId;
        calculatePhotoTopRightMargin();
    }

    /**
     * 设置删除按钮是否重叠四分之一，默认值为 false
     *
     * @param isDeleteDrawableOverlapQuarter
     */
    public void setIsDeleteDrawableOverlapQuarter(boolean isDeleteDrawableOverlapQuarter) {
        mIsDeleteDrawableOverlapQuarter = isDeleteDrawableOverlapQuarter;
        calculatePhotoTopRightMargin();
    }

    /**
     * 计算图片右上角 margin
     */
    private void calculatePhotoTopRightMargin() {
        if (mIsDeleteDrawableOverlapQuarter) {
            int deleteDrawableWidth = BitmapFactory.decodeResource(getResources(), mDeleteDrawableResId).getWidth();
            int deleteDrawablePadding = getResources().getDimensionPixelOffset(R.dimen.bga_pp_size_delete_padding);
            mPhotoTopRightMargin = deleteDrawablePadding + deleteDrawableWidth / 2;
        } else {
            mPhotoTopRightMargin = 0;
        }
    }

    /**
     * 设置可选择图片的总张数,默认值为9
     *
     * @param maxItemCount
     */
    public void setMaxItemCount(int maxItemCount) {
        mMaxItemCount = maxItemCount;
    }

    /**
     * 获取选择的图片的最大张数
     *
     * @return
     */
    public int getMaxItemCount() {
        return mMaxItemCount;
    }

    /**
     * 设置列数,默认值为3
     *
     * @param itemSpanCount
     */
    public void setItemSpanCount(int itemSpanCount) {
        mItemSpanCount = itemSpanCount;
        mGridLayoutManager.setSpanCount(mItemSpanCount);
    }

    /**
     * 设置添加按钮图片
     *
     * @param plusDrawableResId
     */
    public void setPlusDrawableResId(@DrawableRes int plusDrawableResId) {
        mPlusDrawableResId = plusDrawableResId;
    }

    /**
     * 设置 Item 条目圆角尺寸，默认为 0dp
     *
     * @param itemCornerRadius
     */
    public void setItemCornerRadius(int itemCornerRadius) {
        mItemCornerRadius = itemCornerRadius;
    }

    private void initActivity() {
        if (mActivity == null) {
            if (getContext() instanceof Activity) {
                mActivity = (Activity) getContext();
            } else {
                throw new RuntimeException("请先调用 " + BGASortableNinePhotoLayout.class.getSimpleName() + " 的 init 方法进行初始化");
            }
        }
    }

    /**
     * 设置图片路径数据集合
     *
     * @param photos
     */
    public void setData(ArrayList<String> photos) {
        initActivity();

        mPhotoAdapter.setData(photos);
        updateHeight();
    }

    /**
     * 在集合尾部添加更多数据集合
     *
     * @param photos
     */
    public void addMoreData(ArrayList<String> photos) {
        initActivity();

        if (photos != null) {
            mPhotoAdapter.getData().addAll(photos);
            mPhotoAdapter.notifyDataSetChanged();
            updateHeight();
        }
    }

    /**
     * 在集合的尾部添加一条数据
     *
     * @param photo
     */
    public void addLastItem(String photo) {
        initActivity();

        if (!TextUtils.isEmpty(photo)) {
            mPhotoAdapter.addLastItem(photo);
            updateHeight();
        }
    }

    private void updateHeight() {
        if (mPhotoAdapter.getItemCount() > 0 && mPhotoAdapter.getItemCount() < mItemSpanCount) {
            mGridLayoutManager.setSpanCount(mPhotoAdapter.getItemCount());
        } else {
            mGridLayoutManager.setSpanCount(mItemSpanCount);
        }
        int itemWidth = BGAPhotoPickerUtil.getScreenWidth(getContext()) / (mItemSpanCount + 1);
        int width = itemWidth * mGridLayoutManager.getSpanCount();
        int height = 0;
        if (mPhotoAdapter.getItemCount() != 0) {
            int rowCount = (mPhotoAdapter.getItemCount() - 1) / mGridLayoutManager.getSpanCount() + 1;
            height = itemWidth * rowCount;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    /**
     * 获取图片路劲数据集合
     *
     * @return
     */
    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
    }

    /**
     * 删除指定索引位置的图片
     *
     * @param position
     */
    public void removeItem(int position) {
        mPhotoAdapter.removeItem(position);
        updateHeight();
    }

    /**
     * 获取图片总数
     *
     * @return
     */
    public int getItemCount() {
        return mPhotoAdapter.getData().size();
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (mDelegate != null) {
            mDelegate.onClickDeleteNinePhotoItem(this, childView, position, mPhotoAdapter.getItem(position), (ArrayList<String>) mPhotoAdapter.getData());
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (mPhotoAdapter.isPlusItem(position)) {
            if (mDelegate != null) {
                mDelegate.onClickAddNinePhotoItem(this, itemView, position, (ArrayList<String>) mPhotoAdapter.getData());
            }
        } else {
            if (mDelegate != null && ViewCompat.getScaleX(itemView) <= 1.0f) {
                mDelegate.onClickNinePhotoItem(this, itemView, position, mPhotoAdapter.getItem(position), (ArrayList<String>) mPhotoAdapter.getData());
            }
        }
    }

    public void setIsPlusSwitchOpened(boolean isPlusSwitchOpened) {
        mIsPlusSwitchOpened = isPlusSwitchOpened;
        updateHeight();
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    private class PhotoAdapter extends BGARecyclerViewAdapter<String> {
        private int mImageWidth;
        private int mImageHeight;

        public PhotoAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.bga_pp_item_nine_photo);
            mImageWidth = BGAPhotoPickerUtil.getScreenWidth(recyclerView.getContext()) / 6;
            mImageHeight = mImageWidth;
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
            helper.setItemChildClickListener(R.id.iv_item_nine_photo_flag);
        }

        @Override
        public int getItemCount() {
            if (mIsPlusSwitchOpened && super.getItemCount() < mMaxItemCount) {
                return super.getItemCount() + 1;
            }

            return super.getItemCount();
        }

        @Override
        public String getItem(int position) {
            if (isPlusItem(position)) {
                return null;
            }

            return super.getItem(position);
        }

        public boolean isPlusItem(int position) {
            return mIsPlusSwitchOpened && super.getItemCount() < mMaxItemCount && position == getItemCount() - 1;
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            MarginLayoutParams params = (MarginLayoutParams) helper.getView(R.id.iv_item_nine_photo_photo).getLayoutParams();
            params.setMargins(0, mPhotoTopRightMargin, mPhotoTopRightMargin, 0);

            if (mItemCornerRadius > 0) {
                BGAImageView imageView = helper.getView(R.id.iv_item_nine_photo_photo);
                imageView.setCornerRadius(mItemCornerRadius);
            }

            if (isPlusItem(position)) {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.GONE);
                helper.setImageResource(R.id.iv_item_nine_photo_photo, mPlusDrawableResId);
            } else {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.VISIBLE);
                helper.setImageResource(R.id.iv_item_nine_photo_flag, mDeleteDrawableResId);
                BGAImage.displayImage(mActivity, helper.getImageView(R.id.iv_item_nine_photo_photo), model, R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, mImageWidth, mImageHeight, null);
            }
        }
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return mIsSortable && mPhotoAdapter.getData().size() > 1;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            if (mPhotoAdapter.isPlusItem(viewHolder.getAdapterPosition())) {
                dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
            }
            int swipeFlags = ACTION_STATE_IDLE;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType() || mPhotoAdapter.isPlusItem(target.getAdapterPosition())) {
                return false;
            }
            mPhotoAdapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ACTION_STATE_IDLE) {
                ViewCompat.setScaleX(viewHolder.itemView, 1.2f);
                ViewCompat.setScaleY(viewHolder.itemView, 1.2f);
                ((BGARecyclerViewHolder) viewHolder).getViewHolderHelper().getImageView(R.id.iv_item_nine_photo_photo).setColorFilter(getResources().getColor(R.color.bga_pp_photo_selected_mask));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            ViewCompat.setScaleX(viewHolder.itemView, 1.0f);
            ViewCompat.setScaleY(viewHolder.itemView, 1.0f);
            ((BGARecyclerViewHolder) viewHolder).getViewHolderHelper().getImageView(R.id.iv_item_nine_photo_photo).setColorFilter(null);
            super.clearView(recyclerView, viewHolder);
        }
    }

    public interface Delegate {
        void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models);

        void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models);

        void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models);
    }
}
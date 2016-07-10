package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/8 下午4:51
 * 描述:
 */
public class BGASortableNinePhotoLayout extends RecyclerView implements BGAOnItemChildClickListener, BGAOnRVItemClickListener {
    private static final int MAX_ITEM_COUNT = 9;
    private static final int MAX_SPAN_COUNT = 3;
    private PhotoAdapter mPhotoAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Delegate mDelegate;
    private GridLayoutManager mGridLayoutManager;
    private boolean mIsPlusSwitchOpened = true;
    private boolean mIsSortable = true;

    public BGASortableNinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOverScrollMode(OVER_SCROLL_NEVER);

        initAttrs(context, attrs);

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(this);

        mGridLayoutManager = new GridLayoutManager(context, MAX_SPAN_COUNT);
        setLayoutManager(mGridLayoutManager);
        addItemDecoration(new BGASpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider)));

        mPhotoAdapter = new PhotoAdapter(this);
        mPhotoAdapter.setOnItemChildClickListener(this);
        mPhotoAdapter.setOnRVItemClickListener(this);
        setAdapter(mPhotoAdapter);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGASortableNinePhotoLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_isPlusSwitchOpened) {
            mIsPlusSwitchOpened = typedArray.getBoolean(attr, mIsPlusSwitchOpened);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_isSortable) {
            mIsSortable = typedArray.getBoolean(attr, mIsSortable);
        }
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
     * 设置图片路径数据集合
     *
     * @param photos
     */
    public void setData(ArrayList<String> photos) {
        mPhotoAdapter.setDatas(photos);
        updateHeight();
    }

    private void updateHeight() {
        if (mPhotoAdapter.getItemCount() > 0 && mPhotoAdapter.getItemCount() < MAX_SPAN_COUNT) {
            mGridLayoutManager.setSpanCount(mPhotoAdapter.getItemCount());
        } else {
            mGridLayoutManager.setSpanCount(MAX_SPAN_COUNT);
        }
        int itemWidth = BGAPhotoPickerUtil.getScreenWidth(getContext()) / (MAX_SPAN_COUNT + 1);
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
        return (ArrayList<String>) mPhotoAdapter.getDatas();
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
        return mPhotoAdapter.getDatas().size();
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (mDelegate != null) {
            mDelegate.onClickDeleteNinePhotoItem(this, childView, position, mPhotoAdapter.getItem(position), (ArrayList<String>) mPhotoAdapter.getDatas());
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (mPhotoAdapter.isPlusItem(position)) {
            if (mDelegate != null) {
                mDelegate.onClickAddNinePhotoItem(this, itemView, position, (ArrayList<String>) mPhotoAdapter.getDatas());
            }
        } else {
            if (mDelegate != null && ViewCompat.getScaleX(itemView) <= 1.0f) {
                mDelegate.onClickNinePhotoItem(this, itemView, position, mPhotoAdapter.getItem(position), (ArrayList<String>) mPhotoAdapter.getDatas());
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
        protected void setItemChildListener(BGAViewHolderHelper helper) {
            helper.setItemChildClickListener(R.id.iv_item_nine_photo_flag);
        }

        @Override
        public int getItemCount() {
            if (mIsPlusSwitchOpened && super.getItemCount() < MAX_ITEM_COUNT) {
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
            return mIsPlusSwitchOpened && super.getItemCount() < MAX_ITEM_COUNT && position == getItemCount() - 1;
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            if (isPlusItem(position)) {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.GONE);
                helper.setImageResource(R.id.iv_item_nine_photo_photo, R.mipmap.bga_pp_ic_plus);
            } else {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.VISIBLE);
                BGAImage.displayImage(helper.getImageView(R.id.iv_item_nine_photo_photo), model, R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, mImageWidth, mImageHeight, null);
            }
        }
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return mIsSortable && mPhotoAdapter.getDatas().size() > 1;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (mPhotoAdapter.isPlusItem(viewHolder.getAdapterPosition())) {
                return ItemTouchHelper.ACTION_STATE_IDLE;
            }

            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
            int swipeFlags = dragFlags;
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
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
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
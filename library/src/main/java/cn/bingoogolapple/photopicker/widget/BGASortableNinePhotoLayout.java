package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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
    private PhotoAdapter mPhotoAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Delegate mDelegate;
    private GridLayoutManager mGridLayoutManager;
    private boolean mEditable = true;
    private int mCurrentClickItemPosition;

    public BGASortableNinePhotoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_NEVER);

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(this);

        mGridLayoutManager = new GridLayoutManager(context, 3);
        setLayoutManager(mGridLayoutManager);
        addItemDecoration(new BGASpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.bga_pp_size_photo_divider)));

        mPhotoAdapter = new PhotoAdapter(this);
        mPhotoAdapter.setOnItemChildClickListener(this);
        mPhotoAdapter.setOnRVItemClickListener(this);
        setAdapter(mPhotoAdapter);
    }

    public void setEditable(boolean editable) {
        mEditable = editable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    public void setData(ArrayList<String> photos) {
        mPhotoAdapter.setDatas(photos);
        updateHeight();
    }

    private void updateHeight() {
        if (mPhotoAdapter.getItemCount() > 0 && mPhotoAdapter.getItemCount() < 3) {
            mGridLayoutManager.setSpanCount(mPhotoAdapter.getItemCount());
        } else {
            mGridLayoutManager.setSpanCount(3);
        }
        int itemWidth = BGAPhotoPickerUtil.getScreenWidth(getContext()) / 4;
        int width = itemWidth * mGridLayoutManager.getSpanCount();
        int height = 0;
        if (getItemCount() != 0) {
            int rowCount = (getItemCount() - 1) / mGridLayoutManager.getSpanCount() + 1;
            height = itemWidth * rowCount;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getDatas();
    }

    public void removeItem(int position) {
        mPhotoAdapter.removeItem(position);
        updateHeight();
    }

    public int getItemCount() {
        return mPhotoAdapter.getItemCount();
    }

    public String getCurrentClickItem() {
        return mPhotoAdapter.getItem(mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return mCurrentClickItemPosition;
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (mDelegate != null) {
            mDelegate.onClickDeleteNinePhotoItem(this, childView, position, mPhotoAdapter.getItem(position), mPhotoAdapter.getDatas());
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        mCurrentClickItemPosition = position;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, itemView, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getDatas());
        }
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
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            if (mEditable) {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.VISIBLE);
            } else {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.GONE);
            }

            BGAImage.displayImage(helper.getImageView(R.id.iv_item_nine_photo_photo), model, R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, mImageWidth, mImageHeight, null);
        }
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return mEditable && mPhotoAdapter.getItemCount() > 1;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
            int swipeFlags = dragFlags;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
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
        void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, List<String> models);

        void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, List<String> models);
    }
}
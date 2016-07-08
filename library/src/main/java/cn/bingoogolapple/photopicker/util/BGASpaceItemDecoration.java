package cn.bingoogolapple.photopicker.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午7:22
 * 描述:
 */
public class BGASpaceItemDecoration extends RecyclerView.ItemDecoration {
    public static final int SPAN_COUNT = 3;
    private int mSpace;

    public BGASpaceItemDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.top = mSpace;
        outRect.bottom = mSpace;
    }
}
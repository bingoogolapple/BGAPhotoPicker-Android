package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/9/8 下午1:05
 * 描述:
 */
public class BGARVOnScrollListener extends RecyclerView.OnScrollListener {
    private Activity mActivity;

    public BGARVOnScrollListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            BGAImage.resume(mActivity);
        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            BGAImage.pause(mActivity);
        }
    }
}

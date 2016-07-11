package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/25 下午4:30
 * 描述:
 */
public interface BGAImageLoader {

    void displayImage(Activity activity, ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, DisplayDelegate delegate);

    void downloadImage(Activity activity, String path, DownloadDelegate delegate);

    interface DisplayDelegate {
        void onSuccess(View view, String path);
    }

    interface DownloadDelegate {
        void onSuccess(String path, Bitmap bitmap);

        void onFailed(String path);
    }
}
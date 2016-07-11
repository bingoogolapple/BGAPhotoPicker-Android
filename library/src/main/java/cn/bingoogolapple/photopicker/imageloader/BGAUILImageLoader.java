package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/25 下午5:39
 * 描述:
 */
public class BGAUILImageLoader implements BGAImageLoader {

    private void initImageLoader(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext()).threadPoolSize(3).defaultDisplayImageOptions(options).build();
            ImageLoader.getInstance().init(config);
        }
    }

    @Override
    public void displayImage(Activity activity, ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final DisplayDelegate delegate) {
        initImageLoader(activity);

        if (path == null) {
            path = "";
        }

        if (!path.startsWith("http") && !path.startsWith("file")) {
            path = "file://" + path;
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingResId)
                .showImageOnFail(failResId)
                .cacheInMemory(true)
                .build();
        ImageSize imageSize = new ImageSize(width, height);

        ImageLoader.getInstance().displayImage(path, new ImageViewAware(imageView), options, imageSize, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (delegate != null) {
                    delegate.onSuccess(view, imageUri);
                }
            }
        }, null);
    }

    @Override
    public void downloadImage(Activity activity, String path, final DownloadDelegate delegate) {
        initImageLoader(activity);

        if (path == null) {
            path = "";
        }

        if (!path.startsWith("http") && !path.startsWith("file")) {
            path = "file://" + path;
        }

        ImageLoader.getInstance().loadImage(path, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                if (delegate != null) {
                    delegate.onSuccess(imageUri, loadedImage);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (delegate != null) {
                    delegate.onFailed(imageUri);
                }
            }
        });
    }

}
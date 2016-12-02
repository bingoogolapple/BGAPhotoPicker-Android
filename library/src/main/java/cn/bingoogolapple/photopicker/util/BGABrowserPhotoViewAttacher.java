/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package cn.bingoogolapple.photopicker.util;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/15 上午10:35
 * 描述:
 */
public class BGABrowserPhotoViewAttacher extends PhotoViewAttacher {

    public BGABrowserPhotoViewAttacher(ImageView imageView) {
        super(imageView);
    }

    private boolean isSetTopCrop = false;

    /**
     * 必须重写此方法，防止其他函数覆盖，导致setTopCrop不成功
     *
     * @param d - Drawable being displayed
     */
    @Override
    protected void updateBaseMatrix(Drawable d) {
        if (isSetTopCrop) {
            setTopCrop(d);
        } else {
            super.updateBaseMatrix(d);
        }
    }

    public void setIsSetTopCrop(boolean isSetTopCrop) {
        this.isSetTopCrop = isSetTopCrop;
    }

    public void setUpdateBaseMatrix() {
        ImageView view = getImageView();
        if (view == null) return;
        updateBaseMatrix(view.getDrawable());
    }

    private void setTopCrop(Drawable d) {
        ImageView imageView = getImageView();
        if (null == imageView || null == d) {
            return;
        }
        final float viewWidth = getImageViewWidth(imageView);
        final float viewHeight = getImageViewHeight(imageView);
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();

        Matrix matrix = new Matrix();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;
        float scale = Math.max(widthScale, heightScale);
        matrix.postScale(scale, scale);
        matrix.postTranslate(0, 0);
        updateBaseMatrix(matrix);
    }

}
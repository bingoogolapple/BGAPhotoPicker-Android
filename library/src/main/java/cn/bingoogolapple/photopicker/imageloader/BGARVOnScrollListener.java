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

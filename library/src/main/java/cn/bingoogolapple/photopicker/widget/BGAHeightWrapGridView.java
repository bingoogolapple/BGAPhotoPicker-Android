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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/8 下午2:43
 * 描述:
 */
public class BGAHeightWrapGridView extends GridView {

    public BGAHeightWrapGridView(Context context) {
        this(context, null);
    }

    public BGAHeightWrapGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public BGAHeightWrapGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setSelector(android.R.color.transparent);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
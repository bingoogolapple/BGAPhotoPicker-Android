:running:BGAPhotoPicker-Android:running:
============

[兼容 Glide3](https://github.com/bingoogolapple/BGAPhotoPicker-Android/issues/118)

## 目录

* [功能介绍](#功能介绍)
* [效果图与示例 apk](#效果图与示例-apk)
* [使用](#使用)
* [自定义属性说明](#自定义属性说明)
* [关于我](#关于我)
* [打赏支持](#打赏支持)
* [License](#license)

## 功能介绍

将 [MeiqiaSDK-Android](https://github.com/Meiqia/MeiqiaSDK-Android) 里的图库单独抽出来开源，将其中的 GridView、ListView 和 RelativeLayout 换成 RecyclerView 和 Toolbar，方便在以后的项目中直接依赖使用。Demo 中模仿了微信朋友圈的部分功能，详细用法请查看 Demo。希望能该库帮正在做这几个功能的猿友节省开发时间。

- [x] 单图选择
- [x] 多图选择
- [x] 拍照选择
- [x] 图片选择预览（支持微博长图）、缩放查看
- [x] 图片预览（支持微博长图）、缩放查看
- [x] 支持 glide、picasso、universal-image-loader、xutils 图片加载库
- [x] 支持配置列表滚动时是否暂停加载图片，列表停止滚动时恢复加载图片(用 xutils 作为图片加载库时该配置无效)
- [x] 正方形、圆形头像、带边框的圆形头像控件
- [x] 朋友圈列表界面的九宫格图片控件
- [x] 发布朋友圈界面的可拖拽排序的九宫格图片控件
- [x] 覆盖相应的资源文件来定制界面
- [x] BGAPhotoHelper 图片裁剪、拍照

## 效果图与示例 apk

| Demo | 自定义 |
| ------------ | ------------- |
| ![1](https://cloud.githubusercontent.com/assets/8949716/17476407/7d54831e-5d92-11e6-83d0-4049039e0899.gif) | ![2](https://cloud.githubusercontent.com/assets/8949716/18590019/cde4acdc-7c5f-11e6-8877-b702aba7ae0c.png)  |

| 自定义 | 自定义 |
| ------------ | ------------- |
| ![3](https://user-images.githubusercontent.com/8949716/33283054-99d82070-d3e5-11e7-97a6-d5d2b265f7df.png) | ![4](https://user-images.githubusercontent.com/8949716/33283055-9a10126e-d3e5-11e7-8e12-22a1f0644ac1.png)  |

[点击下载 BGAPhotoPickerDemo.apk](http://fir.im/PhotoPickerDemo) 或扫描下面的二维码安装

![bga_photopicker_qrcode1](https://cloud.githubusercontent.com/assets/8949716/17477053/71e2be30-5d95-11e6-88ae-96f2b8a8a741.png)

## 使用

### 1.添加 Gradle 依赖

* 把 `maven { url 'https://jitpack.io' }` 添加到 root build.gradle 的 repositories 中
* 在 app build.gradle 中添加如下依赖，末尾的「latestVersion」指的是徽章 [![](https://jitpack.io/v/bingoogolapple/BGAPhotoPicker-Android.svg)](https://jitpack.io/#bingoogolapple/BGAPhotoPicker-Android) 里的版本名称，请自行替换

由于需要支持微博长图预览，该库中已经引入了 [PhotoView](https://github.com/chrisbanes/PhotoView) 的源码并进行了修改，所以你的项目中就不要再重复引入 [PhotoView](https://github.com/chrisbanes/PhotoView) 了

```groovy
dependencies {
    // -------------------- 以下4个库是必须依赖的 ----------------------------
    implementation 'com.github.bingoogolapple:BGAPhotoPicker-Android:latestVersion'
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.0.0'
    implementation 'com.github.bingoogolapple:BGABaseAdapter-Android:2.0.1'
    // -------------------- 以上4个库是必须依赖的 ----------------------------

    // 目前支持常见的 4 种图片加载库，必须在下面四个图片加载库中选择一个添加依赖
    implementation 'com.github.bumptech.glide:glide:4.10.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.10.0'
//    implementation 'com.squareup.picasso:picasso:2.5.2'
//    implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
//    implementation 'org.xutils:xutils:3.8.3'
}
```

### 2.接口说明

> BGAPhotoPickerActivity - 选择图片

```Java
@AfterPermissionGranted(PRC_PHOTO_PICKER)
private void choicePhotoWrapper() {
    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    if (EasyPermissions.hasPermissions(this, perms)) {
        // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

        Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                .cameraFileDir(mTakePhotoCb.isChecked() ? takePhotoDir : null) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount()) // 图片选择张数的最大值
                .selectedPhotos(null) // 当前已选中的图片路径集合
                .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                .build();
        startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
    } else {
        EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
    }
}
```

> BGAPhotoPickerActivity - 获取已选择的图片集合

```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
        List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
    }
}
```

> BGAPhotoPickerPreviewActivity - 预览已选择的图片

```Java
Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
        .previewPhotos(models) // 当前预览的图片路径集合
        .selectedPhotos(models) // 当前已选中的图片路径集合
        .maxChooseCount(mPhotosSnpl.getMaxItemCount()) // 图片选择张数的最大值
        .currentPosition(position) // 当前预览图片的位置
        .isFromTakePhoto(false) // 是否是拍完照后跳转过来
        .build();
startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
```

> BGAPhotoPickerPreviewActivity - 获取预览界面已选择的图片集合

```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_PHOTO_PREVIEW) {
        // 在预览界面按返回也会回传预览界面已选择的图片集合
        List<String> selectedPhotos = BGAPhotoPickerPreviewActivity.getSelectedPhotos(data);
    }
}
```

> BGAPhotoPreviewActivity - 图片预览

```Java
@AfterPermissionGranted(PRC_PHOTO_PREVIEW)
private void photoPreviewWrapper() {
    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    if (EasyPermissions.hasPermissions(this, perms)) {
        File downloadDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerDownload");
        BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(this)
                .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能

        if (mCurrentClickNpl.getItemCount() == 1) {
            // 预览单张图片
            photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl.getCurrentClickItem());
        } else if (mCurrentClickNpl.getItemCount() > 1) {
            // 预览多张图片
            photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl.getData())
                    .currentPosition(mCurrentClickNpl.getCurrentClickItemPosition()); // 当前预览图片的索引
        }
        startActivity(photoPreviewIntentBuilder.build());
    } else {
        EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", PRC_PHOTO_PREVIEW, perms);
    }
}
```

### 3.自定义样式

* 可以在你项目的 mipmap-xxhdpi 目录中添加 https://github.com/bingoogolapple/BGAPhotoPicker-Android/tree/master/library/src/main/res/mipmap-xxhdpi 中相应的同名图片文件来替换图片样式
* 可以在你项目的 colors.xml 文件中添加同名的颜色资源来自定义图片选择器的颜色

```xml
<!-- ========================== 自定义 PhotoPicker 颜色 START ========================== -->
<!-- PhotoPicker 状态栏的颜色 -->
<color name="bga_pp_colorPrimaryDark">@color/colorPrimaryDark</color>
<!-- PhotoPicker 导航栏的颜色 -->
<color name="bga_pp_navigationBarColor">@color/navigationBarColor</color>
<!-- PhotoPicker Toolbar 的颜色 -->
<color name="bga_pp_colorPrimary">@color/colorPrimary</color>
<!-- 图片选择器库里所有Activity界面的背景色 -->
<color name="bga_pp_activity_bg">@android:color/white</color>
<!-- 文字颜色 -->
<color name="bga_pp_common_textColor">@android:color/white</color>
<!-- 文件夹名称颜色 -->
<color name="bga_pp_folder_name_textColor">#282828</color>
<!-- 文件夹中照片数量颜色 -->
<color name="bga_pp_folder_count_textColor">#585858</color>
<!-- 图片条目背景色 -->
<color name="bga_pp_photo_item_bg">#303d42</color>
<!-- 拍摄照片文字颜色 -->
<color name="bga_pp_take_photo_textColor">#adb2bb</color>
<!-- 图片处于选中状态时的遮罩层颜色 -->
<color name="bga_pp_photo_selected_mask">#4DFFFFFF</color>
<!-- 右上角确认按钮不可用时的颜色 -->
<color name="bga_pp_btn_confirm_disabled">@android:color/transparent</color>
<!-- 右上角确认按钮可用时的颜色 -->
<color name="bga_pp_btn_confirm_enabled">@android:color/transparent</color>
<!-- 右上角确认按钮按下时的颜色 -->
<color name="bga_pp_btn_confirm_pressed">@android:color/transparent</color>
<!-- 预览界面底部背景色 -->
<color name="bga_pp_preview_bottom_bg">@color/colorPrimary</color>
<!-- 数据加载对话框加载进度条的颜色 -->
<color name="bga_pp_loading_progress_startColor">@android:color/white</color>
<color name="bga_pp_loading_progress_centerColor">@color/colorPrimaryDark</color>
<color name="bga_pp_loading_progress_endColor">@color/colorPrimaryDarkTrans</color>
<!-- ========================== 自定义 PhotoPicker 颜色 END ========================== -->
```

## 自定义属性说明

```xml
<declare-styleable name="BGASortableNinePhotoLayout">
    <!-- 是否显示加号，默认值为 true -->
    <attr name="bga_snpl_plusEnable" format="boolean"/>
    <!-- 是否可拖拽排序，默认值为 true -->
    <attr name="bga_snpl_sortable" format="boolean"/>
    <!-- 删除按钮图片，默认值为 R.mipmap.bga_pp_ic_delete -->
    <attr name="bga_snpl_deleteDrawable" format="reference"/>
    <!-- 可选择图片的总张数,默认值为 9 -->
    <attr name="bga_snpl_maxItemCount" format="integer"/>
    <!-- 列数,默认值为 3 -->
    <attr name="bga_snpl_itemSpanCount" format="integer"/>
    <!-- 删除按钮是否重叠四分之一，默认值为 false -->
    <attr name="bga_snpl_deleteDrawableOverlapQuarter" format="boolean"/>
    <!-- 添加按钮图片，默认值为 R.mipmap.bga_pp_ic_plus -->
    <attr name="bga_snpl_plusDrawable" format="reference"/>
    <!-- Item 条目圆角尺寸，默认值为 0dp -->
    <attr name="bga_snpl_itemCornerRadius" format="dimension|reference"/>
    <!-- Item 间的水平和垂直间距，默认值为 4dp -->
    <attr name="bga_snpl_itemWhiteSpacing" format="dimension|reference"/>
    <!-- 出去九宫格部分的空白区域的尺寸，默认值为 100dp -->
    <attr name="bga_snpl_otherWhiteSpacing" format="dimension|reference"/>
    <!-- 占位图资源，默认值为 R.mipmap.bga_pp_ic_holder_light -->
    <attr name="bga_snpl_placeholderDrawable" format="reference"/>
    <!-- 是否可编辑，默认值为 true -->
    <attr name="bga_snpl_editable" format="boolean"/>
    <!-- item 的尺寸，大于 0dp 时优先级高于 bga_snpl_otherWhiteSpacing，默认值为 0dp -->
    <attr name="bga_snpl_itemWidth" format="dimension|reference"/>
</declare-styleable>

<declare-styleable name="BGANinePhotoLayout">
    <!-- Item 条目圆角尺寸，默认值为 0dp -->
    <attr name="bga_npl_itemCornerRadius" format="dimension|reference"/>
    <!-- 当只有一张图片时，是否显示成大图，默认值为 true -->
    <attr name="bga_npl_showAsLargeWhenOnlyOne" format="boolean"/>
    <!-- Item 间的水平和垂直间距，默认值为 4dp -->
    <attr name="bga_npl_itemWhiteSpacing" format="dimension|reference"/>
    <!-- 出去九宫格部分的空白区域的尺寸，默认值为 100dp -->
    <attr name="bga_npl_otherWhiteSpacing" format="dimension|reference"/>
    <!-- 占位图资源，默认值为 R.mipmap.bga_pp_ic_holder_light -->
    <attr name="bga_npl_placeholderDrawable" format="reference"/>
    <!-- item 的尺寸，优先级高于 bga_npl_otherWhiteSpacing，默认值为 0dp -->
    <attr name="bga_npl_itemWidth" format="dimension|reference"/>
    <!-- 列数,默认值为 3，当该值大于 3 并且数据源里只有四张图片时不会显示成 2 列 -->
    <attr name="bga_npl_itemSpanCount" format="integer"/>
</declare-styleable>

<declare-styleable name="BGAImageView">
    <!-- 默认图片资源，默认值为 null -->
    <attr name="android:src"/>
    <!-- 是否是圆形，默认值为 false -->
    <attr name="bga_iv_circle" format="boolean"/>
    <!-- 圆角矩形的半径，默认值为 0dp -->
    <attr name="bga_iv_cornerRadius" format="reference|dimension"/>
    <!-- 是否是矩形，默认值为 false -->
    <attr name="bga_iv_square" format="boolean"/>
    <!-- 描边的宽度，默认值为 0dp -->
    <attr name="bga_iv_borderWidth" format="reference|dimension"/>
    <!-- 描边的颜色，默认值为 Color.WHITE -->
    <attr name="bga_iv_borderColor" format="reference|color"/>
</declare-styleable>
```

## 详细用法请查看 [Demo](https://github.com/bingoogolapple/BGAPhotoPicker-Android/tree/master/demo):feet:

## 关于我

| 个人主页 | 邮箱 | BGA系列开源库QQ群
| ------------- | ------------ | ------------ |
| <a  href="http://www.bingoogolapple.cn" target="_blank">bingoogolapple.cn</a>  | <a href="mailto:bingoogolapple@gmail.com" target="_blank">bingoogolapple@gmail.com</a> | ![BGA_CODE_CLUB](http://bgashare.bingoogolapple.cn/BGA_CODE_CLUB.png?imageView2/2/w/200) |

## 打赏支持

如果您觉得 BGA 系列开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

如果您目前正打算购买通往墙外的梯子，可以使用我的邀请码「YFQ9Q3B」购买 [Lantern](https://github.com/getlantern/forum)，双方都赠送三个月的专业版使用时间:beers:

<p align="center">
  <img src="http://bgashare.bingoogolapple.cn/bga_pay.png?imageView2/2/w/450" width="450">
</p>

## License

    Copyright 2016 bingoogolapple

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

:running:BGAPhotoPicker-Android:running:
============
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.bingoogolapple/bga-photopicker/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.bingoogolapple/bga-photopicker)

将 [MeiqiaSDK-Android](https://github.com/Meiqia/MeiqiaSDK-Android) 里的图库单独抽出来开源，将其中的 GridView、ListView 和 RelativeLayout 换成 RecyclerView 和 Toolbar，方便在以后的项目中直接依赖使用。Demo 中模仿了微信朋友圈的部分功能，详细用法请查看 Demo。希望能该库帮正在做这几个功能的猿友节省开发时间。

## 如有需要集成「在线客服功能」的猿友，欢迎使用 [美洽](http://meiqia.com)

## 主要功能
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

## 效果图与示例 apk

![bga-photopicker3](https://cloud.githubusercontent.com/assets/8949716/17476407/7d54831e-5d92-11e6-83d0-4049039e0899.gif)![photopickercustom](https://cloud.githubusercontent.com/assets/8949716/18590019/cde4acdc-7c5f-11e6-8877-b702aba7ae0c.png)

[点击下载 BGAPhotoPickerDemo.apk](http://fir.im/PhotoPickerDemo) 或扫描下面的二维码安装

![bga_photopicker_qrcode1](https://cloud.githubusercontent.com/assets/8949716/17477053/71e2be30-5d95-11e6-88ae-96f2b8a8a741.png)

## Gradle 依赖

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.bingoogolapple/bga-photopicker/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.bingoogolapple/bga-photopicker) 「latestVersion」指的是左边这个 maven-central 徽章后面的「数字」，请自行替换。请不要再来问我「latestVersion」是什么了

由于需要支持微博长图预览，该库中已经引入了 [PhotoView](https://github.com/chrisbanes/PhotoView) 的源码并进行了修改，所以你的项目中就不要再重复引入 [PhotoView](https://github.com/chrisbanes/PhotoView) 了

```groovy
dependencies {
    compile 'com.android.support:appcompat-v7:24.0.0'
    compile 'com.android.support:recyclerview-v7:24.0.0'
    compile 'cn.bingoogolapple:bga-adapter:1.1.0@aar'

    compile 'cn.bingoogolapple:bga-photopicker:latestVersion@aar'

    // 必须依赖下面四个图片加载库中的某一个
    compile 'com.github.bumptech.glide:glide:3.7.0'
//    compile 'com.squareup.picasso:picasso:2.5.2'
//    compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
//    compile 'org.xutils:xutils:3.3.36'
}
```

## 接口说明

> BGAPhotoPickerActivity

```java
/**
 * @param context        应用程序上下文
 * @param imageDir       拍照后图片保存的目录。如果传null表示没有拍照功能，如果不为null则具有拍照功能，
 * @param maxChooseCount 图片选择张数的最大值
 * @param selectedImages 当前已选中的图片路径集合，可以传null
 * @param pauseOnScroll  滚动列表时是否暂停加载图片
 * @return
 */
public static Intent newIntent(Context context, File imageDir, int maxChooseCount, ArrayList<String> selectedImages, boolean pauseOnScroll)

/**
 * 获取已选择的图片集合
 *
 * @param intent
 * @return
 */
public static ArrayList<String> getSelectedImages(Intent intent)
```

> BGAPhotoPreviewActivity

```java
/**
 * 获取查看多张图片的intent
 *
 * @param context
 * @param saveImgDir      保存图片的目录，如果传null，则没有保存图片功能
 * @param previewImages   当前预览的图片目录里的图片路径集合
 * @param currentPosition 当前预览图片的位置
 * @return
 */
public static Intent newIntent(Context context, File saveImgDir, ArrayList<String> previewImages, int currentPosition)

/**
 * 获取查看单张图片的intent
 *
 * @param context
 * @param saveImgDir 保存图片的目录，如果传null，则没有保存图片功能
 * @param photoPath  图片路径
 * @return
 */
public static Intent newIntent(Context context, File saveImgDir, String photoPath)
```
## 自定义属性

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
    <!-- item 的尺寸，优先级高于 bga_snpl_otherWhiteSpacing，默认值为 0dp -->
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

## Proguard

```java
## ----------------------------------
##      UIL相关
## ----------------------------------
-keep class com.nostra13.universalimageloader.** { *; }
-keepclassmembers class com.nostra13.universalimageloader.** {*;}
-dontwarn com.nostra13.universalimageloader.**

## ----------------------------------
##      Glide相关
## ----------------------------------
-keep class com.bumptech.glide.Glide { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**

## ----------------------------------
##      Picasso相关
## ----------------------------------
-keep class com.squareup.picasso.Picasso { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**

## ----------------------------------
##      xUtils3相关
## ----------------------------------
-keepattributes Signature,*Annotation*
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers @org.xutils.db.annotation.* class * {*;}
-keepclassmembers @org.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event <methods>;
}
-dontwarn org.xutils.**
```

## 关于我

| 新浪微博 | 个人主页 | 邮箱 | BGA系列开源库QQ群
| ------------ | ------------- | ------------ | ------------ |
| <a href="http://weibo.com/bingoogol" target="_blank">bingoogolapple</a> | <a  href="http://www.bingoogolapple.cn" target="_blank">bingoogolapple.cn</a>  | <a href="mailto:bingoogolapple@gmail.com" target="_blank">bingoogolapple@gmail.com</a> | ![BGA_CODE_CLUB](http://7xk9dj.com1.z0.glb.clouddn.com/BGA_CODE_CLUB.png?imageView2/2/w/200) |

## 打赏支持

如果觉得 BGA 系列开源库对您有用，请随意打赏。如果猿友有打算购买 [Lantern](https://github.com/getlantern/forum)，可以使用我的邀请码「YFQ9Q3B」购买，双方都赠送三个月的专业版使用时间。

<p align="center">
  <img src="http://7xk9dj.com1.z0.glb.clouddn.com/bga_pay.png" width="450">
</p>

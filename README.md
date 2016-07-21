:running:BGAPhotoPicker-Android:running:
============
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.bingoogolapple/bga-photopicker/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.bingoogolapple/bga-photopicker)

将 [MeiqiaSDK-Android](https://github.com/Meiqia/MeiqiaSDK-Android) 里的图库单独抽出来开源，将其中的 GridView、ListView和RelativeLayout 换成 RecyclerView 和 Toolbar，方便在以后的项目中直接依赖使用。Demo 中模仿了微信朋友圈的部分功能，详细用法请查看 Demo。希望能该库帮正在做这几个功能的猿友节省开发时间。

## 如有需要集成「在线客服功能」的猿友，欢迎使用 [美洽](http://meiqia.com)

## 主要功能
- [x] 单图选择
- [x] 多图选择
- [x] 拍照选择
- [x] 图片选择预览（支持微博长图）、缩放查看
- [x] 图片预览（支持微博长图）、缩放查看
- [x] 支持 glide、picasso、universal-image-loader、xutils 图片加载库
- [x] 正方形、圆形头像、带边框的圆形头像控件
- [x] 朋友圈列表界面的九宫格图片控件
- [x] 发布朋友圈界面的可拖拽排序的九宫格图片控件
- [x] 覆盖相应的资源文件来定制界面

## 效果图与示例 apk

![PhotoPicker-Demo](http://7xk9dj.com1.z0.glb.clouddn.com/%40%2Fphotopicker%2Fbga-photopicker3.gif?imageView2/2/w/300)

[点击下载 BGAPhotoPickerDemo.apk](http://fir.im/PhotoPickerDemo) 或扫描下面的二维码安装

![BGAPhotoPickerDemo apk文件二维](http://7xk9dj.com1.z0.glb.clouddn.com//photopicker/BGAPhotoPickerDemo.png)

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
 * @return
 */
public static Intent newIntent(Context context, File imageDir, int maxChooseCount, ArrayList<String> selectedImages)

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

如果觉得 BGA 系列开源库对您有用，请随意打赏。

<p align="center">
  <img src="http://7xk9dj.com1.z0.glb.clouddn.com/bga_pay.png" width="450">
</p>

package cn.bingoogolapple.photopicker.model;

import java.util.ArrayList;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午6:18
 * 描述:
 */
public class BGAImageFolderModel {
    public String name;
    public String coverPath;
    private ArrayList<String> mImages = new ArrayList<>();
    private boolean mTakePhotoEnabled;

    public BGAImageFolderModel(boolean takePhotoEnabled) {
        mTakePhotoEnabled = takePhotoEnabled;
        if (takePhotoEnabled) {
            // 拍照
            mImages.add("");
        }
    }

    public BGAImageFolderModel(String name, String coverPath) {
        this.name = name;
        this.coverPath = coverPath;
    }

    public boolean isTakePhotoEnabled() {
        return mTakePhotoEnabled;
    }

    public void addLastImage(String imagePath) {
        mImages.add(imagePath);
    }

    public ArrayList<String> getImages() {
        return mImages;
    }

    public int getCount() {
        return mTakePhotoEnabled ? mImages.size() - 1 : mImages.size();
    }
}
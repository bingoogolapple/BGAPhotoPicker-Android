package cn.bingoogolapple.photopicker.pw;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.model.BGAImageFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGASquareImageView;


/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午6:19
 * 描述:
 */
public class BGAPhotoFolderPw extends BGABasePopupWindow implements AdapterView.OnItemClickListener {
    public static final int ANIM_DURATION = 300;
    private LinearLayout mRootLl;
    private ListView mContentLv;
    private FolderAdapter mFolderAdapter;
    private Callback mCallback;
    private int mCurrentPosition;

    public BGAPhotoFolderPw(Activity activity, View anchorView, Callback callback) {
        super(activity, R.layout.bga_pp_pw_photo_folder, anchorView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mCallback = callback;
    }

    @Override
    protected void initView() {
        mRootLl = getViewById(R.id.root_ll);
        mContentLv = getViewById(R.id.content_lv);
    }

    @Override
    protected void setListener() {
        mRootLl.setOnClickListener(this);
        mContentLv.setOnItemClickListener(this);
    }

    @Override
    protected void processLogic() {
        setAnimationStyle(android.R.style.Animation);
        setBackgroundDrawable(new ColorDrawable(0x90000000));

        mFolderAdapter = new FolderAdapter();
        mContentLv.setAdapter(mFolderAdapter);
    }

    /**
     * 设置目录数据集合
     *
     * @param datas
     */
    public void setDatas(ArrayList<BGAImageFolderModel> datas) {
        mFolderAdapter.setDatas(datas);
    }

    @Override
    public void show() {
        showAsDropDown(mAnchorView);
        ViewCompat.animate(mContentLv).translationY(-mWindowRootView.getHeight()).setDuration(0).start();
        ViewCompat.animate(mContentLv).translationY(0).setDuration(ANIM_DURATION).start();
        ViewCompat.animate(mRootLl).alpha(0).setDuration(0).start();
        ViewCompat.animate(mRootLl).alpha(1).setDuration(ANIM_DURATION).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCallback != null && mCurrentPosition != position) {
            mCallback.onSelectedFolder(position);
        }
        mCurrentPosition = position;
        dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.root_ll) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        ViewCompat.animate(mContentLv).translationY(-mWindowRootView.getHeight()).setDuration(ANIM_DURATION).start();
        ViewCompat.animate(mRootLl).alpha(1).setDuration(0).start();
        ViewCompat.animate(mRootLl).alpha(0).setDuration(ANIM_DURATION).start();

        if (mCallback != null) {
            mCallback.executeDismissAnim();
        }

        mContentLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                BGAPhotoFolderPw.super.dismiss();
            }
        }, ANIM_DURATION);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    private class FolderAdapter extends BaseAdapter {
        private List<BGAImageFolderModel> mDatas;
        private int mImageWidth;
        private int mImageHeight;

        public FolderAdapter() {
            mDatas = new ArrayList<>();
            mImageWidth = BGAPhotoPickerUtil.getScreenWidth(mActivity) / 10;
            mImageHeight = mImageWidth;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public BGAImageFolderModel getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FolderViewHolder folderViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bga_pp_item_photo_folder, parent, false);
                folderViewHolder = new FolderViewHolder();
                folderViewHolder.photoIv = (BGASquareImageView) convertView.findViewById(R.id.iv_item_photo_folder_photo);
                folderViewHolder.nameTv = (TextView) convertView.findViewById(R.id.tv_item_photo_folder_name);
                folderViewHolder.countTv = (TextView) convertView.findViewById(R.id.tv_item_photo_folder_count);
                convertView.setTag(folderViewHolder);
            } else {
                folderViewHolder = (FolderViewHolder) convertView.getTag();
            }

            BGAImageFolderModel imageFolderModel = getItem(position);
            folderViewHolder.nameTv.setText(imageFolderModel.name);
            folderViewHolder.countTv.setText(String.valueOf(imageFolderModel.getCount()));
            Glide.with(mActivity).load(imageFolderModel.coverPath).placeholder(R.mipmap.bga_pp_ic_holder_light).error(R.mipmap.bga_pp_ic_holder_light).into(folderViewHolder.photoIv);

            return convertView;
        }

        public void setDatas(ArrayList<BGAImageFolderModel> datas) {
            if (datas != null) {
                mDatas = datas;
            } else {
                mDatas.clear();
            }
            notifyDataSetChanged();
        }
    }

    private class FolderViewHolder {
        public BGASquareImageView photoIv;
        public TextView nameTv;
        public TextView countTv;
    }

    public interface Callback {
        void onSelectedFolder(int position);

        void executeDismissAnim();
    }
}
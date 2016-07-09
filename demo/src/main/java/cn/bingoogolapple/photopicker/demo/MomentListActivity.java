package cn.bingoogolapple.photopicker.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;

/**
 * 你自己项目里「可以不继承 BGAPPToolbarActivity」，我在这里继承 BGAPPToolbarActivity 只是为了方便写 Demo
 */
public class MomentListActivity extends BGAPPToolbarActivity {
    private static final int REQUEST_CODE_ADD_MOMENT = 1;

    private RecyclerView mMomentRv;
    private MomentAdapter mMomentAdapter;


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_moment_list);
        mMomentRv = getViewById(R.id.rv_moment_list_moments);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mMomentAdapter = new MomentAdapter(mMomentRv);
        mMomentRv.setLayoutManager(new LinearLayoutManager(this));
        mMomentRv.setAdapter(mMomentAdapter);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_moment_list_add) {
            startActivityForResult(new Intent(this, MomentAddActivity.class), REQUEST_CODE_ADD_MOMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD_MOMENT) {
            mMomentAdapter.addFirstItem(MomentAddActivity.getMoment(data));
            mMomentRv.smoothScrollToPosition(0);
        }
    }

    private static class MomentAdapter extends BGARecyclerViewAdapter<Moment> {

        public MomentAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_moment);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Moment moment) {
            if (TextUtils.isEmpty(moment.content)) {
                helper.setVisibility(R.id.tv_item_moment_content, View.GONE);
            } else {
                helper.setVisibility(R.id.tv_item_moment_content, View.VISIBLE);
                helper.setText(R.id.tv_item_moment_content, moment.content);
            }

            BGANinePhotoLayout ninePhotoLayout = helper.getView(R.id.npl_item_moment_photos);
            ninePhotoLayout.setData(moment.photos);
        }
    }
}
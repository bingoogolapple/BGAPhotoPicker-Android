package cn.bingoogolapple.photopicker.util;

import android.os.AsyncTask;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/25 下午6:30
 * 描述:
 */
public abstract class BGAAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {
    private Callback<Result> mCallback;

    public BGAAsyncTask(Callback<Result> callback) {
        mCallback = callback;
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (mCallback != null) {
            mCallback.onPostExecute(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mCallback != null) {
            mCallback.onTaskCancelled();
        }
        //无法放到 cancelTask()中，因为此方法会在cancelTask()后执行，所以如果放到cancelTask()中，则此字段永远是空，也就不会调用 onCancel()方法了
        mCallback = null;
    }

    public interface Callback<Result> {
        /**
         * 当结果返回的时候执行
         *
         * @param result 返回的结果
         */
        void onPostExecute(Result result);

        /**
         * 当请求被取消的时候执行
         */
        void onTaskCancelled();
    }
}

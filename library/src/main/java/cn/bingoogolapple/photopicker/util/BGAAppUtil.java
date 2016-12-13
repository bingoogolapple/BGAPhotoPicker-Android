package cn.bingoogolapple.photopicker.util;

import android.app.Application;
import android.util.Log;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/12/13 下午4:06
 * 描述:
 */
public class BGAAppUtil {
    public static final Application sApp;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            Log.e(BGAAppUtil.class.getSimpleName(), "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                Log.e(BGAAppUtil.class.getSimpleName(), "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            sApp = app;
        }
    }

    private BGAAppUtil() {
    }
}

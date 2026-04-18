package com.chang.nf.android.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import java.util.List;

/**
 * Base utils init.
 * <p>
 * Created by Nicholas Sean on 2024/7/3 14:45.
 *
 * @version 1.0
 */
public class BaseUtils {
    private static final String TAG = "BaseUtils";

    /**
     * 包名判断是否为主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        return TextUtils.equals(context.getPackageName(), getProcessName(context));
    }

    /**
     * 获取进程名称
     *
     * @param context
     * @return
     */
    private static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == Process.myPid()) {
                return proInfo.processName;
            }
        }
        return null;
    }

    /**
     * 改变状态栏导航栏深浅色
     *
     * @param window
     * @param lightIcons
     */
    public static void changeBarContrastStyle(Window window, Boolean lightIcons) {
        View decorView = window.getDecorView();
        if (lightIcons) {
            // Draw light icons on a dark background color
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                    & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            // Draw dark icons on a light background color
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }
}

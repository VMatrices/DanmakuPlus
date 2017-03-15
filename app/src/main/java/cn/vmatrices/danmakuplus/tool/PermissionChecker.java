package cn.vmatrices.danmakuplus.tool;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by iBelieve on 2017/3/9.
 */

public class PermissionChecker {

    public static boolean check(Context context) {
        if (canShowFloatWind(context) && canReadNotifications(context)) {
            return true;
        }
        return false;
    }

    public static boolean canReadNotifications(Context context) {
        final String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (flat != null) {
            if (!flat.isEmpty()) {
                final String[] names = flat.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        if (pkgName.equals(cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean canShowFloatWind(Context context) {

        //安卓6.0之上判断是否允许开启悬浮窗
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                return false;
            }
        }
        return true;
    }
}

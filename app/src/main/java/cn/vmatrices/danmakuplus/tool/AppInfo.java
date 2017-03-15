package cn.vmatrices.danmakuplus.tool;

import android.graphics.drawable.Drawable;

/**
 * Created by iBelieve on 2017/3/7.
 */

public class AppInfo {

    private String appName;
    private Drawable appIcon;
    private String pkgName;
    private boolean checked;

    public AppInfo(String appName, Drawable appIcon, String pkgName) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.pkgName = pkgName;
        checked = false;
    }

    public AppInfo() {
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}

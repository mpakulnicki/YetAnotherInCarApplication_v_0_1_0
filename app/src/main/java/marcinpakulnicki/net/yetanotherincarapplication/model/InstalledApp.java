package marcinpakulnicki.net.yetanotherincarapplication.model;


import android.graphics.drawable.Drawable;

public class InstalledApp {

    public InstalledApp() {

    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    private String appName;

    public String getActivityPath() {
        return activityPath;
    }

    public void setActivityPath(String activityPath) {
        this.activityPath = activityPath;
    }

    private String activityPath;

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    private Drawable appIcon;
}


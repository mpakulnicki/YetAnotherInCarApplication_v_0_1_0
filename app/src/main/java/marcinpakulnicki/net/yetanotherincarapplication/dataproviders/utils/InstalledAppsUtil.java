package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;
import marcinpakulnicki.net.yetanotherincarapplication.model.InstalledApp;


public class InstalledAppsUtil {
    public static List fetchAppsList (Context inContext)  {

        final PackageManager pm = inContext.getPackageManager();
        final List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        final List<InstalledApp> appsList = new ArrayList<InstalledApp>();

        InstalledApp ia = new InstalledApp();
        ia.setAppName(YaicaConstants.CLEAR_SHORTCUT_MESSAGE);
        appsList.add(ia);

        for (ApplicationInfo packageInfo : packages) {
            Drawable icon = pm.getApplicationIcon(packageInfo);
            InstalledApp installedApp = new InstalledApp();

           // if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)  {
                if (!String.valueOf(pm.getApplicationLabel(packageInfo)).equals("Yet Another In Car Application") &&
                        !String.valueOf(pm.getLaunchIntentForPackage(packageInfo.packageName)).equals("null"))  {

                    installedApp.setActivityPath(packageInfo.packageName);
                    installedApp.setAppIcon(icon);
                    installedApp.setAppName(String.valueOf(pm.getApplicationLabel(packageInfo)));

                    appsList.add(installedApp);
                }

           // }
        }

        return appsList;
    }
}
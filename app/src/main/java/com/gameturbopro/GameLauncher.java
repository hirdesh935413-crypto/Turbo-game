package com.gameturbopro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameLauncher {

    public static class AppItem {

        public final String name;
        public final String packageName;
        public final Drawable icon;

        public AppItem(
                String name,
                String packageName,
                Drawable icon
        ) {

            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
        }
    }

    private final Context context;
    private final PackageManager packageManager;
    private final SharedPreferences prefs;

    public GameLauncher(Context context) {

        this.context =
                context.getApplicationContext();

        packageManager =
                this.context.getPackageManager();

        prefs =
                this.context.getSharedPreferences(
                        "games",
                        Context.MODE_PRIVATE
                );
    }

    public List<AppItem> installedLaunchers() {

        Intent intent =
                new Intent(Intent.ACTION_MAIN);

        intent.addCategory(
                Intent.CATEGORY_LAUNCHER
        );

        List<ResolveInfo> result =
                packageManager.queryIntentActivities(
                        intent,
                        0
                );

        List<AppItem> apps =
                new ArrayList<>();

        for (ResolveInfo info : result) {

            if (info.activityInfo == null) {
                continue;
            }

            String packageName =
                    info.activityInfo.packageName;

            if (context.getPackageName()
                    .equals(packageName)) {
                continue;
            }

            String name =
                    String.valueOf(
                            info.loadLabel(
                                    packageManager
                            )
                    );

            Drawable icon =
                    info.loadIcon(
                            packageManager
                    );

            apps.add(
                    new AppItem(
                            name,
                            packageName,
                            icon
                    )
            );
        }

        apps.sort(
                Comparator.comparing(
                        item ->
                                item.name.toLowerCase()
                )
        );

        return apps;
    }

    public boolean selected(String packageName) {

        return prefs.getBoolean(
                "selected." + packageName,
                false
        );
    }

    public void select(
            String packageName,
            boolean selected
    ) {

        prefs.edit()
                .putBoolean(
                        "selected." + packageName,
                        selected
                )
                .apply();
    }

    public boolean favorite(String packageName) {

        return prefs.getBoolean(
                "favorite." + packageName,
                false
        );
    }

    public void favorite(
            String packageName,
            boolean favorite
    ) {

        prefs.edit()
                .putBoolean(
                        "favorite." + packageName,
                        favorite
                )
                .apply();
    }

    public void last(String packageName) {

        prefs.edit()
                .putString(
                        "last_played",
                        packageName
                )
                .apply();
    }

    public String last() {

        return prefs.getString(
                "last_played",
                ""
        );
    }

    public boolean installed(String packageName) {

        try {

            packageManager.getApplicationInfo(
                    packageName,
                    0
            );

            return true;

        } catch (PackageManager.NameNotFoundException e) {

            return false;
        }
    }

    public String label(String packageName) {

        try {

            return String.valueOf(
                    packageManager
                            .getApplicationInfo(
                                    packageName,
                                    0
                            )
                            .loadLabel(
                                    packageManager
                            )
            );

        } catch (Exception e) {

            return "Not Installed";
        }
    }

    public boolean launch(String packageName) {

        Intent intent =
                packageManager
                        .getLaunchIntentForPackage(
                                packageName
                        );

        if (intent == null) {
            return false;
        }

        try {

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

            last(packageName);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}

package com.gameturbopro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class SettingsHelper {

    private final Context context;

    public SettingsHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    private void open(String action) {

        try {

            Intent intent =
                    new Intent(action);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

        } catch (Exception e) {

            Intent fallback =
                    new Intent(Settings.ACTION_SETTINGS);

            fallback.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(fallback);
        }
    }

    public void display() {
        open(Settings.ACTION_DISPLAY_SETTINGS);
    }

    public void battery() {
        open(Settings.ACTION_BATTERY_SAVER_SETTINGS);
    }

    public void dnd() {
        open(
                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
        );
    }

    public void developer() {
        open(
                Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
        );
    }

    public void gameMode() {

        /*
         * GAME_MODE_SETTINGS is intentionally supplied as a
         * string instead of relying on a compile-time constant.
         */

        if (Build.VERSION.SDK_INT >= 31) {

            open("android.settings.GAME_MODE_SETTINGS");

        } else {

            open(Settings.ACTION_SETTINGS);
        }
    }

    public void appInfo() {

        try {

            Intent intent =
                    new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    );

            intent.setData(
                    Uri.parse(
                            "package:" +
                                    context.getPackageName()
                    )
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

        } catch (Exception e) {

            open(Settings.ACTION_SETTINGS);
        }
    }
}

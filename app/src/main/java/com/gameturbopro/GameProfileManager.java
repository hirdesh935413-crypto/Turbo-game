package com.gameturbopro;

import android.content.Context;
import android.content.SharedPreferences;

public class GameProfileManager {

    private final SharedPreferences prefs;

    public GameProfileManager(Context context) {
        prefs = context.getSharedPreferences("game_profiles", Context.MODE_PRIVATE);
    }

    public GameProfile load(String packageName) {

        GameProfile profile = new GameProfile();
        profile.packageName = packageName;

        profile.dpi =
                prefs.getString(packageName + ".dpi", "");

        profile.brightness =
                prefs.getString(packageName + ".brightness", "");

        profile.timeout =
                prefs.getString(packageName + ".timeout", "");

        profile.refreshRate =
                prefs.getString(packageName + ".refresh", "");

        profile.sensitivity =
                prefs.getString(packageName + ".sensitivity", "");

        profile.graphics =
                prefs.getString(packageName + ".graphics", "");

        profile.gamingMode =
                prefs.getBoolean(packageName + ".gaming", true);

        profile.dnd =
                prefs.getBoolean(packageName + ".dnd", false);

        return profile;
    }

    public void save(GameProfile profile) {

        if (profile == null || profile.packageName == null) {
            return;
        }

        prefs.edit()
                .putString(
                        profile.packageName + ".dpi",
                        profile.dpi
                )
                .putString(
                        profile.packageName + ".brightness",
                        profile.brightness
                )
                .putString(
                        profile.packageName + ".timeout",
                        profile.timeout
                )
                .putString(
                        profile.packageName + ".refresh",
                        profile.refreshRate
                )
                .putString(
                        profile.packageName + ".sensitivity",
                        profile.sensitivity
                )
                .putString(
                        profile.packageName + ".graphics",
                        profile.graphics
                )
                .putBoolean(
                        profile.packageName + ".gaming",
                        profile.gamingMode
                )
                .putBoolean(
                        profile.packageName + ".dnd",
                        profile.dnd
                )
                .apply();
    }

    public void delete(String packageName) {

        if (packageName == null) {
            return;
        }

        prefs.edit()
                .remove(packageName + ".dpi")
                .remove(packageName + ".brightness")
                .remove(packageName + ".timeout")
                .remove(packageName + ".refresh")
                .remove(packageName + ".sensitivity")
                .remove(packageName + ".graphics")
                .remove(packageName + ".gaming")
                .remove(packageName + ".dnd")
                .apply();
    }
}

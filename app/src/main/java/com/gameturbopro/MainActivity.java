package com.gameturbopro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int BG = Color.rgb(5, 7, 10);
    private static final int CARD = Color.rgb(13, 19, 27);
    private static final int CARD_2 = Color.rgb(17, 25, 35);
    private static final int LINE = Color.rgb(36, 49, 63);
    private static final int TEXT = Color.rgb(244, 247, 250);
    private static final int MUTED = Color.rgb(137, 150, 166);
    private static final int ACCENT = Color.rgb(0, 245, 160);
    private static final int CYAN = Color.rgb(0, 200, 255);
    private static final int RED = Color.rgb(255, 82, 82);

    private FrameLayout content;
    private BottomNavigationView bottomNav;

    private DeviceMonitor deviceMonitor;
    private NetworkMonitor networkMonitor;
    private GameLauncher gameLauncher;
    private GameProfileManager profileManager;
    private DpiManager dpiManager;
    private SettingsHelper settingsHelper;

    private final Handler handler = new Handler();

    private String selectedGame = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(BG);
        window.setNavigationBarColor(BG);

        deviceMonitor = new DeviceMonitor(this);
        networkMonitor = new NetworkMonitor(this);
        gameLauncher = new GameLauncher(this);
        profileManager = new GameProfileManager(this);
        dpiManager = new DpiManager(this);
        settingsHelper = new SettingsHelper(this);

        content = findViewById(R.id.content);
        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                homePage();
                return true;
            }

            if (id == R.id.nav_boost) {
                boostPage();
                return true;
            }

            if (id == R.id.nav_dpi) {
                dpiPage();
                return true;
            }

            if (id == R.id.nav_games) {
                gamesPage();
                return true;
            }

            if (id == R.id.nav_settings) {
                settingsPage();
                return true;
            }

            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    // =========================================================
    // BASIC UI HELPERS
    // =========================================================

    private int dp(float value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }

    private TextView text(
            String value,
            float size,
            int color
    ) {
        TextView view = new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setFontFeatureSettings("kern");

        return view;
    }

    private TextView title(String value) {
        TextView view = text(value, 25, TEXT);
        view.setTypeface(
                Typeface.create(
                        "sans",
                        Typeface.BOLD
                )
        );
        return view;
    }

    private TextView label(String value) {
        TextView view = text(value, 11, MUTED);
        view.setTypeface(
                Typeface.create(
                        "sans",
                        Typeface.BOLD
                )
        );
        return view;
    }

    private GradientDrawable background(
            int color,
            float radius,
            int strokeColor
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        if (strokeColor != Color.TRANSPARENT) {
            drawable.setStroke(
                    dp(1),
                    strokeColor
            );
        }

        return drawable;
    }

    private LinearLayout vertical() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        return layout;
    }

    private LinearLayout horizontal() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.HORIZONTAL
        );

        layout.setGravity(
                Gravity.CENTER_VERTICAL
        );

        return layout;
    }

    private ScrollView scroll(View child) {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        scroll.addView(child);

        return scroll;
    }

    private void clear() {
        content.removeAllViews();
    }

    private void padding(
            View view,
            int left,
            int top,
            int right,
            int bottom
    ) {
        view.setPadding(
                dp(left),
                dp(top),
                dp(right),
                dp(bottom)
        );
    }

    private View spacer(int height) {

        Space space = new Space(this);

        space.setLayoutParams(
                new LinearLayout.LayoutParams(
                        1,
                        dp(height)
                )
        );

        return space;
    }

    private Button button(
            String value,
            int backgroundColor,
            int textColor
    ) {

        Button button =
                new Button(this);

        button.setText(value);
        button.setTextSize(12);
        button.setTextColor(textColor);
        button.setAllCaps(false);
        button.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        button.setBackground(
                background(
                        backgroundColor,
                        15,
                        Color.TRANSPARENT
                )
        );

        button.setPadding(
                dp(14),
                0,
                dp(14),
                0
        );

        return button;
    }

    private LinearLayout card() {

        LinearLayout card =
                vertical();

        card.setBackground(
                background(
                        CARD,
                        20,
                        LINE
                )
        );

        padding(
                card,
                16,
                16,
                16,
                16
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                dp(12),
                0,
                dp(12),
                dp(12)
        );

        card.setLayoutParams(params);

        return card;
    }

    private void addSectionTitle(
            LinearLayout root,
            String heading,
            String subtitle
    ) {

        LinearLayout block = vertical();

        TextView h = title(heading);

        TextView s = text(
                subtitle,
                12,
                MUTED
        );

        block.addView(h);

        block.addView(
                s,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        padding(
                block,
                16,
                20,
                16,
                12
        );

        root.addView(block);
    }

    // =========================================================
    // HOME
    // =========================================================

    private void homePage() {

        clear();

        LinearLayout root = vertical();

        TextView contentTitle =
                title("GAME TURBO PRO");

        TextView subtitle =
                text(
                        "PERFORMANCE COMMAND CENTER",
                        10,
                        ACCENT
                );

        LinearLayout header =
                horizontal();

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout headerText =
                vertical();

        headerText.addView(contentTitle);
        headerText.addView(subtitle);

        header.addView(
                headerText,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView status =
                text("● READY", 11, ACCENT);

        status.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        header.addView(status);

        padding(
                header,
                16,
                18,
                16,
                5
        );

        root.addView(header);

        LinearLayout hero = card();

        TextView model =
                text(
                        deviceMonitor.model(),
                        18,
                        TEXT
                );

        model.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        TextView android =
                text(
                        deviceMonitor.android(),
                        12,
                        MUTED
                );

        hero.addView(model);
        hero.addView(android);

        TextView dpi =
                text(
                        "DPI  " + deviceMonitor.dpi(),
                        12,
                        ACCENT
                );

        dpi.setPadding(0, dp(8), 0, 0);

        hero.addView(dpi);

        root.addView(hero);

        LinearLayout statsRow1 =
                horizontal();

        statsRow1.setPadding(
                dp(12),
                0,
                dp(12),
                0
        );

        statsRow1.addView(
                statCard(
                        "BATTERY",
                        deviceMonitor.battery()
                ),
                weightParams()
        );

        statsRow1.addView(
                statCard(
                        "TEMP",
                        deviceMonitor.temperature()
                ),
                weightParams()
        );

        root.addView(statsRow1);

        LinearLayout statsRow2 =
                horizontal();

        statsRow2.setPadding(
                dp(12),
                dp(12),
                dp(12),
                0
        );

        statsRow2.addView(
                statCard(
                        "RAM",
                        deviceMonitor.ram()
                ),
                weightParams()
        );

        statsRow2.addView(
                statCard(
                        "DISPLAY",
                        deviceMonitor.resolution()
                ),
                weightParams()
        );

        root.addView(statsRow2);

        LinearLayout statsRow3 =
                horizontal();

        statsRow3.setPadding(
                dp(12),
                dp(12),
                dp(12),
                0
        );

        statsRow3.addView(
                statCard(
                        "REFRESH",
                        deviceMonitor.refresh()
                ),
                weightParams()
        );

        statsRow3.addView(
                statCard(
                        "NETWORK",
                        networkMonitor.type()
                ),
                weightParams()
        );

        root.addView(statsRow3);

        root.addView(spacer(18));

        LinearLayout boostContainer =
                vertical();

        boostContainer.setGravity(
                Gravity.CENTER
        );

        TextView selected =
                text(
                        selectedGame.isEmpty()
                                ? "NO GAME SELECTED"
                                : gameLauncher.label(selectedGame),
                        11,
                        MUTED
                );

        boostContainer.addView(selected);

        Button boost =
                button(
                        "⚡  BOOST NOW",
                        ACCENT,
                        Color.BLACK
                );

        boost.setTextSize(18);

        boost.setGravity(Gravity.CENTER);

        boost.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(245),
                        dp(70)
                )
        );

        boost.setElevation(dp(10));

        boost.setOnClickListener(
                v -> runBoost()
        );

        boostContainer.addView(
                boost
        );

        boostContainer.startAnimation(
                android.view.animation.AnimationUtils
                        .loadAnimation(
                                this,
                                R.anim.pulse
                        )
        );

        root.addView(
                boostContainer,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        root.addView(spacer(25));

        TextView note =
                text(
                        "System-level CPU/GPU tuning is controlled by Android and the device manufacturer. "
                                + "This app only uses supported APIs and official settings.",
                        11,
                        MUTED
                );

        padding(
                note,
                18,
                0,
                18,
                20
        );

        root.addView(note);

        content.addView(scroll(root));
    }

    private LinearLayout.LayoutParams weightParams() {

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        p.setMargins(
                dp(6),
                0,
                dp(6),
                0
        );

        return p;
    }

    private LinearLayout statCard(
            String name,
            String value
    ) {

        LinearLayout box = vertical();

        box.setBackground(
                background(
                        CARD_2,
                        17,
                        LINE
                )
        );

        padding(
                box,
                12,
                12,
                12,
                12
        );

        TextView n =
                label(name);

        TextView v =
                text(
                        value,
                        13,
                        TEXT
                );

        v.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        box.addView(n);
        box.addView(v);

        return box;
    }

    // =========================================================
    // BOOST
    // =========================================================

    private void boostPage() {

        clear();

        LinearLayout root =
                vertical();

        addSectionTitle(
                root,
                "BOOST CENTER",
                "SUPPORTED ANDROID GAMING CONTROLS"
        );

        LinearLayout modeCard =
                card();

        TextView modeTitle =
                text(
                        "Gaming Mode",
                        17,
                        TEXT
                );

        modeTitle.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        TextView modeInfo =
                text(
                        "Use your device's official gaming mode when available.",
                        11,
                        MUTED
                );

        modeCard.addView(modeTitle);
        modeCard.addView(modeInfo);

        Button gameMode =
                button(
                        "OPEN GAME MODE",
                        ACCENT,
                        Color.BLACK
                );

        gameMode.setOnClickListener(
                v -> settingsHelper.gameMode()
        );

        LinearLayout.LayoutParams gp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(48)
                );

        gp.setMargins(0, dp(14), 0, 0);

        modeCard.addView(gameMode, gp);

        root.addView(modeCard);

        LinearLayout keepCard = card();

        SwitchMaterial keepAwake =
                new SwitchMaterial(this);

        keepAwake.setText(
                "KEEP SCREEN AWAKE"
        );

        keepAwake.setTextColor(TEXT);
        keepAwake.setTextSize(14);
        keepAwake.setChecked(false);

        keepAwake.setOnCheckedChangeListener(
                (buttonView, checked) -> {

                    if (checked) {
                        getWindow().addFlags(
                                android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        );
                    } else {
                        getWindow().clearFlags(
                                android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        );
                    }
                }
        );

        keepCard.addView(keepAwake);

        TextView keepInfo =
                text(
                        "Keeps GAME TURBO PRO itself awake while open.",
                        11,
                        MUTED
                );

        keepCard.addView(keepInfo);

        root.addView(keepCard);

        LinearLayout shortcuts = card();

        TextView shortcutTitle =
                text(
                        "OFFICIAL SYSTEM SHORTCUTS",
                        15,
                        TEXT
                );

        shortcutTitle.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        shortcuts.addView(shortcutTitle);

        addAction(
                shortcuts,
                "🔋 Battery / Power",
                "Open Android battery settings",
                v -> settingsHelper.battery()
        );

        addAction(
                shortcuts,
                "☀ Display",
                "Brightness, timeout and display settings",
                v -> settingsHelper.display()
        );

        addAction(
                shortcuts,
                "🔕 Do Not Disturb",

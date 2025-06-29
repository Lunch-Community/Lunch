package com.dudu.wearlauncher.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import com.dudu.wearlauncher.R;
import com.dudu.wearlauncher.ui.BaseActivity;

public class SettingsActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.card_settings_about).setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });
        findViewById(R.id.card_settings_app_list).setOnClickListener(v -> {
            startActivity(new Intent(this, AppListSettingsActivity.class));
        });
        findViewById(R.id.card_settings_icon).setOnClickListener(v -> {
            startActivity(new Intent(this, IconSettingsActivity.class));
        });
        findViewById(R.id.card_settings_setting_center).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingCenterSettingsActivity.class));
        });
        findViewById(R.id.card_settings_home_page).setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageSettingsActivity.class));
        });
    }
    
}

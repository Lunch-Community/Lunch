package com.dudu.wearlauncher.ui.settings;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import com.dudu.wearlauncher.R;
import com.dudu.wearlauncher.ui.BaseActivity;
import com.dudu.wearlauncher.utils.SharedPreferencesUtil;

public class HomePageSettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_settings);

        Switch msgListSwitch = findViewById(R.id.switch_msg_list);
        Switch settingCenterSwitch = findViewById(R.id.switch_setting_center);

        msgListSwitch.setChecked((boolean) SharedPreferencesUtil.getData(SharedPreferencesUtil.MSG_LIST_ENABLED, true));
        settingCenterSwitch.setChecked((boolean) SharedPreferencesUtil.getData(SharedPreferencesUtil.SETTING_CENTER_ENABLED, true));

        findViewById(R.id.card_msg_list).setOnClickListener(v -> {
            msgListSwitch.toggle();
        });
        findViewById(R.id.card_setting_center).setOnClickListener(v -> {
            settingCenterSwitch.toggle();
        });

        msgListSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.putData(SharedPreferencesUtil.MSG_LIST_ENABLED, isChecked);
        });
        settingCenterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.putData(SharedPreferencesUtil.SETTING_CENTER_ENABLED, isChecked);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "重启后生效", Toast.LENGTH_SHORT).show();
    }
}

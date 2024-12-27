package com.lazytong.launcher.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import com.blankj.utilcode.util.ToastUtils;
import com.lazytong.launcher.R;
import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.utils.SharedPreferencesUtil;

public class RequestPermissonActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);
        findViewById(R.id.card_write_syssetting)
                .setOnClickListener(
                        v -> {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + this.getPackageName()));
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                ToastUtils.showShort("打开页面失败，请尝试手动打开权限");
                            }
                        });
        findViewById(R.id.card_notification)
                .setOnClickListener(
                        v -> {
                            try {
                                Intent intent =
                                        new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                ToastUtils.showShort("打开页面失败，请尝试手动打开权限");
                            }
                        });
        findViewById(R.id.next_btn)
                .setOnClickListener(
                        v -> {
                            SharedPreferencesUtil.putData(SharedPreferencesUtil.FIRST_START, false);
                            finish();
                        });
    }
}

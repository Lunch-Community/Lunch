package com.lazytong.launcher.ui.settings;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazytong.launcher.R;
import com.lazytong.launcher.ui.BaseActivity;
import org.jetbrains.annotations.NotNull;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextView) findViewById(R.id.author_text)).setText("By LazyTong");
        ((TextView) findViewById(R.id.version_text)).setText("版本 " + AppUtils.getAppVersionName());
    }
}
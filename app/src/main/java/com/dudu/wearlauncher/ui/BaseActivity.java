package com.lazytong.launcher.ui;

import android.R;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.SharedPreferencesUtil;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 自动适配屏幕
    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = WearLauncherApp.getFitDisplayContext(newBase);
        super.attachBaseContext(newBase);
    }

    public void setContentLayout(String layoutName) {

        // 由于某些nt厂商 圆屏表用方屏表的设置 导致layout_round无效 故出此下策
        // 检测圆方屏并自动选取view加载
        try {
            //通过反射自动获取资源id
            int squareLayout = R.layout.class.getField(layoutName).getInt(layoutName);
            int roundLayout = R.layout.class.getField(layoutName+"_round").getInt(layoutName+"_round");
            
            String screenType =
                    String.valueOf(
                            SharedPreferencesUtil.getData(
                                    SharedPreferencesUtil.SCREEN_TYPE, "unknown"));
            Configuration cfg = new Configuration();
            if (screenType.equals("unknown")) {
                if (cfg.isScreenRound()) setContentView(roundLayout);
                else setContentView(squareLayout);
            }
            if (screenType.equals("round")) setContentView(roundLayout);
            if (screenType.equals("square")) setContentView(squareLayout);
        } catch (NoSuchFieldException err) {
            err.printStackTrace();
        } catch (IllegalAccessException err) {
            err.printStackTrace();
        }
    }
}

package com.lazytong.launcher.ui.home;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.lazytong.launcher.R;
import com.lazytong.launcher.model.WatchFaceInfo;
import com.lazytong.launcher.services.NotificationListenerService;
import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.ui.ViewPagerFragmentAdapter;
import com.lazytong.launcher.ui.home.pagertransformer.CubicOverturnTransformer;
import com.lazytong.launcher.ui.home.pagertransformer.XTCTrans;
import com.lazytong.launcher.ui.settings.RequestPermissonActivity;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
import com.lazytong.launcher.utils.WatchFaceHelper;

import java.util.List;

public class HomeActivity extends BaseActivity {
    ViewPager homeViewPager;
    WatchFaceFragment watchFaceFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkIsFirstStart();
        enableComponent();

        startService(new Intent(this, NotificationListenerService.class));

        watchFaceFragment = new WatchFaceFragment();

        homeViewPager = findViewById(R.id.home_pager);
        List<Fragment> fragmentList = List.of(watchFaceFragment, new AppListFragment());
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(),fragmentList);
        homeViewPager.setAdapter(adapter);
        homeViewPager.setPageTransformer(false,new CubicOverturnTransformer());

        homeViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                try {
                    if (watchFaceFragment.getWatchFace() == null) return;
                    if (i != 0) {
                        watchFaceFragment.getWatchFace().onWatchfaceInvisible();
                    } else {
                        watchFaceFragment.getWatchFace().onWatchfaceVisible();
                    }
                        
                } catch(Exception err) {
                	
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (watchFaceFragment.getWatchFace() != null) watchFaceFragment.getWatchFace().onWatchfaceInvisible();
        } catch(Exception err) {
        	
        }   
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (watchFaceFragment.getWatchFace() != null) watchFaceFragment.getWatchFace().onWatchfaceVisible();
        } catch(Exception err) {
        	
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if(!isDestroyed()) {
        	homeViewPager.setCurrentItem(0);
            if (watchFaceFragment.getSwipeDrawer() != null) watchFaceFragment.getSwipeDrawer().closeDrawer();
        }
    }
    
    private void enableComponent() {
        PackageManager pm = getPackageManager();
        ComponentName normalComponentName = new ComponentName(this,HomeActivity.class);
        pm.setComponentEnabledSetting(normalComponentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    private void checkIsFirstStart() {
    	if((Boolean)SharedPreferencesUtil.getData(SharedPreferencesUtil.FIRST_START,true)) {
    		SharedPreferencesUtil.putData(SharedPreferencesUtil.SETTING_CENTER,"[{\"button\":\"button_wifi\"},{\"button\":\"button_mobiledata\"},{\"button\":\"button_bluetooth\"}]");
            Intent intent = new Intent(this,RequestPermissonActivity.class);
            startActivity(intent);
    	}
    }
}

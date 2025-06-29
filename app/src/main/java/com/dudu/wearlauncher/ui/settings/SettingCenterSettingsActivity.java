package com.dudu.wearlauncher.ui.settings;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Toast;
import com.dudu.wearlauncher.R;
import com.dudu.wearlauncher.model.FastSettingsItem;
import com.dudu.wearlauncher.ui.BaseActivity;
import com.dudu.wearlauncher.ui.home.fastsettings.BluetoothItem;
import com.dudu.wearlauncher.ui.home.fastsettings.MobileNetworkItem;
import com.dudu.wearlauncher.ui.home.fastsettings.WifiSwitchItem;
import com.dudu.wearlauncher.ui.home.fastsettings.ZenModeItem;
import com.dudu.wearlauncher.utils.DensityUtil;
import com.dudu.wearlauncher.utils.ILog;
import com.dudu.wearlauncher.utils.SettingCenterManager;
import com.dudu.wearlauncher.utils.SharedPreferencesUtil;
import com.dudu.wearlauncher.widget.SwitchIconButton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.stream.Collectors;

public class SettingCenterSettingsActivity extends BaseActivity{
    SwitchIconButton btn1;
    SwitchIconButton btn2;
    SwitchIconButton btn3;
    GridLayout btnList;
    SwitchIconButton choosingButton;
    FastSettingsItem[] items = {new WifiSwitchItem(),new MobileNetworkItem(),new BluetoothItem(),new ZenModeItem()};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_center_settings);
        btn1 = findViewById(R.id.settings_btn_1);
        btn2 = findViewById(R.id.settings_btn_2);
        btn3 = findViewById(R.id.settings_btn_3);
        btnList = findViewById(R.id.btn_grid_list);
        try {
        	JSONArray oldArray = new JSONArray((String)SharedPreferencesUtil.getData(SharedPreferencesUtil.SETTING_CENTER,"[{\"button\":\"button_wifi\"},{\"button\":\"button_mobiledata\"},{\"button\":\"button_bluetooth\"}]"));
            btn1.attach(SettingCenterManager.getButtonInstance(oldArray.getJSONObject(0).getString("button")));
            btn2.attach(SettingCenterManager.getButtonInstance(oldArray.getJSONObject(1).getString("button")));
            btn3.attach(SettingCenterManager.getButtonInstance(oldArray.getJSONObject(2).getString("button")));
        } catch(Exception err) {
            err.printStackTrace();
            btn1.attach(new WifiSwitchItem());
            btn2.attach(new MobileNetworkItem());
            btn3.attach(new BluetoothItem());    
        }
        
        
        btn1.setOnClickListener(v->{
            choosingButton=(SwitchIconButton)v;
            v.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray_choosed));
            btn2.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
            btn3.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
        });
        btn2.setOnClickListener(v->{
            choosingButton=(SwitchIconButton)v;
            v.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray_choosed));
            btn1.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
            btn3.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
        });
        btn3.setOnClickListener(v->{
            choosingButton=(SwitchIconButton)v;
            v.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray_choosed));
            btn1.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
            btn2.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
        });
        btn1.performClick();
        Map<Class,String> btnItemMap = SettingCenterManager.classMap.entrySet().stream().collect(Collectors.toMap(entity-> entity.getValue(),entity-> entity.getKey()));

        for(int i = 0; i < SettingCenterManager.classMap.size(); ++i) {
        	SwitchIconButton button = new SwitchIconButton(this);
            FrameLayout child = new FrameLayout(this);

            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(DensityUtil.dip2px(this,48),DensityUtil.dip2px(this,48));
            flp.gravity = Gravity.CENTER;
            //flp.height = DensityUtil.dip2px(this,48);
            //flp.width = DensityUtil.dip2px(this,48);
            child.addView(button,flp);

            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.setGravity(Gravity.CENTER);
            glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
            glp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
            glp.topMargin = DensityUtil.dip2px(this,2);
            glp.bottomMargin = DensityUtil.dip2px(this,2);

            btnList.addView(child,glp);

            button.attach(items[i]);
            button.setOnClickListener(v->{
                FastSettingsItem item = ((SwitchIconButton)v).getFastSettingsItem();
                choosingButton.setImageDrawable(item.getDrawable());
                try {
                    JSONArray jsonArray = new JSONArray((String)SharedPreferencesUtil.getData(SharedPreferencesUtil.SETTING_CENTER,new JSONArray().toString()));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("button",btnItemMap.get(item.getClass()));
                    jsonArray.put(getChildPosition(choosingButton),jsonObject);
                    SharedPreferencesUtil.putData(SharedPreferencesUtil.SETTING_CENTER,jsonArray.toString());
                } catch(Exception err) {
                	ILog.e("button设置失败:"+err.toString());
                    err.printStackTrace();
                }
            });
        }
        
    }
    private int getChildPosition(View view){
        ViewGroup parent = (ViewGroup)view.getParent();
        for(int i = 0; i < parent.getChildCount(); ++i) {
        	if(parent.getChildAt(i).equals(view)) {
        		return i;
        	}
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "重启后生效", Toast.LENGTH_SHORT).show();
    }
}

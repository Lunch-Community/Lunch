package com.lazytong.launcher.ui.settings;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import com.lazytong.launcher.R;
import com.lazytong.launcher.model.FastSettingsItem;
import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.ui.home.fastsettings.BluetoothItem;
import com.lazytong.launcher.ui.home.fastsettings.MobileNetworkItem;
import com.lazytong.launcher.ui.home.fastsettings.WifiSwitchItem;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.SettingCenterManager;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
import com.lazytong.launcher.widget.SwitchIconButton;
import com.google.gson.JsonArray;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class SettingCenterSettingsActivity extends BaseActivity{
    SwitchIconButton btn1;
    SwitchIconButton btn2;
    SwitchIconButton btn3;
    GridLayout btnList;
    SwitchIconButton choosingButton;
    FastSettingsItem[] items = {new WifiSwitchItem(),new MobileNetworkItem(),new BluetoothItem()};
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

        for(int i = 0; i < btnList.getChildCount(); ++i) {
        	View child = btnList.getChildAt(i);
            ((SwitchIconButton)child).attach(items[i]);
            child.setOnClickListener(v->{
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
}

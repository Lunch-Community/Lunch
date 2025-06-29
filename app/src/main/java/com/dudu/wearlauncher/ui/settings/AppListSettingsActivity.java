package com.dudu.wearlauncher.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.dudu.wearlauncher.R;
import com.dudu.wearlauncher.ui.BaseActivity;
import com.dudu.wearlauncher.utils.SharedPreferencesUtil;
import com.google.android.material.card.MaterialCardView;
import me.shihao.library.XRadioGroup;

public class AppListSettingsActivity extends BaseActivity{
    XRadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_settings);
        radioGroup = findViewById(R.id.settings_app_list_radio_group);
        String listStyle = (String)SharedPreferencesUtil.getData(SharedPreferencesUtil.APP_LIST_STYLE,"linear");
        
        for(int i = 0; i < radioGroup.getChildCount(); ++i) {
                	MaterialCardView card = (MaterialCardView)radioGroup.getChildAt(i);
                    ConstraintLayout container = (ConstraintLayout)card.getChildAt(0);
                    card.setOnClickListener(v->{
                        ((RadioButton)container.getChildAt(1)).setChecked(true);
            });
        }

        radioGroup.setOnCheckedChangeListener((root, id) -> {
            ConstraintLayout toggledParent = (ConstraintLayout) findViewById(id).getParent();
            toggledParent.setBackgroundResource(R.drawable.chip_toggled);
            for (int i = 0; i < root.getChildCount(); ++i) {
                MaterialCardView card = (MaterialCardView) root.getChildAt(i);
                ConstraintLayout container = (ConstraintLayout) card.getChildAt(0);
                container.setBackground(null);
            }
            toggledParent.setBackgroundResource(R.drawable.chip_toggled);
            switch (id) {
                case R.id.linear_list_radio_btn:
                    SharedPreferencesUtil.putData(SharedPreferencesUtil.APP_LIST_STYLE, "linear");
                    break;
                case R.id.grid_list_radio_btn:
                    SharedPreferencesUtil.putData(SharedPreferencesUtil.APP_LIST_STYLE, "grid");
                    break;
                case R.id.bubble_list_radio_btn:
                    SharedPreferencesUtil.putData(SharedPreferencesUtil.APP_LIST_STYLE, "bubble");
                    break;
            }
        });
        if(listStyle.equals("linear")) {
        	((RadioButton)findViewById(R.id.linear_list_radio_btn)).setChecked(true);
        }if(listStyle.equals("grid")) {
        	((RadioButton)findViewById(R.id.grid_list_radio_btn)).setChecked(true);
        }if(listStyle.equals("bubble")) {
            ((RadioButton)findViewById(R.id.bubble_list_radio_btn)).setChecked(true);
        }
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent("com.dudu.wearlauncher.REFESH_APP_LIST");
        sendBroadcast(intent);
        super.onDestroy();
    }
    
}

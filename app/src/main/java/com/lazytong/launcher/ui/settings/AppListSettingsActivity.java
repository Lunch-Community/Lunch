package com.lazytong.launcher.ui.settings;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.lazytong.launcher.R;
import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
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
    
        radioGroup.setOnCheckedChangeListener(new XRadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(XRadioGroup root, int id) {
                ConstraintLayout toggledParent = (ConstraintLayout)findViewById(id).getParent();
                toggledParent.setBackgroundResource(R.drawable.chip_toggled);
                for(int i = 0; i < root.getChildCount(); ++i) {
                	MaterialCardView card = (MaterialCardView)root.getChildAt(i);
                    ConstraintLayout container = (ConstraintLayout)card.getChildAt(0);
                    container.setBackground(null);
                }
                toggledParent.setBackgroundResource(R.drawable.chip_toggled);
                switch(id){
                    case R.id.linear_list_radio_btn:
                        SharedPreferencesUtil.putData(SharedPreferencesUtil.APP_LIST_STYLE,"linear");
                        break;
                    case R.id.grid_list_radio_btn:
                        SharedPreferencesUtil.putData(SharedPreferencesUtil.APP_LIST_STYLE,"grid");
                        break;
                    default:
                }
            }
            
        });
        if(listStyle.equals("linear")) {
        	((RadioButton)findViewById(R.id.linear_list_radio_btn)).setChecked(true);
        }if(listStyle.equals("grid")) {
        	((RadioButton)findViewById(R.id.grid_list_radio_btn)).setChecked(true);
        }
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent("com.lazytong.launcher.REFESH_APP_LIST");
        sendBroadcast(intent);
        super.onDestroy();
    }
    
}

package com.lazytong.launcher.ui.home.fastsettings;
import android.graphics.drawable.Drawable;
import com.lazytong.launcher.R;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.model.FastSettingsItem;

public class NullItem extends FastSettingsItem{
    @Override
    public Drawable getDrawable() {
        return WearLauncherApp.getContext().getResources().getDrawable(R.drawable.icon_about);
        
    }
    @Override
    public ButtonItemListener getTouchListener() {
        return new ButtonItemListener(){
            @Override
            public void onClick(boolean checked) {
                // TODO: Implement this method
            }
            @Override
            public void onLongClick(boolean checked) {
                // TODO: Implement this method
            }
            
        };
    }
    @Override
    public String getSettingsName() {
        return "null";
    }
    
    
    
}

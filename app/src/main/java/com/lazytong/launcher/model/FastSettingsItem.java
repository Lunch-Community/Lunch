package com.lazytong.launcher.model;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.lazytong.launcher.widget.SwitchIconButton;
import java.util.ArrayList;
import java.util.List;

public abstract class FastSettingsItem {
    public interface ButtonItemListener{
        void onClick(boolean checked);
        void onLongClick(boolean checked);
    }
    public SwitchIconButton button;
    public Drawable drawable;
    public ButtonItemListener touchListener;
    public String settingsName;
    public void registerStateObserver(Context context){};
    public void unregisterStateObserver(){};
    public void setButton(SwitchIconButton button) {
    	this.button = button;
    }
    public abstract Drawable getDrawable();
    public abstract String getSettingsName();
    public abstract ButtonItemListener getTouchListener();
    
}

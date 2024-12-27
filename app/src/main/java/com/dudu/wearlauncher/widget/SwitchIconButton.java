package com.lazytong.launcher.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import com.lazytong.launcher.R;
import com.lazytong.launcher.model.FastSettingsItem;
import com.lazytong.launcher.ui.home.fastsettings.NullItem;
import com.lazytong.launcher.utils.ILog;
import com.google.android.material.internal.CheckableImageButton;
import org.jetbrains.annotations.NotNull;

public class SwitchIconButton extends CheckableImageButton {
    FastSettingsItem item;
    public FastSettingsItem getFastSettingsItem() {
    	if(item!=null) {
    		return item;
    	}
        return new NullItem();
    }
    public SwitchIconButton(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public SwitchIconButton(
            @NonNull @NotNull Context context,
            @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchIconButton(
            @NonNull @NotNull Context context,
            @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setActivated(false);
        setForeground(getResources().getDrawable(R.drawable.ripple_effect));
        setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
        setOnClickListener(v->{ILog.e("aaaaa");});
        /*setOnTouchListener(
                (view, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        callOnClick();
                        setActivated(!isActivated());
                    }
                    return false;
                });*/
        setScaleType(ScaleType.CENTER);
    }

    public void attach(FastSettingsItem item) {
        this.item = item;
        item.setButton(this);
            setOnClickListener(
                    v -> {
                        item.getTouchListener().onClick(isActivated());
                        setActivated(!isActivated());
                    });
            setOnLongClickListener(
                    v -> {
                        item.getTouchListener().onLongClick(isActivated());
                        return false;
                    });
            
        
        if (item.getDrawable() != null) {
            setImageDrawable(item.getDrawable());
        }
        item.registerStateObserver(getContext());
    }
    @Override
    protected void onDetachedFromWindow() {
        if(item != null) item.unregisterStateObserver();
        super.onDetachedFromWindow();
    }
    
}

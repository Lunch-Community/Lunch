package com.lazytong.launcher.ui.home.pagertransformer;

import android.view.View;
import androidx.viewpager.widget.ViewPager;

public class XTCTrans implements ViewPager.PageTransformer {

    public void transformPage(View view, float f) {
        if (f > -1.0f) {
            if (f < 1.0f) {
                int width = view.getWidth();
                if (Boolean.TRUE.equals(view.getTag(2131231736))) {
                    view.setTranslationX(((float) (-width)) * f);
                    return;
                }
                view.setTranslationX((((float) (-width)) / 5.0f) * f);
                view.setRotationY(f * -75.0f);
                return;
            }
        }
        view.setTranslationX(0.0f);
        view.setRotationY(0.0f);
    }
}

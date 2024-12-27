package com.lazytong.launcher.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class CellLayout extends ViewGroup{
    public int maxHeight = 99;
    public int width = 4;
    public CellLayout(Context context){
        super(context);
    }
    public CellLayout(Context context,AttributeSet attrs){
        super(context,attrs);
    }
    @Override
    protected void onMeasure(int arg0, int arg1) {
        super.onMeasure(arg0, arg1);
        
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        
    }
    
}

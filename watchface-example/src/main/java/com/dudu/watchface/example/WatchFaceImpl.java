package com.dudu.watchface.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.lazytong.launcher.model.WatchFace;
import com.lazytong.launcher.utils.ILog;
import com.dudu.watchface.example.R;

import java.io.IOException;
import java.util.Calendar;


public class WatchFaceImpl extends WatchFace {

    private TextView tvBattery;
    private TextView tvStep;
    private TextView tvTime;
    private TextView tvDate;
    private TextView tvWeek;
    private ImageView ivStep;
    private ImageView ivBattery;
    private FrameLayout centerLayout;


    public WatchFaceImpl(Context context) {
        super(context);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_main, this);
/*
        setOnClickListener(v->{
            WatchSurfaceHelper.startWsfActivity(getHostContext(),BuildConfig.WATCHFACE_NAME,SettingsSurface.class);
        });
*/
        tvBattery = findViewById(R.id.tv_battery);
        tvStep = findViewById(R.id.tv_step);
        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
        tvWeek = findViewById(R.id.tv_week);
        ivBattery = findViewById(R.id.iv_battery);
        ivStep = findViewById(R.id.iv_step);
        centerLayout = findViewById(R.id.centerlayout);

        GifView gifView = new GifView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DensityUtil.dip2px(getContext(),60),DensityUtil.dip2px(getContext(),30));
        layoutParams.gravity = Gravity.CENTER_VERTICAL+Gravity.END;
        layoutParams.bottomMargin = DensityUtil.dip2px(getContext(),36);

        gifView.setLayoutParams(layoutParams);

        try {
        	gifView.setGifStream(getResources().getAssets().open("astr.gif"));
        } catch(IOException err) {
        	err.printStackTrace();
        }

        centerLayout.addView(gifView);

        ivBattery.setImageResource(R.drawable.battery);
        ivStep.setImageResource(R.drawable.steps);
        tvTime.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/DS-DIGI-1.ttf"));
        tvStep.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/DS-DIGIB-2.ttf"));
        tvBattery.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/DS-DIGIB-2.ttf"));
        tvDate.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/DS-DIGIB-2.ttf"));
        tvWeek.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/DS-DIGIB-2.ttf"));
    }
    @Override
    public void updateBattery(int i,int status) {    //此函数在电池数值更新时执行，i是当前电量，status是电池状态
        tvBattery.setText(i+"%");
        if (status == BatteryManager.BATTERY_STATUS_CHARGING){
            ivBattery.setImageResource(R.drawable.charging);
        }else {
            ivBattery.setImageResource(R.drawable.battery);
        }
    }
    @Override
    public void updateStep(int i) {    //步数步数更新时调用
        tvStep.setText(i);
    }
    @Override
    public void updateTime() {    //此函数在时间更新时执行
        int hour;
        Calendar calendar = Calendar.getInstance();
        if (DateFormat.is24HourFormat(getContext())) {
            hour = calendar.get(11);
        } else {
            hour = calendar.get(10);
            if (hour == 0) {
                hour = 12;
            }
        }
        int week = calendar.get(7);
        String minute = String.valueOf(calendar.get(12));
        String month = String.valueOf(calendar.get(2) + 1);
        String day = String.valueOf(calendar.get(5));
        ILog.i(month + "月" + day + "日，星期" + week + "，" + hour + ":" + minute);

        if (minute.indexOf(0)!=0&&minute.length()!=2) minute = "0" + minute;
        if (month.indexOf(0)!=0&&month.length()!=2) month = "0" + month;
        if (day.indexOf(0)!=0&&month.length()!=2) day = "0" + day;

        tvTime.setText(hour+":"+minute);
        tvDate.setText(month+"-"+day);

        String weekStr = "";

        switch (week){
            case 1:weekStr="SUN";
                break;
            case 2:weekStr="MON";
                break;
            case 3:weekStr="TUE";
                break;
            case 4:weekStr="WED";
                break;
            case 5:weekStr="THU";
                break;
            case 6:weekStr="FRI";
                break;
            case 7:weekStr="SAT";
                break;
            default:
                break;
        }
        tvWeek.setText(weekStr);
    }

}
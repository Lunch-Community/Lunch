package com.lazytong.launcher.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import com.lazytong.launcher.utils.TimeUtil;

import java.util.Date;

public class FormattedTextClock extends androidx.appcompat.widget.AppCompatTextView {

    private final FormattedTextClock textView;
    private Date originalTime = new Date();
    private TimeHandler mTimehandler = new TimeHandler();

    public FormattedTextClock(Context context) {
        this(context, null);
    }

    public FormattedTextClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.textView = this;
        init();
    }

    private void init() {
        try {
            updateClock();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mTimehandler.startScheduleUpdate();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOriginalTime(Date time) {
        this.originalTime = time;
        updateClock();
    }

    private void updateClock() {
        textView.setText(TimeUtil.getTimeFormatText(originalTime));
    }

    private class TimeHandler extends Handler {
        private boolean mStopped;

        private void post() {
            sendMessageDelayed(obtainMessage(0), 60000);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!mStopped) {
                updateClock();
                post();
            }
        }

        public void startScheduleUpdate() {
            mStopped = false;
            post();
        }


        public void stopScheduleUpdate() {
            mStopped = true;
            removeMessages(0);
        }
    }
}

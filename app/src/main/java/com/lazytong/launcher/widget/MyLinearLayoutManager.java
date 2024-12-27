package com.lazytong.launcher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lazytong.launcher.utils.ILog;
import java.util.ArrayList;
import java.util.List;

public class MyLinearLayoutManager extends LinearLayoutManager {
    /**
     * @param context       Current context, will be used to access resources.
     * @param orientation   Layout orientation. Should be {@link #HORIZONTAL} or {@link
     *                      #VERTICAL}.
     * @param reverseLayout When set to true, layouts from end to start.
     */
    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * Constructor used when layout manager is set in XML by RecyclerView attribute
     * "layoutManager". Defaults to vertical orientation.
     * <p>
     * {@link android.R.attr#orientation}
     * {@link androidx.recyclerview.R.attr#reverseLayout}
     * {@link androidx.recyclerview.R.attr#stackFromEnd}
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Creates a vertical LinearLayoutManager
     *
     * @param context Current context, will be used to access resources.
     */
    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    /**
     * @param recycler Recycler to use for fetching potentially cached views for a
     *                 position
     * @param state    Transient state of RecyclerView
     *                 <p>
     *                 Fixed:java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionMsgListHolder
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException err) {
            ILog.e(err.toString());
        }
    }
    
}

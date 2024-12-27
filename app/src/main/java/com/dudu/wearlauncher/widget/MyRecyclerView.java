package com.lazytong.launcher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.lazytong.launcher.utils.OverScrollDelegate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerView extends RecyclerView implements OverScrollDelegate.OverScrollable {
    private View mEmptyView;
    private OverScrollDelegate mOverScrollDelegate;
    private static final int MAX_VELOCITY = 5000;
    private int maxFlingDistance = 3000;

    private AdapterDataObserver emptyObserver =
            new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    Adapter<?> adapter =
                            getAdapter(); 
                    if (adapter != null && mEmptyView != null) {
                        if (adapter.getItemCount() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            MyRecyclerView.this.setVisibility(View.GONE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            MyRecyclerView.this.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };

    public MyRecyclerView(Context context) {
        this(context,null);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createOverScrollDelegate();
        setMaxFlingVelocity(this,MAX_VELOCITY);
        setOnFlingListener(new OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                // 计算预期的滑动距离
                int distance = computeFlingDistance(velocityY);
                if (distance > maxFlingDistance) {
                    // 限制滑动距离
                    int limitedVelocityY = calculateLimitedVelocity(velocityY);
                    fling(velocityX, limitedVelocityY);
                    return true; // 消费事件
                }
                return false; // 继续默认行为
            }
        });
    }
    
    /**
     * 根据速度计算 fling 滑动距离
     */
    private int computeFlingDistance(int velocity) {
        // 典型的滑动公式：s = v² / (2 * a)，此处假设减速度 a 为一个固定值
        final float deceleration = 0.84f; // RecyclerView 内部常用的减速度因子
        return (int) ((velocity * velocity) / (2 * Math.abs(deceleration * 1600))); // 1600 为默认滑动系数
    }

    /**
     * 根据最大滑动距离计算限制后的速度
     */
    private int calculateLimitedVelocity(int originalVelocity) {
        final float deceleration = 0.84f; // 同上，保持一致
        return (int) Math.sqrt(2 * maxFlingDistance * Math.abs(deceleration * 1600)) * (originalVelocity < 0 ? -1 : 1);
    }

    /**
     * 设置最大滑动距离
     */
    public void setMaxFlingDistance(int maxDistance) {
        this.maxFlingDistance = maxDistance;
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }
    //设定RecyclerView最大滑动速度
    private void setMaxFlingVelocity(RecyclerView recycleview, int velocity) {
        try{
            Class clazz = recycleview.getClass().getSuperclass();
            Field velocityField = clazz.getDeclaredField("mMaxFlingVelocity");
            velocityField.setAccessible(true);
            velocityField.set(recycleview, velocity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }
        emptyObserver.onChanged();
    }

    @Override
    public int superComputeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int superComputeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int superComputeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public void superOnTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
    }

    @Override
    public void superDraw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public boolean superAwakenScrollBars() {
        return super.awakenScrollBars();
    }

    @Override
    public boolean superOverScrollBy(
            int deltaX,
            int deltaY,
            int scrollX,
            int scrollY,
            int scrollRangeX,
            int scrollRangeY,
            int maxOverScrollX,
            int maxOverScrollY,
            boolean isTouchEvent) {
        return super.overScrollBy(
                deltaX,
                deltaY,
                scrollX,
                scrollY,
                scrollRangeX,
                scrollRangeY,
                maxOverScrollX,
                maxOverScrollY,
                isTouchEvent);
    }

    // ===========================================================
    // createOverScrollDelegate
    // ===========================================================
    private void createOverScrollDelegate() {
        mOverScrollDelegate = new OverScrollDelegate(this);
    }

    // ===========================================================
    // Delegate
    // ===========================================================
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mOverScrollDelegate.onInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOverScrollDelegate.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        mOverScrollDelegate.draw(canvas);
        scaleBottom();
    }

    @Override
    protected boolean overScrollBy(
            int deltaX,
            int deltaY,
            int scrollX,
            int scrollY,
            int scrollRangeX,
            int scrollRangeY,
            int maxOverScrollX,
            int maxOverScrollY,
            boolean isTouchEvent) {
        return mOverScrollDelegate.overScrollBy(
                deltaX,
                deltaY,
                scrollX,
                scrollY,
                scrollRangeX,
                scrollRangeY,
                maxOverScrollX,
                maxOverScrollY,
                isTouchEvent);
    }

    @Override
    public OverScrollDelegate getOverScrollDelegate() {
        return mOverScrollDelegate;
    }

    @Override
    public View getOverScrollableView() {
        return this;
    }

    @Override
    public boolean fling(int arg0, int arg1) {
        if(super.fling(arg0,arg1))mOverScrollDelegate.recyclerViewAbsorbGlows(arg0,arg1);
        return super.fling(arg0,arg1);
    }
    
    
    private void scaleBottom() {
        int scrollY = getScrollY();
        int measuredHeight = getMeasuredHeight();
        for (View view : listChild()) {
            if (view.getVisibility() == 0) {
                int top = view.getTop();
                int bottom = view.getBottom();
                int height = view.getHeight();
                int width = view.getWidth();
                int i = scrollY + measuredHeight;
                if (bottom >= scrollY) {
                    if (top <= i) {
                        top = (bottom <= i || top >= i) ? height : i - top;
                        float f = ((((float) top) * 0.19999999f) / ((float) height)) + 0.8f;
                        view.setPivotX(((float) width) / 2.0f);
                        view.setPivotY(0.0f);
                        view.setScaleX(f);
                        view.setScaleY(f);
                    }
                }
            }
        }
    }
    
    
    private List<View> listChild() {
        int childCount = getChildCount();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < childCount; i++) {
            arrayList.add(getChildAt(i));
        }
        return arrayList;
    }
}

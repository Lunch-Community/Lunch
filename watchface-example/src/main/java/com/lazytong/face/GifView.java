package com.lazytong.face;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.os.Build;

import java.io.InputStream;

public class GifView extends View {
    private Resources resources;
    private Movie mMovie;
    private long mMovieStart;
    private float ratioWidth;
    private float ratioHeight;
    public GifView(Context context) {
        this(context,null);
    }

    public GifView(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        resources = context.getResources();
    }

    public void setGifResource(InputStream resource) {
        mMovie = Movie.decodeStream(resource);
        requestLayout();
    }
    public  void setGifStream(InputStream is){
        mMovie = Movie.decodeStream(is);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie!=null){
            int w = mMovie.width();
            int h = mMovie.height();
            if (w<=0){
                w=1;
            }
            if (h<=0){
                h=1;
            }
            int pLeft = getPaddingLeft();
            int pRight = getPaddingRight();
            int pTop = getPaddingTop();
            int pBottom = getPaddingBottom();
            int widthSize;
            int heightSize;
            w+=pLeft+pRight;
            h+=pTop+pBottom;
            w=Math.max(w,getSuggestedMinimumWidth());
            h=Math.max(h,getSuggestedMinimumHeight());
            widthSize= resolveSizeAndState(w,widthMeasureSpec,0);
            heightSize= resolveSizeAndState(h,heightMeasureSpec,0);
            ratioWidth = (float) widthSize/w;
            ratioHeight = (float) heightSize/h;
            setMeasuredDimension(widthSize,heightSize);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = SystemClock.uptimeMillis();
        if (mMovieStart ==0){ //第一次进入
            mMovieStart =now;
        }
        if (mMovie!=null){
            int dur = mMovie.duration();
            if (dur==0){
                dur=1000;
            }
            int relTime= (int) ((now-mMovieStart)%dur);
            mMovie.setTime(relTime);
            //  mMovie.draw(canvas,0,0);
            float scale=Math.min(ratioWidth,ratioHeight);
            canvas.scale(scale,scale);
            mMovie.draw(canvas,0,0);
            invalidate();
        }
    }
}

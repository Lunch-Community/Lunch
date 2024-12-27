package com.lazytong.launcher.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lazytong.launcher.R;
import com.lazytong.launcher.model.WatchFaceInfo;
import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.utils.DensityUtil;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
import com.lazytong.launcher.utils.WatchFaceHelper;
import com.lazytong.launcher.widget.MyRecyclerView;
import java.util.List;

public class AddWatchfaceActivity extends BaseActivity {
    MyRecyclerView recycler;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_add_watchface);
        recycler = findViewById(R.id.watchface_list);
        WatchfaceListAdapter adapter = new WatchfaceListAdapter(this,WatchFaceHelper.getWatchfaces());
        recycler.setLayoutManager(new GridLayoutManager(this,2));
        recycler.setEmptyView(findViewById(R.id.empty_view));
        recycler.setAdapter(adapter);
    }
    
    
    public class WatchfaceListAdapter extends RecyclerView.Adapter<WatchfaceListAdapter.WatchfaceHolder> {
        Context context;
        List<WatchFaceInfo> list;
        public WatchfaceListAdapter(Context context,List<WatchFaceInfo> list){
            this.context = context;
            this.list = list;
        }
	    @Override
	    public int getItemCount() {
	        return list.size();
	    }
        @Override
        public WatchfaceHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.item_watchface,arg0,false);
            return new WatchfaceHolder(view);
        }
        
        @Override
        public void onBindViewHolder(WatchfaceHolder arg0, int arg1) {
            WatchFaceInfo watchface = list.get(arg1);
            RequestOptions options = new RequestOptions()
                .bitmapTransform(new RoundedCorners(DensityUtil.dip2px(context,5)));
            Glide.with(context)
                .load(watchface.preview)
                .apply(options)
                .into(arg0.img);
            arg0.text.setText(watchface.displayName);
            ((View)arg0.img.getParent()).setOnClickListener(v->{
                List<String> originWatchfaceList = SharedPreferencesUtil.getListData(SharedPreferencesUtil.SHOWING_WATCHFACE_LIST,String.class);
                originWatchfaceList.add(watchface.packageName);
                SharedPreferencesUtil.putListData(SharedPreferencesUtil.SHOWING_WATCHFACE_LIST,originWatchfaceList);
                SharedPreferencesUtil.putData(SharedPreferencesUtil.NOW_WATCHFACE,watchface.packageName);
                ((Activity)context).finish();
            });
        }
        
	    public class WatchfaceHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView text;
	    	public WatchfaceHolder(View itemView){
                super(itemView);
                img = itemView.findViewById(R.id.watchface_preview_iv);
                text = itemView.findViewById(R.id.watchface_name);
            }
	    }
    }
}

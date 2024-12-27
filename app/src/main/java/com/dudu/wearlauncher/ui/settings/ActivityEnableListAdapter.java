package com.lazytong.launcher.ui.settings;



import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazytong.launcher.R;

import com.lazytong.launcher.utils.DensityUtil;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ActivityEnableListAdapter extends RecyclerView.Adapter<ActivityEnableListAdapter.ActivityEnableListHolder> {

    Context context;
    List<ResolveInfo> appList;
    List<String> buildinHiddenList;
    List<String> hiddenList;

    public ActivityEnableListAdapter(Context context, List<ResolveInfo> appList) {
        this.context = context;
        this.appList = appList;
        this.buildinHiddenList = Arrays.asList(context.getResources().getStringArray(R.array.hidden_activities));
        this.hiddenList = new ArrayList();
        hiddenList.addAll(Arrays.asList(((String)SharedPreferencesUtil.getData(SharedPreferencesUtil.HIDDEN_ACTIVITIES,"")).split(":")));
    }

    @NonNull
    @Override
    public ActivityEnableListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_app_linear,parent,false);
        return new ActivityEnableListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityEnableListHolder holder, int position) {
        ActivityInfo activityInfo = appList.get(position).activityInfo;


        Glide.with(context).load(activityInfo.loadIcon(context.getPackageManager()))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .override(DensityUtil.dip2px(context,48),DensityUtil.dip2px(context,48))
                .thumbnail(0.12f)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        holder.appIcon.setImageDrawable(resource);
                    }
                });
        holder.appName.setText(activityInfo.loadLabel(context.getPackageManager()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClassName(activityInfo.packageName,activityInfo.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        if(hiddenList.contains(activityInfo.packageName+"/"+activityInfo.name)) {
        	holder.appSwitch.setChecked(true);
        }else{
            holder.appSwitch.setChecked(false);
        }
        if(buildinHiddenList.contains(activityInfo.packageName+"/"+activityInfo.name)) {
            holder.appSwitch.setEnabled(false);
        }else{
            holder.appSwitch.setEnabled(true);
        }
        holder.appSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if(arg1) hiddenList.add(activityInfo.packageName+"/"+activityInfo.name);
                else hiddenList.remove(activityInfo.packageName+"/"+activityInfo.name);
                save();
            }
            
        });
    }
    
    @Override
    public int getItemViewType(int pos) {
    	return pos;
    }
    
    public void save() {
        StringBuilder builder = new StringBuilder();
    	for(String i : hiddenList) {
    		builder.append(i);
            if(hiddenList.lastIndexOf(i)!=hiddenList.size()) builder.append(":");
    	}
        SharedPreferencesUtil.putData(SharedPreferencesUtil.HIDDEN_ACTIVITIES,builder.toString());
    }
    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ActivityEnableListHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;
        TextView appName;
        Switch appSwitch;

        public ActivityEnableListHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name);
            appIcon = itemView.findViewById(R.id.app_icon);
            appSwitch = itemView.findViewById(R.id.app_switch);
        }
    }
}
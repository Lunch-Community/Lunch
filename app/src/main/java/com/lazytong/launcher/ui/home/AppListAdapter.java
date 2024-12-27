package com.lazytong.launcher.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lazytong.launcher.R;

import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.utils.DensityUtil;
import com.lazytong.launcher.utils.ILog;
import java.util.List;
import java.util.Map;


public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListHolder> {

    Context context;
    List<ResolveInfo> appList;
    Map<String,Drawable> iconMap;
    String mode;
    public AppListAdapter(Context context, List<ResolveInfo> appList,Map<String,Drawable> iconMap,String mode) {
        this.context = context;
        this.appList = appList;
        this.iconMap = iconMap;
        this.mode = mode;
        
    }
    @Override
    public void onViewRecycled(AppListHolder holder){
        holder.appIcon.setImageBitmap(null);
    }
    @NonNull
    @Override
    public AppListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(mode.equals("linear")) {
        	view = LayoutInflater.from(this.context).inflate(R.layout.item_app_linear,parent,false);
        }if(mode.equals("grid")) {
        	view = LayoutInflater.from(this.context).inflate(R.layout.item_app_grid,parent,false);
        }
        return new AppListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListHolder holder, int position) {
        ActivityInfo activityInfo = appList.get(position).activityInfo;

        Drawable icon;
        if(iconMap.containsKey(activityInfo.packageName+"/"+activityInfo.name)) {
            icon = iconMap.get(activityInfo.packageName+"/"+activityInfo.name);
        }else{
            icon = activityInfo.loadIcon(context.getPackageManager());
        }
        holder.appIcon.setImageBitmap(ImageUtils.toRound(ImageUtils.drawable2Bitmap(icon)));
        
        /*Glide.with(context).load(icon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .dontTransform()
                .apply(RequestOptions.circleCropTransform())
                .override(DensityUtil.dip2px(context,48),DensityUtil.dip2px(context,48))
                .into(holder.appIcon);*/
        if(mode.equals("linear")) {
        	holder.appName.setText(activityInfo.loadLabel(context.getPackageManager()));
            holder.appName.setMaxWidth(DensityUtil.dip2px(context,90));
            holder.appSwitch.setVisibility(View.GONE);
        }
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
        holder.itemView.setOnLongClickListener(v->{
            Uri uri = Uri.fromParts("package", activityInfo.packageName, null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            context.startActivity(intent);
            ((BaseActivity)context).overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
    
    public static class AppListHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;
        TextView appName;
        Switch appSwitch;

        public AppListHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name);
            appIcon = itemView.findViewById(R.id.app_icon);
            appSwitch = itemView.findViewById(R.id.app_switch);
        }
    }
}
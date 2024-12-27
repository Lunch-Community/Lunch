package com.lazytong.launcher.ui.home;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lazytong.launcher.R;
import com.lazytong.launcher.model.Notification;
import com.lazytong.launcher.utils.DensityUtil;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.widget.FormattedTextClock;
import com.google.android.material.card.MaterialCardView;
import java.util.concurrent.CancellationException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MsgListHolder> {
    Context context;
    List<Notification> msgList;

    public MsgListAdapter(Context context, List<Notification> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    @NotNull
    @Override
    public MsgListHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_msg, parent, false);
        return new MsgListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull MsgListHolder holder, int position) {
        Notification notification = msgList.get(position);
        Glide.with(context).load(notification.icon.loadDrawable(context))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                        holder.msgImg.setImageDrawable(resource);
                    }
                });
        holder.msgAppName.setText(notification.appName);
        holder.msgTime.setOriginalTime(new Date(notification.time));
        holder.msgTitle.setText(notification.title);
        holder.msgContent.setText(notification.content);
        
        holder.delBtn.setOnClickListener(v -> {
            Intent intent = new Intent("com.lazytong.launcher.NOTIFICATION_LISTENER");
            intent.putExtra("command", "cancelMsg");
            intent.putExtra("key", notification.key);
            context.sendBroadcast(intent);
        });
        
        holder.msgCard.setOnClickListener(v->{
            try{
                notification.intent.send();
            }catch(PendingIntent.CanceledException e){
                ILog.e("打开PendingIntent失败"+e.getMessage());
            }catch(NullPointerException e){
                ILog.e("打开PendingIntent失败"+e.getMessage());
            }
            
        });
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void addSbn(Notification sbn) {
        for (Notification notification : msgList) {
            if (notification.key.equals(sbn.key)) {
                Collections.replaceAll(msgList, notification, sbn);
                return;
            }
        }
        msgList.add(sbn);
        notifyItemInserted(getItemCount());
    }

    public void removeSbn(Notification sbn) {
        //new Thread(()->{
        while (true) {
            try {
                int pos = msgList.indexOf(sbn);
                if (pos == -1) {
                    break;
                }
                msgList.remove(sbn);
                notifyItemRemoved(pos);
            } catch (IndexOutOfBoundsException e) {
                ILog.e(e.getMessage() + "sbn is not available,skipped");
                break;
            }
        }
        //}).start();
    }

    public static class MsgListHolder extends RecyclerView.ViewHolder {
        ImageView msgImg;
        FormattedTextClock msgTime;
        TextView msgAppName, msgTitle, msgContent;
        ImageButton delBtn;
        MaterialCardView msgCard;
        public MsgListHolder(View itemView) {
            super(itemView);
            msgImg = itemView.findViewById(R.id.msg_icon);
            msgAppName = itemView.findViewById(R.id.msg_app_name);
            msgTime = itemView.findViewById(R.id.msg_time);
            msgTitle = itemView.findViewById(R.id.msg_title);
            msgContent = itemView.findViewById(R.id.msg_content);
            delBtn = itemView.findViewById(R.id.del_msg_btn);
            msgCard = itemView.findViewById(R.id.item_msg_main_card);
        }
    }
}

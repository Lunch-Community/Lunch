package com.lazytong.launcher.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lazytong.launcher.R;
import com.lazytong.launcher.model.IconPack;
import com.lazytong.launcher.utils.DensityUtil;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import java.util.List;
import me.shihao.library.XRadioGroup;

public class IconPackAdapter extends RecyclerView.Adapter<IconPackAdapter.IconPackHolder> {

    Context context;
    List<IconPack> iconPackList;

    public IconPackAdapter(Context context, List<IconPack> iconPackList) {
        this.context = context;
        this.iconPackList = iconPackList;
    }

    @NonNull
    @Override
    public IconPackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(this.context).inflate(R.layout.item_icon_pack, parent, false);
        return new IconPackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconPackHolder holder, int position) {
        IconPack iconPack = iconPackList.get(position);

        Glide.with(context)
                .load(iconPack.icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .override(DensityUtil.dip2px(context, 48), DensityUtil.dip2px(context, 48))
                .thumbnail(0.12f)
                .into(holder.iconPackImg);

        holder.iconPackName.setText(iconPack.name);

        holder.itemView.setOnClickListener(
                view -> {
                    RecyclerView recycler = (RecyclerView)view.getParent();
                    for(int i = 0; i < recycler.getChildCount(); ++i) {
                        MaterialCardView card = (MaterialCardView)recycler.getChildAt(i);
                        ConstraintLayout container = (ConstraintLayout)card.getChildAt(0);
                        for(int j = 0; j < container.getChildCount(); ++j) {
                            if(container.getChildAt(j) instanceof MaterialRadioButton) {
                            	((MaterialRadioButton)container.getChildAt(j)).setChecked(false);
                            }
                        }
                        container.setBackground(null);
                    }
                    holder.iconPackSwitch.setChecked(true);
                    SharedPreferencesUtil.putData(SharedPreferencesUtil.ICON_PACK,iconPack.packageName);
                    ((ViewGroup)holder.itemView).getChildAt(0).setBackgroundResource(R.drawable.chip_toggled);
                });
        if(SharedPreferencesUtil.getData(SharedPreferencesUtil.ICON_PACK,"default").equals(iconPack.packageName)) {
            holder.iconPackSwitch.setChecked(true);
            ((ConstraintLayout)holder.iconPackSwitch.getParent()).setBackgroundResource(R.drawable.chip_toggled);
        }
    }

    @Override
    public int getItemCount() {
        return iconPackList.size();
    }

    public static class IconPackHolder extends RecyclerView.ViewHolder {
        ImageView iconPackImg;
        TextView iconPackName;
        MaterialRadioButton iconPackSwitch;

        public IconPackHolder(@NonNull View itemView) {
            super(itemView);
            iconPackName = itemView.findViewById(R.id.icon_pack_name);
            iconPackImg = itemView.findViewById(R.id.icon_pack_image);
            iconPackSwitch = itemView.findViewById(R.id.icon_pack_radio_btn);
        }
    }
}

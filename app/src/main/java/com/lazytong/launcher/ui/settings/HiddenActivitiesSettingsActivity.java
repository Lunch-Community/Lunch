package com.lazytong.launcher.ui.settings;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lazytong.launcher.R;
import com.lazytong.launcher.ui.BaseActivity;
import com.lazytong.launcher.utils.PmUtils;
import com.lazytong.launcher.widget.MyLinearLayoutManager;
import com.lazytong.launcher.widget.MyRecyclerView;
import java.util.List;

public class HiddenActivitiesSettingsActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_activities);
        List<ResolveInfo> appList = PmUtils.getAllApps();
        MyRecyclerView recycler = findViewById(R.id.activity_list);
        ActivityEnableListAdapter adapter = new ActivityEnableListAdapter(this,appList);
        recycler.setLayoutManager(new MyLinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent("com.lazytong.launcher.REFESH_APP_LIST");
        sendBroadcast(intent);
        super.onDestroy();
    }
    
}

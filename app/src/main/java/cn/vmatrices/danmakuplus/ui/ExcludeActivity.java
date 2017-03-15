package cn.vmatrices.danmakuplus.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.adapter.AppInfoAdapter;
import cn.vmatrices.danmakuplus.tool.AppInfo;
import cn.vmatrices.danmakuplus.tool.Common;

public class ExcludeActivity extends AppCompatActivity implements Runnable {

    private ArrayList<AppInfo> appInfos;
    private Handler handler;
    private ListView listView;
    private AppInfoAdapter adapter;
    private SharedPreferences preferences;
    private SearchView searchView;
    private Animation animation1;
    private Animation animation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exclude);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        appInfos = new ArrayList<>();
        handler = new Handler();

        animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_pull_in);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_pull_out);

        listView = (ListView) findViewById(R.id.applist);
        adapter = new AppInfoAdapter(this, R.layout.item_appinfo, appInfos);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.startAnimation(animation1);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.startAnimation(animation2);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.isEmpty()) {
                    adapter.getFilter().filter(s);
                } else {
                    adapter.getFilter().filter("");
                }
                return false;
            }
        });

        CheckBox checkBox = (CheckBox) findViewById(R.id.select_all);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (AppInfo appInfo : appInfos) {
                    appInfo.setChecked(isChecked);
                }
                adapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.snackbar_setting_saved, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                update();
            }
        });


        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        handler.postDelayed(this, 500);
    }

    @Override
    public void run() {
        PackageManager pm = this.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if (!packageInfo.packageName.equals(packageInfo.applicationInfo.loadLabel(pm))) {
                AppInfo info = new AppInfo();
                info.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                info.setPkgName(packageInfo.packageName);
                info.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
                appInfos.add(info);
            }
        }

        String[] str = preferences.getString(Common.PREF_EXCLUDE_APP, "").split("/");
        if (str.length > 0) {
            for (AppInfo appInfo : appInfos) {
                for (int i = 0; i < str.length; i++) {
                    if (str[i].equals(appInfo.getPkgName())) {
                        appInfo.setChecked(true);
                        break;
                    }
                }
            }
        }

        //TODO 排序未完成
//        appInfos.sort(new Comparator<AppInfo>() {
//            @Override
//            public int compare(AppInfo o1, AppInfo o2) {
//                return 0;
//            }
//        });
        adapter.notifyDataSetChanged();

        findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void update() {
        String str = new String();
        for (AppInfo appInfo : appInfos) {
            if (appInfo.getChecked()) {
                str += appInfo.getPkgName() + "/";
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Common.PREF_EXCLUDE_APP, str);
        editor.commit();

        Intent intent = new Intent();
        intent.setAction(Common.ACTION_UPDATE);
        sendBroadcast(intent);
    }

}

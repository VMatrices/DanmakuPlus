package cn.vmatrices.danmakuplus.ui;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.fragment.SettingsFragment;
import cn.vmatrices.danmakuplus.service.DanmakuRainService;
import cn.vmatrices.danmakuplus.tool.Common;
import cn.vmatrices.danmakuplus.tool.PermissionChecker;

public class MainActivity extends AppCompatActivity {

    private SettingsFragment mSettingsFragment;
    private ImageView imageView;

    private Intent rainService = new Intent();
    private boolean isRaining = false;

    private static int[] BG_DAILY = {
            R.drawable.bg_0, R.drawable.bg_1,
            R.drawable.bg_2, R.drawable.bg_3,
            R.drawable.bg_4, R.drawable.bg_5,
            R.drawable.bg_6, R.drawable.bg_7,
            R.drawable.bg_8, R.drawable.bg_9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.setting_content, mSettingsFragment).commit();
        }

        init();
    }

    private void init() {
        rainService = new Intent(MainActivity.this, DanmakuRainService.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRain(view);
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(Math.random() > 0.5f ? BG_DAILY[0] : BG_DAILY[(int) (Math.random() * 10)]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission()) return;
                sendNotification(view);
            }
        });

        stopService(rainService);
        checkPermission();

    }

    private boolean checkPermission() {
        if (!PermissionChecker.check(this)) {
            startActivity(new Intent(this, PermissionActivity.class));
            return false;
        }
        return true;
    }

    public void sendNotification(View view) {
        final Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        int index = (int) (Math.random() * Common.strings.length);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(Common.strings[index]);
        builder.setTicker(Common.strings[index]);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }

    public void startRain(View view) {
        if (isRaining) {
            isRaining = false;
            stopService(rainService);
            Snackbar.make(view, R.string.snackbar_rain_stop, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            if (!checkPermission()) return;
            isRaining = true;
            startService(rainService);
            Snackbar.make(view, R.string.snackbar_rain_start, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}

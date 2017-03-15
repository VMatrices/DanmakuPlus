package cn.vmatrices.danmakuplus.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.tool.Common;
import cn.vmatrices.danmakuplus.tool.DanmakuManager;
import cn.vmatrices.danmakuplus.tool.PermissionChecker;

/**
 * Created by iBelieve on 2017/2/12.
 */

public class DanmakuNotificationListener extends NotificationListenerService implements SensorEventListener {

    private final static int HANDLER_UPDATE = 0;
    private final static int HANDLER_TEXT = 1;
    private final static int HANDLER_TICKER = 2;
    private static final int HANDLER_CLEAR = 3;

    RelativeLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;

    public DanmakuManager danmakuManager;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Vibrator vibrator;

    private String title;
    private String text;
    private String ticker;

    private BCReceiver mReceiver;
    private SharedPreferences preferences;
    private List<String> excludeWord;
    private boolean enable;
    private boolean shakeClear;

    private MyHandler myHandler = new MyHandler();
    private boolean isAddedView = false;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            init();
        } catch (Exception e) {
            stopSelf();
        }
        Toast.makeText(this, R.string.toast_running, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (enable) {
            super.onNotificationPosted(sbn);
            try {
                if (excludeWord.contains(sbn.getPackageName())) {
                    return;
                }
                Notification notification = sbn.getNotification();
                title = notification.extras.getString(Notification.EXTRA_TITLE);
                text = notification.extras.getString(Notification.EXTRA_TEXT);

                if (text == null) {  //过滤空消息
                    if (notification.tickerText != null) {
                        ticker = notification.tickerText.toString();
                        if (ticker.length() > 1) {
                            myHandler.sendEmptyMessage(HANDLER_TICKER);
//                            System.out.println(ticker);
                        }
                    }
                } else {
                    if (!text.isEmpty()) {
//                        System.out.println(text);
                        myHandler.sendEmptyMessage(HANDLER_TEXT);
                    }
                }

            } catch (NullPointerException e) {
                Toast.makeText(this, R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, R.string.toast_quit, Toast.LENGTH_SHORT).show();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    private long perShakeTime = 0;
    private int shakeCount=0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {

            float[] values = event.values;
            float x = values[0];
            float y = values[1];
//            float z = values[2];
            if (Math.abs(x) > 19 || Math.abs(y) > 19 ) {
                if (System.currentTimeMillis() - perShakeTime < 200) {
                    shakeCount++;
                    if(shakeCount>4) {
                        if (!danmakuManager.isEmpty()) {
                            vibrator.vibrate(200);
                            danmakuManager.clear();
                        }
                        shakeCount=0;
                    }
                }
                perShakeTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void init() {
        createFloatView();
        System.out.println(PermissionChecker.check(this));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        if (mSensorManager != null) {
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        update(); //注意顺序

        mReceiver = new BCReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.ACTION_UPDATE);
        filter.addAction(Common.ACTION_SEND);
        filter.addAction(Common.ACTION_CLEAR);
        registerReceiver(mReceiver, filter);
    }

    private void update() {
        addView();

        excludeWord = Arrays.asList(preferences.getString(Common.PREF_EXCLUDE_APP, "").split("/"));
        enable = preferences.getBoolean(Common.PREF_ENABLE, true);

        shakeClear = preferences.getBoolean(Common.PREF_SHAKECLEAR, true); //开启关闭加速传感器
        if (mAccelerometerSensor != null) {
            if (shakeClear) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                mSensorManager.unregisterListener(this);
            }
        }

        danmakuManager.update();
    }

    private void createFloatView() {

        wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888
        );

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        mFloatLayout = new RelativeLayout(this);
        mFloatLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        danmakuManager = new DanmakuManager(this, mFloatLayout);

        addView();
    }

    private void addView() {
        if (isAddedView) { //判读是否已经添加过悬浮窗
            if (!PermissionChecker.canShowFloatWind(this)) { //如果已经添加，又被禁止显示
                isAddedView = false;
            }
        } else {
            if (PermissionChecker.canShowFloatWind(this)) { //如果未添加，且允许显示
                mWindowManager.addView(mFloatLayout, wmParams);
                isAddedView = true;
            }
        }
    }

    private class BCReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                final String action = intent.getAction();
                if (action.equals(Common.ACTION_UPDATE)) {
                    myHandler.sendEmptyMessage(HANDLER_UPDATE);
                } else if (action.equals(Common.ACTION_SEND)) {
                    ticker = intent.getExtras().getString("text");
                    if (ticker != null) {
                        myHandler.sendEmptyMessage(HANDLER_TICKER);
                    }
                } else if (action.equals(Common.ACTION_CLEAR)) {
                    myHandler.sendEmptyMessage(HANDLER_CLEAR);

                }
            }
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_UPDATE:
                    update();
                    break;
                case HANDLER_TICKER:
                    danmakuManager.send(ticker);
                    break;
                case HANDLER_TEXT:
                    danmakuManager.send(title, text);
                    break;
                case HANDLER_CLEAR:
                    danmakuManager.clear();
                    break;
            }
        }
    }


}

package cn.vmatrices.danmakuplus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import cn.vmatrices.danmakuplus.tool.Common;

public class DanmakuRainService extends Service implements Runnable {

    private Intent intent = new Intent();
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(this, 500);
    }

    @Override
    public void onDestroy() {
        intent.setAction(Common.ACTION_CLEAR);
        sendBroadcast(intent);
        handler.removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        int index = (int) (Math.random() * Common.strings.length);

        intent.setAction(Common.ACTION_SEND);
        intent.putExtra("text", Common.strings[index]);
        sendBroadcast(intent);

        handler.postDelayed(this, 250);
    }

}

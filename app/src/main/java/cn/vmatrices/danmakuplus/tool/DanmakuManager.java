package cn.vmatrices.danmakuplus.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import cn.vmatrices.danmakuplus.R;

/**
 * Created by iBelieve on 2017/2/8.
 */

public class DanmakuManager implements Runnable {

    private Context context;
    private RelativeLayout srcLayout;

    private static int DANMAKU_NUM = 100;
    private DanmakuItem[] queue = new DanmakuItem[DANMAKU_NUM];
    private boolean running = false;

    SharedPreferences preferences;

    private float speed;
    private String posotion;
    private boolean fontBold;
    private int fontColor;
    private Boolean fontColorRandom;
    private int bgColor;
    private Boolean bgColorRandom;
    private Boolean bgEnable;
    private Boolean singleTitle;
    private List<String> excludeWord;
    private float fontSize;
    private boolean excludeSame;
    private long preTime = 0;

    /*
        Getter
     */

    public Boolean getFontBold() {
        return fontBold;
    }

    public Boolean getFontColorRandom() {
        return fontColorRandom;
    }

    public int getFontColor() {
        return fontColor;
    }

    public float getFontSize() {
        return fontSize;
    }

    public String getPosotion() {
        return posotion;
    }

    public float getSpeed() {
        return speed;
    }

    public int getBgColor() {
        return bgColor;
    }

    public Boolean getBgColorRandom() {
        return bgColorRandom;
    }

    public Boolean getBgEnable() {
        return bgEnable;
    }

    public Boolean getSingleTitle() {
        return singleTitle;
    }

    public DanmakuManager(Context context, RelativeLayout srcLayout) {
        this.context = context;
        this.srcLayout = srcLayout;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (int i = 0; i < DANMAKU_NUM; i++) {
            queue[i] = new DanmakuItem(context);
        }
        update();
    }

    private int index = 0;

    public void send(String text) {

        send("none", text);
    }

    public void send(String title, String text) {
        try {
            if (excludeSame) {
                long time = System.currentTimeMillis();
                if (time - preTime < 500) {
                    return;
                }
                preTime = time;
            }
            if (text != null && title != null) {
                if (checkWord(text)) {
                    if (!queue[index].availiable) {
                        srcLayout.addView(queue[index].getView());
                        num++;
                    }
                    queue[index].load(title, text, this);
                    index++;
                    if (index == DANMAKU_NUM) {
                        index = 0;
                    }
                    if (!running) {
                        handler.post(this);

                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.toast_error + " CODE", Toast.LENGTH_SHORT).show();
        }

    }

    public void clear() {
        for (int i = 0; i < DANMAKU_NUM; i++) {
            if (queue[i].availiable) {
                queue[i].clear();
            }
        }
    }

    private Handler handler = new Handler();

    private int num = 0;

    @Override
    public void run() {
        running = true;
        for (int i = 0; i < DANMAKU_NUM; i++) {
            if (queue[i].availiable) {
                if (!queue[i].move()) {
                    num--;
                    srcLayout.removeView(queue[i].getView());
                }
            }
        }

        if (num > 0) {
            handler.postDelayed(this, 16);
        } else {
            running = false;
        }
    }

    public boolean checkWord(String str) {
        for (String wd : excludeWord) {
            if (!wd.isEmpty() && str.indexOf(wd) >= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        //TODO 弹幕清空效果未完成
//        Toast.makeText(context, "NUM:" + num, Toast.LENGTH_SHORT).show();
        return num == 0;
    }

    public void update() {
        speed = preferences.getInt(Common.PREF_SPEED, 20) / 20f;
        posotion = preferences.getString(Common.PREF_POSITION, "full");
        fontSize = preferences.getInt(Common.PREF_FONT_SIZE, 20) / 10f;
        fontBold = preferences.getBoolean(Common.PREF_FONT_BOLD, true);
        fontColor = preferences.getInt(Common.PREF_COLOR_FONT, Color.WHITE);
        fontColorRandom = preferences.getBoolean(Common.PREF_COLOR_FONT_RANDOM, true);
        bgColor = preferences.getInt(Common.PREF_COLOR_BG, Color.BLACK);
        bgColorRandom = preferences.getBoolean(Common.PREF_COLOR_BG_RANDOM, false);
        bgEnable = preferences.getBoolean(Common.PREF_COLOR_BG_ENABLE, true);
        singleTitle = preferences.getBoolean(Common.PREF_SINGLE_TITLE, true);
        excludeSame = preferences.getBoolean(Common.PREF_EXCLUDE_SAME, true);
        excludeWord = Arrays.asList(preferences.getString(Common.PREF_EXCLUDE_WORD, "").split(","));

    }
}

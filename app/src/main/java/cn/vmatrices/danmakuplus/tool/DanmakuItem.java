package cn.vmatrices.danmakuplus.tool;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import cn.vmatrices.danmakuplus.R;

/**
 * Created by iBelieve on 2017/2/8.
 */

public class DanmakuItem {

    private TextView textView;
    private TextView titleView;

    private int style;
    private float speed;
    private float size;

    private View view;

    public boolean availiable = false;
    private long startTime = 0;

    private int screen_width = 0;
    private int screen_height = 0;

    private Animation animation;
    private Context context;
    private GradientDrawable background;

    public DanmakuItem(Context context) {
        this.context = context;
        LayoutInflater mInflater = LayoutInflater.from(context);

        view = mInflater.inflate(R.layout.item_danmaku, null);
        textView = (TextView) view.findViewById(R.id.danmaku_text);
        titleView = (TextView) view.findViewById(R.id.danmaku_titie);
        background = (GradientDrawable) view.getBackground();

        animation = new AlphaAnimation(1f, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                availiable = false;  //有问题，不能在这里消去
                isCleaning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    boolean isCleaning = false;

    public void clear() {
        if (!isCleaning) {
            isCleaning = true;
            view.startAnimation(animation);
        }
    }

    public void load(String title, String text, DanmakuManager dv) {


        if (!isCleaning) {

            screen_width = context.getResources().getDisplayMetrics().widthPixels;
            screen_height = context.getResources().getDisplayMetrics().heightPixels;

            if (title.equals("none")) { //设置弹幕显示方式
                titleView.setVisibility(View.GONE);
            } else {
                if (!dv.getSingleTitle()) {
                    titleView.setVisibility(View.GONE);
                    text = title + ": " + text;
                } else {
                    titleView.setText(title);
                    titleView.setVisibility(View.VISIBLE);
                }
            }
            textView.setText(text);

            view.setX(screen_width);
            switch (dv.getPosotion()) { //设置弹幕位置
                case "full":
                    view.setY((int) (Math.random() * 19) / 20f * screen_height);
                    break;
                case "top":
                    view.setY((int) (Math.random() * 6) / 20f * screen_height);
                    break;
                case "middle":
                    view.setY((int) (Math.random() * 5 + 7) / 20f * screen_height);
                    break;
                case "bottom":
                    view.setY((int) (Math.random() * 6 + 13) / 20f * screen_height);
                    break;
            }

            if (dv.getSpeed() < 0.1) { //设置弹幕速度
                speed = (float) (screen_width / (5f / (Math.random() * 1.2f + 0.8f)));
            } else {
                speed = screen_width / (5f / dv.getSpeed());
                System.out.println(dv.getSpeed());
            }

            if (dv.getBgEnable()) { //设置弹幕背景
                background.setAlpha(150);
                if (dv.getBgColorRandom()) {
                    background.setColor(LinearColor.getColor((float) Math.random(), 0.25f));
                } else {
                    background.setColor(dv.getBgColor());
                }
            } else {
                background.setAlpha(0);
            }

            if (dv.getFontColorRandom()) { //设置字体颜色
                final int color = LinearColor.getColor((float) Math.random(), 0.75f);
                textView.setTextColor(color);
                titleView.setTextColor(color);
            } else {
                textView.setTextColor(dv.getFontColor());
                titleView.setTextColor(dv.getFontColor());
            }

            if (dv.getFontSize() == 0) { //设置字体大小
                final float scale = (float) (Math.random() * 2 + 2);
                textView.setTextSize(7 * scale);
                titleView.setTextSize(5 * scale);
            } else {
                textView.setTextSize(7 * dv.getFontSize());
                titleView.setTextSize(5 * dv.getFontSize());
            }

            if (dv.getFontBold()) { //设置字体加粗
                titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            } else {
                titleView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            }


            startTime = System.currentTimeMillis();
            availiable = true;
        }
    }

    public boolean move() {
        if (availiable) {
            long time = System.currentTimeMillis() - startTime;
            float posX = screen_width - time * speed / 1000f;
            view.setX(posX);
            if (view.getWidth() > 0 && posX < -view.getWidth()) {
                availiable = false;
            }
        }
        return availiable;
    }

    public View getView() {
        return view;
    }
}

package cn.vmatrices.danmakuplus.tool;

import android.graphics.Color;

/**
 * Created by iBelieve on 2017/3/4.
 */

public class LinearColor {

    /*
        index 颜色位置 float:0~1
        min 颜色开始值 int:0~255
        max 颜色结束值 int:0~255
        (min<=max)
     */
    public static int getColor(float index, int min, int max) {
        if (max > 255 || min > 255 || max < 0 || min < 0 || min >= max)
            return Color.BLACK;
        int d = max - min;

        int n = (int) (3f * d * index);
        if (n < d) {
            return Color.rgb(d - n + min, n + min, min);
        }
        if (n < d * 2) {
            n -= d;
            return Color.rgb(min, d - n + min, n + min);
        }
        if (n < d * 3) {
            n -= 2 * d;
            return Color.rgb(n + min, min, d - n + min);
        }
        return getColor(0, min, max);
    }


    public static int getColor(float index, float light) {
        int color = getColor(index, 0, 255);

        int dx = (int) (512 * light - 256);

        int red = Color.red(color) + dx;
        int green = Color.green(color) + dx;
        int blue = Color.blue(color) + dx;

        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;
        if (red < 0) red = 0;
        if (green < 0) green = 0;
        if (blue < 0) blue = 0;

        return Color.rgb(red, green, blue);

    }
}

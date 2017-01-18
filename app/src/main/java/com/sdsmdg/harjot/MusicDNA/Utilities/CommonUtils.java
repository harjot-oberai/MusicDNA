package com.sdsmdg.harjot.MusicDNA.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;

/**
 * Created by Harjot on 18-Jan-17.
 */

public class CommonUtils {

    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dpTopx(int dp, Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static boolean hasNavBar(Context ctx) {
        int id = ctx.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && ctx.getResources().getBoolean(id);
    }

    public static int getDarkColor(int color) {
        int darkColor = 0;

        int r = Math.max(Color.red(color) - 25, 0);
        int g = Math.max(Color.green(color) - 25, 0);
        int b = Math.max(Color.blue(color) - 25, 0);

        darkColor = Color.rgb(r, g, b);

        return darkColor;
    }

}

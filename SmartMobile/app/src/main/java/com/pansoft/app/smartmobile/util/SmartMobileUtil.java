package com.pansoft.app.smartmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.pansoft.app.smartmobile.constant.SmartMobileConstant;
import com.pansoft.app.smartmobile.view.TaskItembean;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by skylin on 2016-8-12.
 */
public class SmartMobileUtil {
    private static final String APP_DEFAULT_PROTOCOL     = "http";
    private static final String APP_DEFAULT_HOST         = "10.143.128.102";
    private static final String APP_DEFAULT_PORT         = "8082";
    private static final String APP_DEFAULT_APPLICATION  = "KLRQ";
    private static final int    APP_DEFAULT_LOGINMODE    = 1;

    public static String getAppProtocol(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, Context.MODE_PRIVATE);
        return preferences.getString(SmartMobileConstant.APP_SETTING_PROTOCOL, APP_DEFAULT_PROTOCOL);
    }

    public static String getAppHost(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, Context.MODE_PRIVATE);
        return preferences.getString(SmartMobileConstant.APP_SETTING_HOST, APP_DEFAULT_HOST);
    }

    public static String getAppPort(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, Context.MODE_PRIVATE);
        return preferences.getString(SmartMobileConstant.APP_SETTING_PORT, APP_DEFAULT_PORT);
    }

    public static String getApplication(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, Context.MODE_PRIVATE);
        return preferences.getString(SmartMobileConstant.APP_SETTING_APP, APP_DEFAULT_APPLICATION);
    }

    public static int getLoginType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, Context.MODE_PRIVATE);
        return preferences.getInt(SmartMobileConstant.APP_SETTING_LT, APP_DEFAULT_LOGINMODE);
    }

    public static void setLoginType(Context context, int type) {
        SharedPreferences preferences = context.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SmartMobileConstant.APP_SETTING_LT, type);
        editor.commit();
    }

    public static String getLoginUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("userMsg", Context.MODE_PRIVATE);
        return preferences.getString("userName", "");
    }

    public static String getAppPath(Context context) {
        String protocol = SmartMobileUtil.getAppProtocol(context);
        String host = SmartMobileUtil.getAppHost(context);
        String port = SmartMobileUtil.getAppPort(context);
        String app = SmartMobileUtil.getApplication(context);

        StringBuffer reqUrl = new StringBuffer();
        reqUrl.append(protocol).append("://");
        reqUrl.append(host);
        if (port != null && !"".equals(port)) {
            reqUrl.append(":").append(port);
        }

        reqUrl.append("/");

        if (app != null && !"".equals(app)) {
            reqUrl.append(app).append("/");
        }

        return reqUrl.toString();
    }

    public static String getFormatTime(String time, String srcFormatText, String destFormatText) throws Exception {
        if (time == null || "".equals(time)) {
            return "";
        }

        if (srcFormatText == null || "".equals(srcFormatText)) {
            srcFormatText = "yyyyMMddHHmmss";
        }

        if (destFormatText == null || "".equals(destFormatText)) {
            destFormatText = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat srcFormat = new SimpleDateFormat(srcFormatText);
        SimpleDateFormat destFormat = new SimpleDateFormat(destFormatText);
        Date date = srcFormat.parse(time);

        return destFormat.format(date);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        float roundPx = pixels;
        if (roundPx <= 0) {
            roundPx = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}

package com.ashqal.smartbar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.view.Window;

import com.ashqal.xposed.models.SmartOptions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ashqal on 14-7-29.
 */
public class Utils {


    public static int GetVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static String GetVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getString(R.string.version_unknown);
        }
    }

    public static boolean GetBoolean(String value) throws Exception {
        if ( "true".equals(value.toLowerCase()) )
        {
            return true;
        }
        else if( "false".equals(value.toLowerCase()) )
        {
            return false;
        }
        else
        {
            throw new Exception("can't read the value as boolean." );
        }
    }

    public static String Exec(String cmd)
    {
        Process p = null;
        String output = "";
        BufferedReader reader = null;
        try {
            p = Runtime.getRuntime().exec(cmd);
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = null;
            while ( (result = reader.readLine()) != null )
            {
                output += result;
            }
            p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally
        {
            if (p!= null) p.destroy();
            if ( reader != null )
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return output;
    }

    private static boolean HasSmartBar() {
        try {
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar", new Class[0x0]);
            return (Boolean)method.invoke(0x0, new Object[0x0]);
        }
        catch(Exception e)
        {
            if(Build.DEVICE.equals("mx2") || Build.DEVICE.equals("mx3")) {
                return true;
            }
            return false;
        }
    }

    public static int DisplayName(SmartOptions op)
    {
        switch (op)
        {
            case HIDE:
                return R.string.op_hidden;
            case IGNORE:
                return R.string.op_ignore;
            case SMART:
                return R.string.op_smart;
        }
        return R.string.op_smart;
    }
}

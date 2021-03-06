package com.ashqal.xposed;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ashqal on 14-7-29.
 */
public class SmartBarUtils {


    public static boolean IsFrameworkSupport(Activity context,String app)
    {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if(pinfo != null)
        {
            for(int i = 0; i < pinfo.size(); i++){
                String pn = pinfo.get(i).packageName;
                if (pn.equals(app)) return true;
            }
        }
        return false;

    }
    public static void NoActionBarHook(final Activity context)
    {
        try
        {
            Window windows = context.getWindow();
            testIfNull(windows,"context.getWindow() failed");
            //Log.e("ashqal.smartbar.Xposed","windows:" + windows);
            //Log.e("ashqal.smartbar.Xposed","windows.hasFeature:" + windows.hasFeature(Window.FEATURE_ACTION_BAR));
            if( !windows.hasFeature(Window.FEATURE_ACTION_BAR) )
            {
                View decorView = windows.getDecorView();
                SmartBarUtils.Hide(decorView);
            }


        }
        catch (Exception e)
        {
            Log.e("com.ashqal.smartbar.xposed",e.toString());
        }
    }

    public static void HasActionBarHook(final Activity context,Menu menu)
    {
        try
        {

            ActionBar a = context.getActionBar();
            testIfNull(a,"context.getActionBar() failed");

            Object mActionView =  SmartBarUtils.GetFieldValue(a, "mActionView");
            testIfNull(mActionView,"get mActionView failed");

            FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
            testIfNull(mSplitView,"get mSplitView failed");

            Window windows = context.getWindow();
            testIfNull(windows,"context.getWindow() failed");

            View decorView = windows.getDecorView();
            testIfNull(decorView,"windows.getDecorView() failed");

            Object mUiOptions = SmartBarUtils.GetFieldValue(windows, "mUiOptions");
            testIfNull(mUiOptions,"get windows mUiOptions failed");
            int uiOption = (Integer)mUiOptions;

            boolean isNeedHide = false;
            if( uiOption == 0 )
            {
                isNeedHide = true;
            }
            else
            {
                if( menu != null && menu.size() > 0   )
                {
                    isNeedHide = false;
                }
                else
                {
                    isNeedHide = true;
                }

            }

            //Log.e("com.ashqal.smartbar.Xposed","mSplitView child count:" + mSplitView.getChildCount());
            if( isNeedHide )
            {
                SmartBarUtils.HideV2(mSplitView);
                //SmartBarUtils.Hide(decorView);
            }
            else
            {
                SmartBarUtils.ShowV2(a);
            }



        }
        catch (Exception e)
        {
            Log.e("com.ashqal.smartbar.xposed",e.toString());
        }
    }

    private static void testIfNull(Object obj,String error) throws NullPointerException
    {
        if( obj == null ) throw new NullPointerException(error);
    }

    public static void HideV2(FrameLayout mSplitView)
    {

        mSplitView.setVisibility(View.GONE);

        for (int i = 0; i < mSplitView.getChildCount() ; i++)
        {
            mSplitView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    public static void ShowV2(ActionBar actionBar)
    {
        Object mActionView =  SmartBarUtils.GetFieldValue(actionBar, "mActionView");
        testIfNull(mActionView,"get mActionView failed");

        FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
        testIfNull(mSplitView,"get mSplitView failed");
        mSplitView.setVisibility(View.VISIBLE);

        for (int i = 0; i < mSplitView.getChildCount() ; i++)
        {
            mSplitView.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    public static void Hide(View decorView) {
        try
        {
            decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void SetFieldValue(Object aObject, String aFieldName,Object value) {
        Field field = GetClassField(aObject.getClass(), aFieldName);// get the field in this object
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(aObject,value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static Object GetFieldValue(Object aObject, String aFieldName) {
        Field field = GetClassField(aObject.getClass(), aFieldName);// get the field in this object
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(aObject);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }



    public static Field GetClassField(Class aClazz, String aFieldName) {

        try {
            Field field = aClazz.getDeclaredField(aFieldName);
            return field;
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();

        }


        Class superclass = aClazz.getSuperclass();
        if (superclass != null) {// 简单的递归一下
            return GetClassField(superclass, aFieldName);
        }
        return null;
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
}

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

import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ashqal on 14-7-29.
 */
public class SmartBarUtils {


    public static boolean IsFrameworkSport(Activity context,String app)
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
           // Log.e("ashqal.smartbar.Xposed","windows:" + windows);
           // Log.e("ashqal.smartbar.Xposed","windows.hasFeature:" + windows.hasFeature(Window.FEATURE_ACTION_BAR));
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

    public static void Hook(final Activity context,Menu menu)
    {
        try
        {
            ActionBar a = context.getActionBar();
            testIfNull(a,"context.getActionBar() failed");

            Object mActionView =  SmartBarUtils.GetFieldValue(a, "mActionView");
            testIfNull(mActionView,"get mActionView failed");

            View mSplitView = (View) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
            testIfNull(mSplitView,"get mSplitView failed");

            Window windows = context.getWindow();
            testIfNull(windows,"context.getWindow() failed");

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
                if( menu != null && menu.size() > 0  )
                {
                    isNeedHide = false;
                    /*
                    for ( int i = 0 ; i < menu.size() ; i++ )
                    {
                        android.view.MenuItem item = menu.getItem(i);
                        Log.w("ashqal.smartbar.Xposed","\nitem[" + i + "]" + item.getTitle()
                                        + ",collapseActionView=" + item.collapseActionView()
                                        + ",expandActionView=" + item.expandActionView()
                        );
                    }
                    */
                }
                else
                {
                    isNeedHide = true;
                }

            }

            if( isNeedHide )
            {
                mSplitView.setVisibility(View.GONE);
                View decorView = windows.getDecorView();
                SmartBarUtils.Hide(decorView);
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


    public static void Hide(View decorView) {
        if(!HasSmartBar())
        {
            return;
        }
        try {
            Class[] arrayOfClass = new Class[0x1];
            arrayOfClass[0x0] = Integer.TYPE;
            Method localMethod = View.class.getMethod("setSystemUiVisibility", arrayOfClass);
            Field localField = View.class.getField("SYSTEM_UI_FLAG_HIDE_NAVIGATION");
            Object[] arrayOfObject = new Object[0x1];
            try {
                arrayOfObject[0x0] = localField.get(0x0);
            } catch(Exception localException1) {
            }
            localMethod.invoke(decorView, arrayOfObject);
            return;
        } catch(Exception e) {
            e.printStackTrace();
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
        Field[] declaredFields = aClazz.getDeclaredFields();
        for (Field field : declaredFields) {
            // 注意：这里判断的方式，是用字符串的比较。很傻瓜，但能跑。要直接返回Field。我试验中，尝试返回Class，然后用getDeclaredField(String fieldName)，但是，失败了
            if (field.getName().equals(aFieldName)) {
                return field;// define in this class
            }
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

    public static boolean Exec(String[] cmds)
    {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());

            for ( int i = 0 ; i < cmds.length; i++ )
            {
                os.writeBytes(cmds[i] + "\n");
            }

            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
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

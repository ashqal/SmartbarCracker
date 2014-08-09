package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


/**
 * Created by ashqal on 14-8-9.
 */
public class SmartAttachedToWindowHook extends SmartMethodHook
{
    private boolean isRegister = false;

    public SmartAttachedToWindowHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log("SmartAttachedToWindowHook onAttachedToWindow//////////////////////////////////////////////");
        Activity context = (Activity)param.thisObject;
        getContext().setActivity(context);

        Window windows = context.getWindow();
        ActionBar ab = context.getActionBar();
        if (ab == null)
        {
            Log("ab=null");
            View decorView = windows.getDecorView();
            SmartBarUtils.Hide(decorView);
            if (!isRegister)
            {
                findAndHookMethod(WindowManager.LayoutParams.class, "copyFrom",WindowManager.LayoutParams.class
                        ,new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable
                    {
                        WindowManager.LayoutParams lp = (WindowManager.LayoutParams)param.args[0];
                        SmartBarUtils.SetFieldValue(lp,"meizuFlags",0);
                    }
                });
            }
            isRegister = true;
        }
        else
        {
            int mUiOptions = (Integer)SmartBarUtils.GetFieldValue(windows, "mUiOptions");
            Log("windows.mUiOptions=" + mUiOptions);

            if ( mUiOptions == 0 )
            {
                if ( ab.getNavigationMode() != 2)
                {
                    Object mActionView =  SmartBarUtils.GetFieldValue(ab, "mActionView");
                    FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
                    SmartBarUtils.HideV2(mSplitView);
                }
                else
                {
                    SmartBarUtils.ShowV2(ab);
                }

            }
            else
            {
                SmartBarUtils.ShowV2(ab);
            }



            Log("ab.getNavigationMode()=" + ab.getNavigationMode());
        }
    }

    private static SmartAttachedToWindowHook instance;
    public static SmartAttachedToWindowHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new SmartAttachedToWindowHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }
}

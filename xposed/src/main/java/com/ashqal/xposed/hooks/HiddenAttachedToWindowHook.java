package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
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
public class HiddenAttachedToWindowHook extends SmartMethodHook
{
    private boolean isRegister = false;

    public HiddenAttachedToWindowHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log(" HiddenAttachedToWindowHook onAttachedToWindow//////////////////////////////////////////////");
        Activity context = (Activity)param.thisObject;
        getContext().setActivity(context);

        Window windows = context.getWindow();
        View decorView = windows.getDecorView();
        SmartBarUtils.Hide(decorView);


        ActionBar ab = context.getActionBar();
        if (ab == null)
        {
            Log("ab=null");
            if (context != null)
                hiddenV1(context);

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
            if ( context != null )
                hiddenV2(context);
        }
    }

    private static HiddenAttachedToWindowHook instance;
    public static HiddenAttachedToWindowHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new HiddenAttachedToWindowHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }
}

package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;

/**
 * Created by ashqal on 14-8-9.
 */
public class HiddenSetMenuHook extends SmartMethodHook
{
    public HiddenSetMenuHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log("actionView setMenu//////////////////////////////////////////////");
        final Menu menu = (Menu) param.args[0];
        Log( "menu = null");
        Activity context = getContext().getActivity();
        Menu pervMenu = getContext().getMenu();
        if ( context != null && pervMenu != null )
        {
            ActionBar ab = context.getActionBar();
            if ( ab != null  )
            {
                Object mActionView =  SmartBarUtils.GetFieldValue(ab, "mActionView");
                FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
                SmartBarUtils.HideV2(mSplitView);
            }
        }
    }

    private static HiddenSetMenuHook instance;
    public static HiddenSetMenuHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new HiddenSetMenuHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }
}

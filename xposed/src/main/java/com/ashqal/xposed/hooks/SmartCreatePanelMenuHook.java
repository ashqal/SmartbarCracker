package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.FrameLayout;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;


/**
 * Created by ashqal on 14-8-9.
 */
public class SmartCreatePanelMenuHook extends SmartMethodHook
{
    public SmartCreatePanelMenuHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log("SmartCreatePanelMenuHook onCreatePanelMenu//////////////////////////////////////////////");
        final Activity context = (Activity) param.thisObject;
        getContext().setActivity(context);
        Window windows = context.getWindow();
        final Menu menu = (Menu) param.args[1];
        getContext().setMenu(menu);



        ActionBar ab = context.getActionBar();
        if (ab != null)
        {
            Log("ab.getNavigationMode()=" + ab.getNavigationMode());
            if ( ab.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS )
            {
                SmartBarUtils.ShowV2(ab);
            }
            else
            {
                Object mActionView =  SmartBarUtils.GetFieldValue(ab, "mActionView");
                FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
                int mUiOptions = (Integer)SmartBarUtils.GetFieldValue(windows, "mUiOptions");
                Log("windows.mUiOptions=" + mUiOptions + ",return value:" + param.getResult());
                if ( mUiOptions == 0 )
                {
                    SmartBarUtils.HideV2(mSplitView);
                }
                else
                {
                    Log("menu.size()=" + menu.size()
                            + ",hasVisibleItems=" + menu.hasVisibleItems());
                    if ( menu.hasVisibleItems() )
                    {
                        SmartBarUtils.ShowV2(ab);
                    }
                    else
                    {
                        SmartBarUtils.HideV2(mSplitView);
                    }
                }
            }

        }
        else
        {
            Log( "ab=null unhandler!!!!!!!!!******");
        }
    }

    private static SmartCreatePanelMenuHook instance;
    public static SmartCreatePanelMenuHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new SmartCreatePanelMenuHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }
}

package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.widget.FrameLayout;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;

/**
 * Created by ashqal on 14-8-9.
 */
public class SmartSetMenuHook extends SmartMethodHook
{
    public SmartSetMenuHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log("SmartSetMenuHook actionView setMenu//////////////////////////////////////////////");
        final Menu menu = (Menu) param.args[0];
        if (menu != null) {
            Log("SmartSetMenuHook menu.size()=" + menu.size()
                    + ",hasVisibleItems=" + menu.hasVisibleItems());

        }
        else
        {
            Log( "SmartSetMenuHook menu = null");
            Activity context = getContext().getActivity();
            Menu pervMenu = getContext().getMenu();
            if ( context != null && pervMenu != null )
            {
                ActionBar ab = context.getActionBar();
                if ( ab != null )
                {
                    if( ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS
                        && !pervMenu.hasVisibleItems() )
                    {
                        Object mActionView =  SmartBarUtils.GetFieldValue(ab, "mActionView");
                        FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
                        SmartBarUtils.HideV2(mSplitView);
                    }

                }
            }
        }
    }

    private static SmartSetMenuHook instance;
    public static SmartSetMenuHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new SmartSetMenuHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }
}

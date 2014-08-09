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
public class HiddenCreatePanelMenuHook extends SmartMethodHook
{
    public HiddenCreatePanelMenuHook(SmartContext context)
    {
        super(context);
    }


    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log("onCreatePanelMenu//////////////////////////////////////////////");
        Activity context = (Activity) param.thisObject;
        getContext().setActivity(context);
        final Menu menu = (Menu) param.args[1];
        getContext().setMenu(menu);


        if ( context != null )
        {
            hiddenV2(context);
        }

    }




    private static HiddenCreatePanelMenuHook instance;
    public static HiddenCreatePanelMenuHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new HiddenCreatePanelMenuHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }

}

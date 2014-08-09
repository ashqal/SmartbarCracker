package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;


/**
 * Created by ashqal on 14-8-9.
 */
public class HiddenMenuInflaterHook extends SmartMethodHook
{
    public HiddenMenuInflaterHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable
    {
        Log( "MenuInflater//////////////////////////////////////////////");
        final Menu menu = (Menu) param.args[1];
        getContext().setMenu(menu);
        Log("menu.size()=" + menu.size()
                + ",hasVisibleItems=" + menu.hasVisibleItems());
        Activity context = getContext().getActivity();

        if ( context != null )
            hiddenV2(context);
    }

    private static HiddenMenuInflaterHook instance;
    public static HiddenMenuInflaterHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new HiddenMenuInflaterHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }

}

package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;


/**
 * Created by ashqal on 14-8-9.
 */
public class SmartMenuInflaterHook extends SmartMethodHook
{
    public SmartMenuInflaterHook(SmartContext context)
    {
        super(context);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable
    {
        Log( "SmartMenuInflaterHook MenuInflater//////////////////////////////////////////////");
        final Menu menu = (Menu) param.args[1];
        getContext().setMenu(menu);
        Log("SmartMenuInflaterHook menu.size()=" + menu.size()
                + ",hasVisibleItems=" + menu.hasVisibleItems());
        Activity context = getContext().getActivity();
        ActionBar ab = context.getActionBar();

        int mUiOptions = (Integer)SmartBarUtils.GetFieldValue(context.getWindow(), "mUiOptions");
        Log("SmartMenuInflaterHook windows.mUiOptions=" + mUiOptions);

        if ( ab != null )
        {
            if (menu.hasVisibleItems() && mUiOptions != 0)
                SmartBarUtils.ShowV2(ab);
        }
    }

    private static SmartMenuInflaterHook instance;
    public static SmartMenuInflaterHook Instance( SmartContext context )
    {
        if (instance == null)
        {
            instance = new SmartMenuInflaterHook(context);
        }
        else
        {
            instance.setContext(context);
        }
        return instance;
    }

}

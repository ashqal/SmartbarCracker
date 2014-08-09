package com.ashqal.xposed.hooks;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.ashqal.xposed.SmartBarUtils;
import com.ashqal.xposed.models.SmartContext;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by ashqal on 14-8-9.
 */
public class SmartMethodHook extends XC_MethodHook
{
    private SmartContext mContext;
    public SmartMethodHook( SmartContext context )
    {
        this.mContext = context;
    }

    public SmartContext getContext()
    {
        return mContext;
    }

    public void setContext(SmartContext mContext) {
        this.mContext = mContext;
    }

    protected void hiddenV2(Activity a)
    {
        do
        {
            ActionBar ab = a.getActionBar();
            if (ab != null)
            {
                Object mActionView =  SmartBarUtils.GetFieldValue(ab, "mActionView");
                FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
                SmartBarUtils.HideV2(mSplitView);
            }

        }
        while ( (a = a.getParent()) != null );
    }

    protected void hiddenV1(Activity a)
    {
        do
        {
            Window window = a.getWindow();
            if (window != null)
            {
                View decorView = window.getDecorView();
                SmartBarUtils.Hide(decorView);
            }

        }
        while ( (a = a.getParent()) != null );
    }

    public void Log(String str)
    {
        //Log.w("ashqal.smartbar.Xposed", str);
    }
}

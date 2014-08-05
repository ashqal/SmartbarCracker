package com.ashqal.xposed;

import android.app.Activity;
import android.os.Build;
import android.view.Menu;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


/**
 * Created by ashqal on 14-8-2.
 */
public class XposedInterface implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable
    {
        //if (!loadPackageParam.packageName.equals("android.app.Application"))

        if (loadPackageParam.packageName.contains("com.android.")
             || loadPackageParam.packageName.contains("com.meizu.")
                || loadPackageParam.packageName.contains("com.eg.android.AlipayGphone")
           )
        {
            //XposedBridge.log("[com.ashqal.smartbar.xposed] system app ,return. " + loadPackageParam.packageName);
            return;
        }
        //XposedBridge.log("[com.ashqal.smartbar.xposed] custom app ,hook. " + loadPackageParam.packageName);



        if (loadPackageParam.packageName.equals("com.ashqal.smartbar"))
        {
            findAndHookMethod("com.ashqal.smartbar.MyActivity", loadPackageParam.classLoader, "SetupSuccess",new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable
                {
                    param.setResult(true);
                }

            });
        }


        findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onAttachedToWindow"
                ,noActionBarMethodHook);
        findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onCreatePanelMenu"
                , int.class
                , Menu.class
                , hasActionBarMethodHook);

    }
    private static XC_MethodHook noActionBarMethodHook = new NoActionBarMethodHook();
    private static class NoActionBarMethodHook extends XC_MethodHook
    {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable
        {
            Activity a = (Activity) param.thisObject;
            SmartBarUtils.NoActionBarHook(a);
        }
    }

    private static XC_MethodHook hasActionBarMethodHook = new HasActionBarMethodHook();
    private static class HasActionBarMethodHook extends XC_MethodHook
    {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable
        {
            Menu menu = (Menu) param.args[1];
            Activity a = (Activity) param.thisObject;
            SmartBarUtils.HasActionBarHook(a, menu);
        }
    }
}

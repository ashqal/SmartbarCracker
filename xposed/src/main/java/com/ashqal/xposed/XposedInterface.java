package com.ashqal.xposed;

import android.app.Activity;
import android.view.Menu;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
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

//        XposedBridge.log("hook app: " + loadPackageParam.packageName
//                        + ",isFirstApplication=" + loadPackageParam.isFirstApplication
//                        + ",manageSpaceActivityName =" + loadPackageParam.appInfo.manageSpaceActivityName
//                        + ",appInfo =" + loadPackageParam.appInfo
//
//        );
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
        findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onAttachedToWindow",new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable
            {
                Activity a = (Activity) param.thisObject;
                SmartBarUtils.NoActionBarHook(a);
            }

        });
        findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onCreatePanelMenu"
                , int.class
                , Menu.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Menu menu = (Menu) param.args[1];
                Activity a = (Activity) param.thisObject;
                SmartBarUtils.Hook(a, menu);

            }
        });

    }
}

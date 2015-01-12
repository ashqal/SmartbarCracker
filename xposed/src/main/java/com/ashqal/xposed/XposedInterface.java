package com.ashqal.xposed;

import android.content.res.XResources;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ashqal.xposed.hooks.HiddenAttachedToWindowHook;
import com.ashqal.xposed.hooks.HiddenCreatePanelMenuHook;
import com.ashqal.xposed.hooks.HiddenMenuInflaterHook;
import com.ashqal.xposed.hooks.HiddenSetMenuHook;
import com.ashqal.xposed.hooks.SmartAttachedToWindowHook;
import com.ashqal.xposed.hooks.SmartCreatePanelMenuHook;
import com.ashqal.xposed.hooks.SmartMenuInflaterHook;
import com.ashqal.xposed.hooks.SmartMethodHook;
import com.ashqal.xposed.hooks.SmartSetMenuHook;
import com.ashqal.xposed.managers.ConfigManager;
import com.ashqal.xposed.models.SmartContext;
import com.ashqal.xposed.models.SmartOptions;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;


/**
 * Created by ashqal on 14-8-2.
 */
public class XposedInterface implements IXposedHookLoadPackage,IXposedHookInitPackageResources
{


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        //XposedBridge.log("[com.ashqal.smartbar.xposed] handleInitPackageResources:" + resparam.packageName);

    }




    private boolean debug = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable
    {

        //XposedBridge.log("[com.ashqal.smartbar.xposed] handleLoadPackage" + loadPackageParam.packageName);
        //ApplicationCreateHook appCreateHook = new ApplicationCreateHook(loadPackageParam);


        if(loadPackageParam.packageName.equals("com.android.systemui"))
        {
            final ExecutorService executor = Executors.newFixedThreadPool(2);
            final Runnable runable = new Runnable() {
                @Override
                public void run() {
                    SmartBarUtils.Exec("input keyevent 82");
                }
            };
            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "updateClock", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    TextView tv = (TextView) param.thisObject;

                    if( tv.isClickable() == false )
                    {
                        tv.setClickable(true);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //XposedBridge.log("[com.ashqal.smartbar.xposed]menu onClick 82!!!!!!!!!" );
                                executor.execute(runable);
                            }
                        });
                    }

                }
            });
        }
        if (loadPackageParam.packageName.contains("com.ashqal.smartbar"))
            hookSmartSetup(loadPackageParam);

        //public final int copyFrom(LayoutParams o)
        if( debug )
        {
            return;
        }



        if (loadPackageParam.packageName.contains("com.android.")
             || loadPackageParam.packageName.contains("com.meizu.")
           )
        {
            //XposedBridge.log("[com.ashqal.smartbar.xposed] system app ,return. " + loadPackageParam.packageName);
            return;
        }









        if ( !ConfigManager.Instance().load() )
        {
            XposedBridge.log("[com.ashqal.smartbar.xposed] read the config error. " + loadPackageParam.packageName);
            return;
        }



        Properties configs = ConfigManager.Instance().getProp();
        //打开或关闭
        if (configs.containsKey(loadPackageParam.packageName))
        {
            SmartOptions[] options = SmartOptions.values();
            int value = Integer.parseInt(configs.getProperty(loadPackageParam.packageName));
            if ( value < options.length && value >= 0)
            {
                SmartOptions op = options[value];
                if ( op ==  SmartOptions.IGNORE )
                {
                    return;
                }
                //强制隐藏模式
                else if ( op == SmartOptions.HIDE )
                {
                    final SmartContext mSmartCtx = new SmartContext();
                    hookAttachedToWindow(loadPackageParam
                            , HiddenAttachedToWindowHook.Instance(mSmartCtx));
                    hookCreatePanelMenu(loadPackageParam
                            , HiddenCreatePanelMenuHook.Instance(mSmartCtx));
                    hookMenuInflater(loadPackageParam
                            , HiddenMenuInflaterHook.Instance(mSmartCtx));
                    hookSetMenu(loadPackageParam
                            , HiddenSetMenuHook.Instance(mSmartCtx));

                }
            }
        }
        //smart模式
        else
        {
            final SmartContext mSmartCtx = new SmartContext();
            hookAttachedToWindow(loadPackageParam
                    , SmartAttachedToWindowHook.Instance(mSmartCtx));
            hookCreatePanelMenu(loadPackageParam
                    , SmartCreatePanelMenuHook.Instance(mSmartCtx));
            hookMenuInflater(loadPackageParam, SmartMenuInflaterHook.Instance(mSmartCtx));
            hookSetMenu(loadPackageParam
                    , SmartSetMenuHook.Instance(mSmartCtx));
        }



    }




    private static class ApplicationCreateHook extends XC_MethodHook
    {
        private XC_LoadPackage.LoadPackageParam mLoadPackageParam;
        private ApplicationCreateHook( final XC_LoadPackage.LoadPackageParam loadPackageParam )
        {
            mLoadPackageParam = loadPackageParam;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable
        {

        }
    }

    private static  void hookAttachedToWindow(final XC_LoadPackage.LoadPackageParam loadPackageParam
            ,SmartMethodHook methodHook)
    {
        findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onAttachedToWindow"
                ,methodHook);
    }

    private static void hookMenuInflater(final XC_LoadPackage.LoadPackageParam loadPackageParam
            ,SmartMethodHook methodHook)
    {
        findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onCreatePanelMenu"
                , int.class
                , Menu.class
                , methodHook);
    }

    private static void hookCreatePanelMenu(final XC_LoadPackage.LoadPackageParam loadPackageParam
            ,SmartMethodHook methodHook)
    {
        findAndHookMethod("android.view.MenuInflater", loadPackageParam.classLoader, "inflate"
                ,  int.class
                , Menu.class
                , methodHook);
    }


    private static void hookSetMenu(final XC_LoadPackage.LoadPackageParam loadPackageParam
            ,SmartMethodHook methodHook)
    {
        findAndHookMethod("com.android.internal.widget.ActionBarView", loadPackageParam.classLoader
                , "setMenu"
                , Menu.class
                , findClass("com.android.internal.view.menu.MenuPresenter.Callback",loadPackageParam.classLoader)
                , methodHook);
    }


    /****************
     *
     *
     *  用于判断Smart模块是否已安装成功
     *
     *  @param loadPackageParam
     *
     * */
    private void hookSmartSetup( final XC_LoadPackage.LoadPackageParam loadPackageParam )
    {


        if (loadPackageParam.packageName.equals("com.ashqal.smartbar"))
        {
            findAndHookMethod("com.ashqal.smartbar.fragments.SetupFragment"
                    , loadPackageParam.classLoader
                    , "SetupSuccess"
                    ,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable
                {
                    param.setResult(true);
                }

            });
        }
    }


}

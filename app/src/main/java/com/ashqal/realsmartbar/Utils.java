package com.ashqal.realsmartbar;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.DataOutputStream;

/**
 * Created by ashqal on 14-7-29.
 */
public class Utils {


    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getString(R.string.version_unknown);
        }
    }

    public static boolean getBoolean(String value) throws Exception {
        if ( "true".equals(value.toLowerCase()) )
        {
            return true;
        }
        else if( "false".equals(value.toLowerCase()) )
        {
            return false;
        }
        else
        {
            throw new Exception("can't read the value as boolean." );
        }
    }

    public static boolean exec(String[] cmds)
    {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());

            for ( int i = 0 ; i < cmds.length; i++ )
            {
                os.writeBytes(cmds[i] + "\n");
            }

            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}

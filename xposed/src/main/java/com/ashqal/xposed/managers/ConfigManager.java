package com.ashqal.xposed.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by ashqal on 14-8-9.
 */
public class ConfigManager
{
    private static ConfigManager instance;
    public static ConfigManager Instance()
    {
        if ( instance == null )
            instance = new ConfigManager();
        return instance;
    }

    private Properties mProp;
    private File mFile;
    private ConfigManager()
    {
        mProp = new Properties();
        File path = new File("/mnt/sdcard/smartdata");
        if (!path.exists() )
        {
            path.mkdir();
        }
        mFile = new File(path.getAbsoluteFile() + "/config.data");
        if (!mFile.exists())
        {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public Properties getProp() {
        return mProp;
    }

    public boolean load()
    {
        try
        {
            mProp.clear();
            mProp.load(new FileInputStream(mFile));
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean save() {
        try
        {
            mProp.store(new FileOutputStream(mFile),"created by smart.");
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}

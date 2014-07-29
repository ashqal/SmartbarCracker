package com.ashqal.realsmartbar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class MyActivity extends ActionBarActivity {

    private Properties mProp;
    private Button mCrackerBtn;
    private Button mRestoreBtn;
    private File mFile ;
    private File mTmpFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mFile = new File("/system/build.prop");
        mTmpFile = new File( getCacheDir() + "/" + "build.prop");

        TextView version = (TextView)this.findViewById(R.id.versionText);
        version.setText( getString(R.string.author) +  "\nVERSION:" + Utils.getVersion(this)   );

        mCrackerBtn = (Button)this.findViewById(R.id.crackerBtn);
        mRestoreBtn = (Button)this.findViewById(R.id.restoreBtn);
        disabledBtn();
        readStatus();
    }


    private void readStatus()
    {
        try {
            //ro.meizu.permanentkey=true
            //ro.meizu.has_smartbar=false
            mProp = new Properties();
            mProp.load( new FileInputStream(mFile) );
            String value1 = mProp.getProperty("ro.meizu.permanentkey");
            String value2 = mProp.getProperty("ro.meizu.has_smartbar");
            boolean permanentkey = Utils.getBoolean(value1);
            boolean has_smartbar = Utils.getBoolean(value2);
            if( permanentkey == true &&  has_smartbar == false )
            {
                mRestoreBtn.setEnabled(true);
            }
            else if( permanentkey == false  &&  has_smartbar == true )
            {
                mCrackerBtn.setEnabled(true);
            }
            else
            {
                throw new Exception("unsupport value." );
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void onCracker(View v)
    {
        disabledBtn();
        try {
            mProp.setProperty("ro.meizu.permanentkey","true");
            mProp.setProperty("ro.meizu.has_smartbar","false");
            mProp.store( new FileOutputStream(mTmpFile),null);
            String[] cmds = {
                    "mount -o remount,rw /system"
                    ,"cp -f " + mTmpFile.getAbsolutePath() + " " + mFile.getAbsolutePath()
                    ,"mount -o remount,ro /system"
            };
            Utils.exec(cmds);
            Toast.makeText(this,"配置修改成功,重启后生效:)",Toast.LENGTH_SHORT).show();
            readStatus();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void onRestore(View v)
    {
        disabledBtn();
        try {

            mProp.setProperty("ro.meizu.permanentkey","false");
            mProp.setProperty("ro.meizu.has_smartbar","true");
            mProp.store( new FileOutputStream(mTmpFile),null);
            String[] cmds = {
               "mount -o remount,rw /system"
               ,"cp -f " + mTmpFile.getAbsolutePath() + " " + mFile.getAbsolutePath()
               ,"mount -o remount,ro /system"
            };
            Utils.exec(cmds);
            Toast.makeText(this,"配置修改成功,重启后生效:)",Toast.LENGTH_SHORT).show();
            readStatus();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void disabledBtn()
    {
        mCrackerBtn.setEnabled(false);
        mRestoreBtn.setEnabled(false);
    }




}

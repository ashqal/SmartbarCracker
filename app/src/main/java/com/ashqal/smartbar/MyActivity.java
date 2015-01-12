package com.ashqal.smartbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ActionBarContainer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashqal.xposed.SmartBarUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyActivity extends ActionBarActivity {

    private Button mCrackerBtn;
    private int mStep;
    public static final String PATTERN = "[^\\da-zA-Z_]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_my);


        Pattern pt = Pattern.compile(PATTERN);
        Matcher m = pt.matcher(Build.DEVICE + "_" + Build.DISPLAY);
        String result = m.replaceAll("");


        TextView version = (TextView)this.findViewById(R.id.versionText);
        version.setText( "version:" + Utils.GetVersion(this)
                + " author:" + getString(R.string.author) + " "
                + result + ","
                + android.os.Build.MODEL + ","
                + Build.DISPLAY + ","
                + android.os.Build.VERSION.SDK_INT + ","
                + android.os.Build.VERSION.RELEASE  );

        mCrackerBtn = (Button)this.findViewById(R.id.crackerBtn);

        boolean isSupport = SmartBarUtils.IsFrameworkSupport(this,"de.robv.android.xposed.installer");
        if (!isSupport)
        {
            mCrackerBtn.setText( "请下载安装xposed" );
            mStep = 0;
        }
        else
        {
            mCrackerBtn.setText( "请激活xposed框架,启用Smart模块,并重启" );
            mStep = 1;
        }
        if (SetupSuccess())
        {
            mCrackerBtn.setText( "安装成功,enjoy it.." );
            mStep = 2;
        }


    }
    public void onTest(View v2)
    {
        //MzActionBarContainer
        Window window = getWindow();
        View v = window.getDecorView();
        //v.setVisibility(View.GONE);
        Object[] views =  (Object[])SmartBarUtils.GetFieldValue(v,"mChildren");
        View actionbarOverlayLayout = (View)views[0];
        FrameLayout mActionBarBottom = (FrameLayout)SmartBarUtils.GetFieldValue(actionbarOverlayLayout,"mActionBarBottom");
        //mActionBarBottom.setVisibility(View.GONE);
       //SmartBarUtils.HideV2(mActionBarBottom);


        SmartBarUtils.Hide(v);
        //View mMzActionBar = (View)views[2];
        //mMzActionBar.setVisibility(View.GONE);
        //SmartBarUtils.HideV2(mActionBarBottom);
        //mActionBarBottom.setBackground();
        //mActionBarBottom.setBackgroundColor(R.color.green);

        //ActionBar ab = getActionBar();
        //Object mActionView =  SmartBarUtils.GetFieldValue(ab, "mActionView");
        //FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
        //SmartBarUtils.HideV2(mSplitView);
        //ActionBarView mActionView = (ActionBarView) SmartBarUtils.GetFieldValue(getActionBar(), "mActionView");
        //mActionView.setMenu(null,null);

    }


    public boolean SetupSuccess()
    {
         return  false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
       // menu.addSubMenu("test");
        //MenuItem item = menu.add(0,-111,999,"test");
        //View;
        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.w("com.ashqal.smartbar.xposed","item:" + item.getTitle() + "," + item.getItemId());
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCracker(View v)
    {
        switch ( mStep  )
        {
            case 0:
                break;
            case 1:
                Intent i = new Intent();
                i.setClassName("de.robv.android.xposed.installer","de.robv.android.xposed.installer.WelcomeActivity");
                this.startActivity(i);
                break;
            case 2:
                break;
        }
    }



}

package com.ashqal.smartbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ashqal.xposed.SmartBarUtils;


public class MyActivity extends ActionBarActivity {

    private Button mCrackerBtn;
    private int mStep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_my);

        TextView version = (TextView)this.findViewById(R.id.versionText);
        version.setText( getString(R.string.author) +  "\nVERSION:" + Utils.GetVersion(this) + "  " + android.os.Build.MODEL + ","
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
            mCrackerBtn.setText( "安装成功,enjoy it." );
            mStep = 2;
        }

    }
    public void onTest(View v2)
    {

    }


    public boolean SetupSuccess()
    {
         return  false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        //View;
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

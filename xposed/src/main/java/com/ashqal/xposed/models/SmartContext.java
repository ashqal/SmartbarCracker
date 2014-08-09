package com.ashqal.xposed.models;

import android.app.Activity;
import android.view.Menu;

/**
 * Created by ashqal on 14-8-9.
 */
public class SmartContext
{
    private Activity mActivity;
    private Menu mMenu;

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu mMenu) {
        this.mMenu = mMenu;
    }

}

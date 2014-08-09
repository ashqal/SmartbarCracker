package com.ashqal.smartbar.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashqal.smartbar.R;
import com.ashqal.smartbar.Utils;
import com.ashqal.xposed.models.SmartOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashqal on 14-8-9.
 */
public class AppItemAdapter extends BaseAdapter
{
    private final LayoutInflater mInflater;
    private List<AppItem> ITEMS ;

    public AppItemAdapter(Context context)
    {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ITEMS = new ArrayList<AppItem>();
    }
    public void addItem(AppItem item)
    {
        ITEMS.add(item);
    }

    @Override
    public int getCount()
    {
        return ITEMS.size();
    }

    @Override
    public Object getItem(int position) {
        return ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if ( convertView == null )
        {
            convertView = mInflater.inflate(R.layout.layout_item_app,null);
        }
        bind(position,convertView);
        return convertView;
    }

    public AppItem getAppItem(int position)
    {
        return (AppItem)getItem(position);
    }

    private void bind(int position, View convertView)
    {
        ImageView imageView = (ImageView)convertView.findViewById(R.id.app_icon);
        TextView textView = (TextView)convertView.findViewById(R.id.app_name);
        TextView opTextView = (TextView)convertView.findViewById(R.id.app_op);
        AppItem item = ITEMS.get(position);
        imageView.setImageDrawable(item.getIcon());
        textView.setText(item.getAppName());
        opTextView.setText(Utils.DisplayName(item.getTempOption()));

        item.setView(convertView);
        item.setOpTV(opTextView);
    }

    public void setChecked(View list,boolean isChecked)
    {
        if (isChecked)
        {
            list.setBackgroundResource(R.drawable.selector_list_bg_checked);
        }
        else
        {
            list.setBackgroundResource(R.drawable.selector_list_bg);
        }

    }


    public static class AppItem {
        private String mPackgeName;
        private String mAppName;
        private Drawable mIcon;
        private SmartOptions mOption;
        private View mView;
        private TextView mOpTV;
        private SmartOptions mTempOption;

        public AppItem(String packgeName, String appName, Drawable icon) {
            this.mPackgeName = packgeName;
            this.mAppName = appName;
            this.mIcon = icon;
            this.mOption = SmartOptions.SMART;
            this.mTempOption = mOption;
        }



        public String getPackgeName() {
            return mPackgeName;
        }

        public String getAppName() {
            return mAppName;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public SmartOptions getOption() {
            return mOption;
        }

        public void setOption(SmartOptions mOption) {
            this.mOption = mOption;
            this.mTempOption = mOption;
        }

        public View getView() {
            return mView;
        }

        public void setView(View mView) {
            this.mView = mView;
        }

        public void tempDisplay(SmartOptions op)
        {
            this.mTempOption = op;
            if ( this.mOpTV != null )
                mOpTV.setText(Utils.DisplayName(op));
        }

        public void setOpTV(TextView mOpTV) {
            this.mOpTV = mOpTV;
        }

        public SmartOptions getTempOption() {
            return mTempOption;
        }

        public void saveChange()
        {
            mOption = mTempOption;
        }
        public void dissmisChange()
        {
            mTempOption = mOption;
        }

        @Override
        public String toString() {
            return mAppName;
        }
    }
}

package com.ashqal.smartbar.fragments;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ashqal.smartbar.R;
import com.ashqal.smartbar.adapters.AppItemAdapter;
import com.ashqal.smartbar.interfaces.IActionModeOp;
import com.ashqal.xposed.managers.ConfigManager;
import com.ashqal.xposed.models.SmartOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * interface.
 */
public class SettingFragment extends ListFragment implements PopupMenu.OnMenuItemClickListener
        , PopupMenu.OnDismissListener,IActionModeOp
{

    private OnFragmentInteractionListener mListener;


    private AppItemAdapter mAdapter;
    private PopupMenu mPopupMenu;
    private ActionMode mActionMode;
    private Map<String,AppItemAdapter.AppItem> mModifyList;

    public SettingFragment()
    {
        mModifyList = new HashMap<String, AppItemAdapter.AppItem>();

    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new AppItemAdapter(getActivity());

        if (  !ConfigManager.Instance().load() )
        {
            Toast.makeText(getActivity(),"配置文件读取失败.",Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }).start();

    }

    private void loadData()
    {
        Properties configs = ConfigManager.Instance().getProp();

        SmartOptions[] options = SmartOptions.values();

        final PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if(pinfo != null)
        {
            Iterator<PackageInfo> iter = pinfo.iterator();
            while (iter.hasNext())
            {
                PackageInfo info = iter.next();
                if ( !info.packageName.contains("com.android.")
                        && !info.packageName.contains("com.meizu.") )
                {



                    AppItemAdapter.AppItem item = new AppItemAdapter.AppItem(
                            info.packageName
                            ,info.applicationInfo.loadLabel(packageManager).toString()
                            ,info.applicationInfo.loadIcon(packageManager)
                    );
                    if ( configs.containsKey(info.packageName) )
                    {
                        int value = Integer.parseInt(configs.getProperty(info.packageName));
                        if ( options.length > value  && value >= 0 )
                        {
                            SmartOptions op = SmartOptions.values()[value];
                            item.setOption(op);
                        }
                    }
                    mAdapter.addItem(item);
                }

            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setListAdapter(mAdapter);
            }
        });

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getListView().setDivider(null);
    }

    private View mListItem;
    private AppItemAdapter.AppItem mModifyingItem;
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        mModifyingItem = mAdapter.getAppItem(position);
        mListItem = v;
        mAdapter.setChecked(mListItem,true);
        mPopupMenu = new PopupMenu(getActivity(),v.findViewById(R.id.app_anchor));
        mPopupMenu.inflate(R.menu.options);
        mPopupMenu.setOnMenuItemClickListener(this);
        mPopupMenu.setOnDismissListener(this);
        mPopupMenu.show();



            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            // mListener.onFragmentInteraction(mAdapter.ITEMS.get(position).getPackgeName());
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        SmartOptions op = null;
        switch (menuItem.getItemId())
        {
            case R.id.action_hidden:
                op = SmartOptions.HIDE;
                break;
            case R.id.action_ignore:
                op = SmartOptions.IGNORE;
                break;
            case R.id.action_smart:
                op = SmartOptions.SMART;
                break;
            default:
                break;
        }
        if ( op != null && mModifyingItem != null )
        {
            mModifyingItem.tempDisplay(op);
            if ( mModifyingItem.getOption() != op )
            {
                startActionMode();
                mModifyList.put(mModifyingItem.getPackgeName(),mModifyingItem);
            }
            else
            {
                mModifyList.remove(mModifyingItem.getPackgeName());
            }

            mActionMode.setTitle("保存"  + mModifyList.size() + "项修改?");

            return true;
        }
        return false;

    }

    @Override
    public void onDismiss(PopupMenu popupMenu)
    {
        if ( mListItem != null )
            mAdapter.setChecked(mListItem,false);
    }

    public void startActionMode() {
        if (mActionMode == null)
        {
            ActionMode.Callback cb = new ModeCallback(this);
            ActionBarActivity context = (ActionBarActivity)getActivity();
            mActionMode = context.startSupportActionMode(cb);
        }
    }

    public void stopActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }


    @Override
    public void onActionModeCancel()
    {
        Set<String> set = mModifyList.keySet();
        Iterator<String> iter =  set.iterator();
        while ( iter.hasNext() )
        {
            String key = iter.next();
            AppItemAdapter.AppItem item = mModifyList.get(key);
            item.dissmisChange();
        }
        resetChange();
    }

    @Override
    public void onActionModeSave()
    {
        Properties configs = ConfigManager.Instance().getProp();
        Set<String> set = mModifyList.keySet();
        Iterator<String> iter =  set.iterator();
        while ( iter.hasNext() )
        {
            String key = iter.next();
            AppItemAdapter.AppItem item = mModifyList.get(key);
            item.saveChange();

            if ( item.getOption() == SmartOptions.SMART )
            {
                configs.remove( item.getPackgeName() );
            }
            else
            {
                configs.setProperty(  item.getPackgeName()
                        , "" + item.getOption().ordinal() );
            }

            Log.e("com.ashqal.smart", "key=" + key + ",value=" + item.getOption());
        }
        ConfigManager.Instance().save();
        resetChange();
    }

    private void resetChange()
    {
        mModifyList.clear();
        mAdapter.notifyDataSetChanged();
    }




    private class ModeCallback implements ActionMode.Callback {

        private IActionModeOp mOP;

        private ModeCallback( IActionModeOp op ) {
             mOP = op;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("保存修改");
            mode.setSubtitle(null);
            menu.add(0, 1, 0, "取消");

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            if (  mOP != null )
            {
                mOP.onActionModeCancel();
                mOP = null;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (  mOP != null )
            {
                mOP.onActionModeSave();
                mOP = null;
            }
            mActionMode = null;
        }



        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }


    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

}

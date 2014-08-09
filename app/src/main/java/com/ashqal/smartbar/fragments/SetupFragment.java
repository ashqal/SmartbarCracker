package com.ashqal.smartbar.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashqal.smartbar.R;
import com.ashqal.xposed.SmartBarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 * com.ashqal.smartbar.fragments.SetupFragment
 */
public class SetupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private List<ImageView> mImages;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Parameter 1.
     * @return A new instance of fragment SetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupFragment newInstance(String param) {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }
    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        mImages = new ArrayList<ImageView>();


    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private TextView mProcessStep;
    private ImageView mProcessBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setup, container, false);

        mImages.add((ImageView) v.findViewById(R.id.process_step1));
        mImages.add( (ImageView)v.findViewById(R.id.process_step2) );
        mImages.add( (ImageView)v.findViewById(R.id.process_step3) );
        mProcessStep = (TextView)v.findViewById(R.id.process_text);
        mProcessBar = (ImageView)v.findViewById(R.id.process_bar);


        boolean isSupport = SmartBarUtils.IsFrameworkSupport(getActivity(), "de.robv.android.xposed.installer");

        if (!isSupport)
        {
            mProcessStep.setText( "STEP1\n\n请下载安装xposed" );
            step(0);
        }
        else
        {
            mProcessStep.setText( "STEP2\n\n请激活xposed框架,启用Smart模块,并重启" );
            step(1);
        }
        if (SetupSuccess())
        {
            mProcessStep.setText( "STEP3\n\n安装成功,enjoy it." );
            step(2);
        }


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mImages.clear();

    }

    private void step(int step)
    {
        for ( int i = 0 ; i < step + 1 ; i++ )
        {
            if ( i < step + 1 )
                mImages.get(i).setImageResource(R.drawable.shape_circle_complete);
            else
                mImages.get(i).setImageResource(R.drawable.shape_circle);
        }
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int targetWidth = 0;
        switch (step)
        {
            case 0:
                targetWidth = (int) (80 * density);
                break;
            case 1:
                targetWidth = width/2;
                break;
            case 2:
                targetWidth = width;
                break;
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mProcessBar.getLayoutParams();
        lp.width = targetWidth;
        mProcessBar.setLayoutParams(lp);
    }

    public boolean SetupSuccess()
    {
        return false;
    }


}

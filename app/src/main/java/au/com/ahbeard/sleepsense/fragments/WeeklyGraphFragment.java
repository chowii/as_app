package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.widgets.WeeklyGraphView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by neal on 12/03/2016.
 */
public class WeeklyGraphFragment extends Fragment {

    public interface OnClickListener {
        void onClick(int sleepId);
    }

    private WeeklyGraphView.OnClickListener mOnClickListener;

    @Bind(R.id.graph_view)
    WeeklyGraphView mGraphView;

    private float[] mValues;
    private String[] mLabels;
    private int[] mSleepIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_graph, container, false);

        ButterKnife.bind(this, view);

        Integer[] sleepIds = new Integer[mSleepIds.length];

        for (int i = 0; i < mSleepIds.length; i++) {
            sleepIds[i] = mSleepIds[i];
        }

        Log.d("WeeklyGraphFragment","sleepIds: "+org.apache.commons.lang3.ArrayUtils.toString(sleepIds));
        Log.d("WeeklyGraphFragment","values: "+org.apache.commons.lang3.ArrayUtils.toString(mValues));

        mGraphView.setValues(mValues, mLabels, sleepIds, 20, 100);
        mGraphView.setOnClickListener(new WeeklyGraphView.OnClickListener() {
            @Override
            public void onValueClicked(Object identifier) {
                if ( mOnClickListener != null ) {
                    mOnClickListener.onValueClicked(identifier);
                }
            }
        });



        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public static WeeklyGraphFragment newInstance(int[] sleepIds, float[] values, String[] names) {
        WeeklyGraphFragment fragment = new WeeklyGraphFragment();
        Bundle args = new Bundle();
        args.putIntArray("sleep_ids", sleepIds);
        args.putFloatArray("values", values);
        args.putStringArray("labels", names);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mValues = getArguments().getFloatArray("values");
            mLabels = getArguments().getStringArray("labels");
            mSleepIds = getArguments().getIntArray("sleep_ids");
        }

    }

    public void setOnClickListener(WeeklyGraphView.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}

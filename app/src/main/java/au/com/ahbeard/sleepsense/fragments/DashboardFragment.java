package au.com.ahbeard.sleepsense.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Random;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.widgets.SleepSenseGraphView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    @Bind(R.id.dashboard_view_pager_graph)
    ViewPager graphViewPager;

    private Float[] mValues;
    private Calendar mCalendar;
    private int mDayOfWeek;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }

        Random random = new Random(1234);

        mValues = new Float[400];

        for ( int i=0; i < mValues.length; i++ ) {
            if ( random.nextFloat() > 0.1f ) {
                mValues[i] = 40f + random.nextFloat() * 55f;
            }
        }

        mCalendar = Calendar.getInstance();
        mDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK)-1;

    }

    public float[] fetchDataForDateRange(int daysBeforeToday, int length) {
        float[] data = new float[length];
        for (int i=0; i< data.length; i++) {
            data[i] = -1000f;
        }
        for (int i=mValues.length-daysBeforeToday-1,j=0;i < mValues.length && j < data.length ; i++,j++){
            if (i > 0) {
                if ( mValues[i] !=null ) {
                    data[j] = mValues[i];
                }
            }
        }
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ButterKnife.bind(this,view);

        graphViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            private String[] mLabels = {null,"SUN","MON","TUE","WED","THU","FRI","SAT",null};
            private int mWidth = 7;
            private int mNumberOfPagesBack = 1024;

            @Override
            public Fragment getItem(int position) {
                int offset = ( mNumberOfPagesBack - ( position + 1 ) ) * mWidth;
                return GraphFragment.newInstance(fetchDataForDateRange(offset+1+mDayOfWeek,mWidth+2), mLabels);
            }

            @Override
            public int getCount() {
                return mNumberOfPagesBack;
            }
        });



        graphViewPager.setCurrentItem(1023);

        return view;
    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        super.onDestroyView();

    }
}

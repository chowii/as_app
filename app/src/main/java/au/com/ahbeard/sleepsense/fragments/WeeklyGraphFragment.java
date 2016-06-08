package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.widgets.WeeklyGraphView;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neal on 12/03/2016.
 */
public class WeeklyGraphFragment extends Fragment {

    @Bind(R.id.graph_view)
    WeeklyGraphView mGraphView;

    private int mWeeksBeforeThisWeek;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_graph, container, false);

        ButterKnife.bind(this, view);

        Calendar calendar = Calendar.getInstance();

        // Find the start of the week and subtract one day.
        calendar.set(Calendar.DAY_OF_WEEK, 1);

        // Count back the number of pages.
        calendar.add(Calendar.DAY_OF_YEAR, mWeeksBeforeThisWeek * 7 - 1);

        int startSleepId = SleepService.getSleepId(calendar);

        calendar.add(Calendar.DAY_OF_YEAR, 7 + 1);

        int endSleepId = SleepService.getSleepId(calendar);

        final String[] labels = SleepService.generateLabels(startSleepId, endSleepId,
                new SimpleDateFormat("EEE", Locale.getDefault()));

        List<Integer> sleepIdList = SleepService.generateSleepIdRange(startSleepId, endSleepId);

        final Integer[] sleepIds = new Integer[sleepIdList.size()];

        for (int i = 0; i < sleepIdList.size(); i++) {
            sleepIds[i] = sleepIdList.get(i);
        }

        mCompositeSubscription.add(SleepService.instance().readSleepScoresAsync(startSleepId, endSleepId).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Float[]>() {
            @Override
            public void call(Float[] sleepScores) {
                mGraphView.setValues(sleepScores, labels, sleepIds, 20, 100);
                mGraphView.setOnClickListener(new WeeklyGraphView.OnClickListener() {
                    @Override
                    public void onValueClicked(Object identifier) {
                        SleepService.instance().notifySleepIdSelected((Integer)identifier);
                    }
                });
            }
        }));

        return view;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public static WeeklyGraphFragment newInstance(int weeksBeforeThisWeek) {
        WeeklyGraphFragment fragment = new WeeklyGraphFragment();
        Bundle args = new Bundle();
        args.putInt("weeks_before_this_week", weeksBeforeThisWeek);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWeeksBeforeThisWeek = getArguments().getInt("weeks_before_this_week");
        }

    }

}

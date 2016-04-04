package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.adapters.SimpleItemAnimator;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFragment extends Fragment {

    LogAdapter mLogAdapter = new LogAdapter();

    @OnClick(R.id.debug_button_acquire)
    void acquire() {
        acquireDevice();
    }

    @OnClick(R.id.debug_button_clear)
    void clear() {
        SleepSenseDeviceService.instance().clearDevices();
    }

    @Bind(R.id.debug_recycler_view_log)
    RecyclerView mLogRecyclerView;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public DebugFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_debug, container, false);

        ButterKnife.bind(this, view);

        mLogRecyclerView.setAdapter(mLogAdapter);
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecyclerView.setItemAnimator(new SimpleItemAnimator());

        updateControls(false);

        mCompositeSubscription.add(SleepSenseDeviceService.instance().getLogObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String message) {
                        mLogAdapter.log(message);
                    }
                }));


        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        mCompositeSubscription.clear();
        super.onDestroyView();
    }

    private void updateControls(boolean isConnected) {
    }

    public static Fragment newInstance() {
        return new DebugFragment();
    }

    /**
     * Log adapter... manages the log entries.
     */
    public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        int mMaxLogItems = 128;
        List<String> mLogItems = new ArrayList<>();

        public LogAdapter() {
        }

        public LogAdapter(int maxLogItems) {
            mMaxLogItems = maxLogItems;
        }

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LogViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_log, parent, false));
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            holder.bind(mLogItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mLogItems.size();
        }

        public void clear() {
            mLogItems.clear();
            notifyDataSetChanged();
        }

        public void log(String message) {
            if (mLogItems.size() >= mMaxLogItems) {
                mLogItems.remove(0);
            }
            mLogItems.add(message);
            notifyDataSetChanged();
        }
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.log_text_view)
        TextView mDebugTextView;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String message) {
            mDebugTextView.setText(message);
        }
    }

    public void acquireDevice() {
        // Attempt to grab the devices, then grab a SleepSense device, then connect to them all.
        SleepSenseDeviceService.instance().acquireDevices(3500)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        if ( SleepSenseDeviceService.instance().getPumpDevice() != null ) {
                            SleepSenseDeviceService.instance().getPumpDevice().getLogObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                                @Override
                                public void call(String message) {
                                   mLogAdapter.log(message);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });


    }
}

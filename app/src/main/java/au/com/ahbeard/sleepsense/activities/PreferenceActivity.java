package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreferenceActivity extends AppCompatActivity {

    @Bind(R.id.preference_seek_bar_sleep_target)
    SeekBar mSeekBar;

    @Bind(R.id.preference_layout_devices)
    ViewGroup mDevicesLayout;

    @Bind(R.id.preference_text_view_sleep_goal)
    TextView mSleepGoalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);

        ButterKnife.bind(this);

        mSeekBar.setMax(6);
        mSeekBar.setProgress((int) (PreferenceService.instance().getSleepTargetTime() * 2 - 6));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    PreferenceService.instance().setSleepTargetTime(progress / 2 + 6);
                    mSleepGoalTextView.setText(
                            String.format("Sleep target %dh %dmin",
                                    (int) PreferenceService.instance().getSleepTargetTime(),
                                    (int) ((PreferenceService.instance().getSleepTargetTime() * 60) % 60)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        add(mDevicesLayout, "Mattress", SleepSenseDeviceService.instance().hasPumpDevice() ? "Connected" : "Not connected");
        add(mDevicesLayout, "Sleep tracker", SleepSenseDeviceService.instance().hasTrackerDevice() ? "Connected" : "Not connected");
        add(mDevicesLayout, "Base", SleepSenseDeviceService.instance().hasBaseDevice() ? "Connected" : "Not connected");

    }

    public void add(ViewGroup container, String title, String detail) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_device, container, false);
        new MoreItemViewHolder().bind(view).populate(title, detail);
        container.addView(view);
    }

    class MoreItemViewHolder {

        Intent mIntent;

        @Bind(R.id.more_text_view_title)
        TextView mTitleTextView;
        @Bind(R.id.more_text_view_detail)
        TextView mDetailTextView;
        @Bind(R.id.more_layout)
        ViewGroup mLayout;

        @OnClick(R.id.more_layout)
        void itemClicked() {
            if (mIntent != null) {
                startActivity(mIntent);
            }
        }

        public MoreItemViewHolder bind(View view) {
            ButterKnife.bind(this, view);
            return this;
        }

        public void populate(String title, String detail) {
            mTitleTextView.setText(title);
            mDetailTextView.setText(detail);
        }


    }

}

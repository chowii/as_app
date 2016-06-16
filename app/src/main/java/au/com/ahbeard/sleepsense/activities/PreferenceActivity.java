package au.com.ahbeard.sleepsense.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    @OnClick(R.id.preference_button_reset_sleepsense)
    void clear() {

        new AlertDialog.Builder(this).setPositiveButton(getString(R.string.preference_dialog_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(PreferenceActivity.this, NewOnBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).setMessage(getString(R.string.preference_dialog_message)).create().show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);

        ButterKnife.bind(this);

        int sleepTargetTimeMinutes = (int) (PreferenceService.instance().getSleepTargetTime() * 60);

        mSeekBar.setMax(6);
        mSeekBar.setProgress((sleepTargetTimeMinutes-360)/30);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    PreferenceService.instance().setSleepTargetTime(progress / 2.0f + 6.0f);

                    int sleepTargetTimeMinutes = (int) (PreferenceService.instance().getSleepTargetTime() * 60);

                    int hours = sleepTargetTimeMinutes/60;
                    int minutes = sleepTargetTimeMinutes % 60;

                    mSleepGoalTextView.setText(String.format(getString(R.string.preferences_sleep_target_time), hours, minutes));
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
        add(mDevicesLayout, "Sleep tracker", SleepSenseDeviceService.instance().hasTrackerDevice() ? PreferenceService.instance().getTrackerDeviceName() : "Not connected");
        add(mDevicesLayout, "Base", SleepSenseDeviceService.instance().hasBaseDevice() ? "Connected" : "Not connected");

        int hours = sleepTargetTimeMinutes/60;
        int minutes = sleepTargetTimeMinutes % 60;

        mSleepGoalTextView.setText(String.format(getString(R.string.preferences_sleep_target_time), hours, minutes));

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

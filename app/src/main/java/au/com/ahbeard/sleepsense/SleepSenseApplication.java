package au.com.ahbeard.sleepsense;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.LogService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.RemoteSleepDataService;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.services.TypefaceService;
import rx.functions.Action1;

/**
 * Created by neal on 3/03/2016.
 */
public class SleepSenseApplication extends Application {

    private static SleepSenseApplication sharedInstance;

    public static SleepSenseApplication instance() {
        return sharedInstance;
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();

        sharedInstance = this;

        LogService.initialize(1024);
        LogService.instance().getLogObservable().subscribe(new Action1<LogService.LogMessage>() {
            @Override
            public void call(LogService.LogMessage logMessage) {
                log(logMessage);
            }
        });

        // Kind of have to do these first!
        TypefaceService.initialize(this);
        PreferenceService.initialize(this);

        // Initialize these later.
        BluetoothService.initialize(this);
        SleepSenseDeviceService.initialize(this);
        SleepService.initialize(this);
        RemoteSleepDataService.initialize(this);
        AnalyticsService.initialize(this);

//        try {
//            AndroidLogger.createInstance(getApplicationContext(),false,true,false,null,0,"fc1fc163-a9c8-4634-bff6-d4b4e577c881", true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *
     * @param logMessage
     */
    private void log(LogService.LogMessage logMessage) {

        switch (logMessage.getLevel()) {

            case Verbose:
                if ( logMessage.getT() == null ) {
                    Log.v(logMessage.getTag(),logMessage.getMessage());
                } else {
                    Log.v(logMessage.getTag(),logMessage.getMessage(),logMessage.getT());
                }
                break;
            case Debug:
                if ( logMessage.getT() == null ) {
                    Log.d(logMessage.getTag(),logMessage.getMessage());
                } else {
                    Log.d(logMessage.getTag(),logMessage.getMessage(),logMessage.getT());
                }
                break;
            case Info:
                if ( logMessage.getT() == null ) {
                    Log.i(logMessage.getTag(),logMessage.getMessage());
                } else {
                    Log.i(logMessage.getTag(),logMessage.getMessage(),logMessage.getT());
                }
                break;
            case Warn:
                if ( logMessage.getT() == null ) {
                    Log.w(logMessage.getTag(),logMessage.getMessage());
                } else {
                    Log.w(logMessage.getTag(),logMessage.getMessage(),logMessage.getT());
                }
                break;
            case Error:
                if ( logMessage.getT() == null ) {
                    Log.e(logMessage.getTag(),logMessage.getMessage());
                } else {
                    Log.e(logMessage.getTag(),logMessage.getMessage(),logMessage.getT());
                }
                break;
            default:
                break;

        }

    }

    private boolean isShowingBluetoothAlert;

    public void showBluetoothOffAlertDialog(final Context context) {
        if (isShowingBluetoothAlert) { return; }
        isShowingBluetoothAlert = true;
        new AlertDialog.Builder(context)
                .setPositiveButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isShowingBluetoothAlert = false;
                    }
                })
                .setNeutralButton(getString(R.string.enable_bluetooth), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        context.startActivity(intent);
                    }
                })
                .setMessage("Bluetooth is disabled.").create().show();
    }
}

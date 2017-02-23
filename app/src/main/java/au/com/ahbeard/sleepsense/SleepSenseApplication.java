package au.com.ahbeard.sleepsense;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;

import com.crashlytics.android.Crashlytics;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.RemoteSleepDataService;
import au.com.ahbeard.sleepsense.services.SSActivityCallbacks;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.services.TypefaceService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import io.fabric.sdk.android.Fabric;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by neal on 3/03/2016.
 */
public class SleepSenseApplication extends Application {

    private static SleepSenseApplication sharedInstance;

    public static SleepSenseApplication instance() {
        return sharedInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedInstance = this;

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
            // In Release builds, don't spit out stacks for errors in rxjava
            RxJavaPlugins.setErrorHandler(Functions.<Throwable>emptyConsumer());
        }

        SSLog.initialize(this);
        String appString = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appString = String.format(" %s (%s)", packageInfo.versionName, packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {

        }
        SSLog.i("Sleepsense Started" + appString);

        // Kind of have to do these first!
        TypefaceService.initialize(this);
        PreferenceService.initialize(this);

        // Initialize these later.
        BluetoothService.initialize(this);
        SleepSenseDeviceService.initialize(this);
        SleepService.initialize(this);
        RemoteSleepDataService.initialize(this);
        AnalyticsService.initialize(this);

        SSActivityCallbacks.initialize(this, new AppActivityLogger());

//        try {
//            AndroidLogger.createInstance(getApplicationContext(),false,true,false,null,0,"fc1fc163-a9c8-4634-bff6-d4b4e577c881", true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
                        isShowingBluetoothAlert = false;
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        context.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .setMessage("Bluetooth is disabled.").create().show();
    }

    private static class AppActivityLogger implements SSActivityCallbacks.AppActivityInterface {

        @Override
        public void appIsActive() {
            SSLog.d("Sleepsense is active");
        }

        @Override
        public void appWentToBackground() {
            SSLog.d("Sleepsense went to background");
        }
    }
}

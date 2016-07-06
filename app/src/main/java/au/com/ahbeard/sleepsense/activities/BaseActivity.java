package au.com.ahbeard.sleepsense.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.SleepSenseApplication;
import au.com.ahbeard.sleepsense.bluetooth.BluetoothEvent;
import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neal on 3/03/2016.
 */
public class BaseActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 123;

    private boolean mCallOnScanningPermissionGranted;

    public void openSleepScoreBreakdown(int sleepId) {

    }

    public void requestBackgroundScanningPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_COARSE_LOCATION);

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_COARSE_LOCATION);
            }
        } else {
            onScanningPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mCallOnScanningPermissionGranted  = true;

                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover Bluetooth Devices near you.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });

                    builder.show();
                }

            }
        }
    }

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCompositeSubscription.add(BluetoothService.instance().getBluetoothEventObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, BluetoothEvent>() {
                    @Override
                    public BluetoothEvent call(Throwable throwable) {
                        return null;
                    }
                })
                .filter(new Func1<BluetoothEvent, Boolean>() {
                    @Override
                    public Boolean call(BluetoothEvent bluetoothEvent) {
                        return bluetoothEvent != null;
                    }
                })
                .subscribe(new Action1<BluetoothEvent>() {
            @Override
            public void call(BluetoothEvent bluetoothEvent) {
                if ( bluetoothEvent instanceof BluetoothEvent.BluetoothUseWhileDisabledEvent ) {
                    showBluetoothOffAlertView();
                }
            }
        }));
    }

    void showBluetoothOffAlertView() {
        SleepSenseApplication.instance().showBluetoothOffAlertDialog(this);
    }

    @Override
    protected void onStop() {
        mCompositeSubscription.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onScanningPermissionGranted() {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if ( mCallOnScanningPermissionGranted ) {
            mCallOnScanningPermissionGranted = false;
            onScanningPermissionGranted();
        }

    }

    public void settingsIconClicked() {


    }

    public void startHelpActivity() {

    }
}

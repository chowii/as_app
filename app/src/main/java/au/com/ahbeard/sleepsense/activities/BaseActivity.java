package au.com.ahbeard.sleepsense.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import au.com.ahbeard.sleepsense.R;

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
}

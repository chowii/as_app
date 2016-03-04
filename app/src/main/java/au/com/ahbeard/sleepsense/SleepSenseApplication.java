package au.com.ahbeard.sleepsense;

import android.app.Application;
import android.text.style.TypefaceSpan;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * Created by neal on 3/03/2016.
 */
public class SleepSenseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TypefaceService.initialize(this);
        PreferenceService.initialize(this);

        BluetoothService.initialize(this);
        SleepSenseDeviceService.initialize(this);
    }
}

package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;

import com.beddit.sensor.SensorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.SleepSenseApplication;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.bleService.SleepsenseScanningFilter;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.hardware.BedHardware;
import au.com.ahbeard.sleepsense.hardware.PumpHardware;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by neal on 4/01/2016.
 */
public class SleepSenseDeviceService {

    public enum SleepSenseDeviceServiceEvent {
        StartedSearchingForDevices,
        FinishedSearchingForDevices,
        DeviceListChanged
    }

    private static SleepSenseDeviceService sSleepSenseDeviceService;

    public static void initialize(Context context) {
        sSleepSenseDeviceService = new SleepSenseDeviceService(context);
    }

    public static SleepSenseDeviceService instance() {
        return sSleepSenseDeviceService;
    }

    private PublishSubject<String> mLogSubject = PublishSubject.create();

    private PublishSubject<SleepSenseDeviceServiceEvent> mChangeEventPublishSubject = PublishSubject.create();

    private String mPumpDeviceAddress;
    private String mBaseDeviceAddress;
    private String mTrackerDeviceAddress;
    private String mAltTrackerDeviceAddress;

    private PumpDevice mPumpDevice;
    private BaseDevice mBaseDevice;
    private TrackerDevice mTrackerDevice;
    private TrackerDevice mAltTrackerDevice;

    private SleepSenseDeviceService(Context context) {
        SensorManager.init(context);

        mPumpDeviceAddress = PreferenceService.instance().getPumpDeviceAddress();
        mBaseDeviceAddress = PreferenceService.instance().getBaseDeviceAddress();
        mTrackerDeviceAddress = PreferenceService.instance().getTrackerDeviceAddress();
        mAltTrackerDeviceAddress = PreferenceService.instance().getAltTrackerDeviceAddress();

        if (mPumpDeviceAddress != null) {
            mPumpDevice = new PumpDevice();
            mPumpDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mPumpDeviceAddress));
        }
        if (mBaseDeviceAddress != null) {
            mBaseDevice = new BaseDevice();
            mBaseDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mBaseDeviceAddress));
        }
        if (mTrackerDeviceAddress != null) {
            mTrackerDevice = new TrackerDevice();
            mTrackerDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mTrackerDeviceAddress));
        }
        if (mAltTrackerDeviceAddress != null) {
            mAltTrackerDevice = new TrackerDevice();
            mAltTrackerDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mAltTrackerDeviceAddress));
        }
    }

    public Observable<SleepSenseDeviceServiceEvent> getEventObservable() {
        return mChangeEventPublishSubject;
    }

    public BaseDevice getBaseDevice() {
        return mBaseDevice;
    }

    public PumpDevice getPumpDevice() {
        return mPumpDevice;
    }

    public TrackerDevice getTrackerDevice() {
        return mTrackerDevice;
    }

    public void log(int level, String message) {
        mLogSubject.onNext(message);
    }

    private static final long deviceScanTimeout = 2500;

    public Observable<List<PumpHardware>> scanPumps() {
        return BluetoothService.instance().scanForBLEDevices(deviceScanTimeout, new SleepsenseScanningFilter())
                .map(new CreateHardwareFunction())
                .map(new Function<List<BedHardware>, List<PumpHardware>>() {
                    @Override
                    public List<PumpHardware> apply(@NonNull List<BedHardware> bedHardware) throws Exception {
                        List<PumpHardware> array = new ArrayList<PumpHardware>();
                        for (BedHardware hardware : bedHardware) {
                            if (hardware instanceof PumpHardware) {
                                array.add((PumpHardware) hardware);
                            }
                        }
                        return array;
                    }
                });
    }

    public Observable<SleepSenseDeviceAquisition> scanDevices() {
        return newAcquireDevices(deviceScanTimeout)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<SleepSenseDeviceAquisition> newAcquireDevices(final long milliseconds) {
        return Observable.create(new ObservableOnSubscribe<SleepSenseDeviceAquisition>() {
            @Override
            public void subscribe(final ObservableEmitter<SleepSenseDeviceAquisition> e) throws Exception {
                SSLog.d("Starting devices scan...");

                mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.StartedSearchingForDevices);

                BluetoothService.instance().scanForBLEDevices(milliseconds, new SleepsenseScanningFilter())
                        .map(new CreateDevicesFunction())
                        .map(new MapToDeviceAquisitionFunction())
                        .subscribe(new Consumer<SleepSenseDeviceAquisition>() {
                            @Override
                            public void accept(@NonNull SleepSenseDeviceAquisition sleepSenseDeviceAquisition) throws Exception {
                                e.onNext(sleepSenseDeviceAquisition);
                                e.onComplete();
                            }
                        });
            }
        });
    }



    public void setDevices(BaseDevice baseDevice, PumpDevice pumpDevice, TrackerDevice trackerDevice, TrackerDevice altTrackerDevice) {

        mBaseDevice = baseDevice;

        if (baseDevice != null) {
            mBaseDeviceAddress = mBaseDevice.getAddress();
            PreferenceService.instance().setBaseDeviceAddress(baseDevice.getAddress());
        } else {
            mBaseDeviceAddress = null;
            PreferenceService.instance().clearBaseDeviceAddress();
        }

        mPumpDevice = pumpDevice;

        if (pumpDevice != null) {
            mPumpDeviceAddress = mPumpDevice.getAddress();
            PreferenceService.instance().setPumpDeviceAddress(pumpDevice.getAddress());
        } else {
            mPumpDeviceAddress = null;
            PreferenceService.instance().clearPumpDeviceAddress();
        }

        mTrackerDevice = trackerDevice;

        if (trackerDevice != null) {
            mTrackerDeviceAddress = mTrackerDevice.getAddress();
            PreferenceService.instance().setTrackerDeviceAddress(trackerDevice.getAddress());
            PreferenceService.instance().setTrackerDeviceName(trackerDevice.getName());
        } else {
            mTrackerDeviceAddress = null;
            PreferenceService.instance().clearTrackerDeviceAddress();
            PreferenceService.instance().clearTrackerDeviceName();
        }

        mAltTrackerDevice = altTrackerDevice;

        if (altTrackerDevice != null ) {
            mAltTrackerDeviceAddress = mAltTrackerDevice.getAddress();
            PreferenceService.instance().setAltTrackerDeviceAddress(altTrackerDevice.getAddress());
            PreferenceService.instance().setAltTrackerDeviceName(altTrackerDevice.getName());
        } else {
            mAltTrackerDeviceAddress = null;
            PreferenceService.instance().clearAltTrackerDeviceAddress();
            PreferenceService.instance().clearAltTrackerDeviceName();
        }
    }

    public boolean hasBaseDevice() {
        return mBaseDevice != null || mBaseDeviceAddress != null;
    }

    public boolean hasPumpDevice() {
        return mPumpDevice != null || mPumpDeviceAddress != null;
    }

    public boolean hasTrackerDevice() {
        return mTrackerDevice != null || mTrackerDeviceAddress != null;
    }

    public boolean hasAltTrackerDevice() {
        return mAltTrackerDevice != null || mAltTrackerDeviceAddress != null;
    }

    private static class CreateDevicesFunction implements Function<List<BluetoothEvent.PacketEvent>, List<Device>> {
        @Override
        public List<Device> apply(@NonNull List<BluetoothEvent.PacketEvent> scanPacketEvents) throws Exception {

            BluetoothService.instance().stopScanning();

            // Sort by RSSI.
            Collections.sort(scanPacketEvents, new Comparator<BluetoothEvent.PacketEvent>() {
                @Override
                public int compare(BluetoothEvent.PacketEvent lhs,
                                   BluetoothEvent.PacketEvent rhs) {
                    return rhs.getRssi() - lhs.getRssi();
                }
            });

            ArrayList<Device> devices = new ArrayList<>();

            Context context = SleepSenseApplication.instance().getApplicationContext();
            for (BluetoothEvent.PacketEvent scanPacketEvent : scanPacketEvents) {
                devices.addAll(SleepSenseDeviceFactory.factorySleepSenseDevice(context, scanPacketEvent));
            }

            SSLog.d("Found %d devices", devices.size());

            return devices;
        }
    }

    private static class MapToDeviceAquisitionFunction implements Function<List<Device>, SleepSenseDeviceAquisition> {
        @Override
        public SleepSenseDeviceAquisition apply(@NonNull List<Device> devices) throws Exception {
            SleepSenseDeviceAquisition sleepSenseDeviceAquisition = new SleepSenseDeviceAquisition();

            // Assign the closest of each device if we don't have one.
            for (Device device : devices) {
                SSLog.d(device.getName());
                // Log.d("SleepSenseDeviceService", String.format("device found: %s", device.getName()));
                if (device instanceof BaseDevice) {
                    sleepSenseDeviceAquisition.getBaseDevices().add((BaseDevice) device);
                    continue;
                }

                if (device instanceof PumpDevice) {
                    sleepSenseDeviceAquisition.getPumpDevices().add((PumpDevice) device);
                    continue;
                }

                if (device instanceof TrackerDevice) {
                    sleepSenseDeviceAquisition.getTrackerDevices().add((TrackerDevice) device);
                }
            }

            return sleepSenseDeviceAquisition;
        }
    }

    private static class CreateHardwareFunction implements Function<List<BluetoothEvent.PacketEvent>, List<BedHardware>> {
        @Override
        public List<BedHardware> apply(@NonNull List<BluetoothEvent.PacketEvent> scanPacketEvents) throws Exception {

            BluetoothService.instance().stopScanning();

            // Sort by RSSI.
            Collections.sort(scanPacketEvents, new Comparator<BluetoothEvent.PacketEvent>() {
                @Override
                public int compare(BluetoothEvent.PacketEvent lhs,
                                   BluetoothEvent.PacketEvent rhs) {
                    return rhs.getRssi() - lhs.getRssi();
                }
            });

            List<BedHardware> devices = new ArrayList<>();

            Context context = SleepSenseApplication.instance().getApplicationContext();
            for (BluetoothEvent.PacketEvent scanPacketEvent : scanPacketEvents) {
                devices.addAll(SleepSenseDeviceFactory.factoryBedHardware(context, scanPacketEvent));
            }

            SSLog.d("Found %d devices", devices.size());

            return devices;
        }
    }

}

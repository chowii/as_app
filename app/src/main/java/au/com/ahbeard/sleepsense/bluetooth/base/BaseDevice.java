package au.com.ahbeard.sleepsense.bluetooth.base;

import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils;
import au.com.ahbeard.sleepsense.bluetooth.Device;

/**
 * Created by neal on 4/03/2016.
 */
public class BaseDevice extends Device {

    private static UUID[] ADVERTISED_UUIDS = {
            BluetoothUtils.uuidFrom16BitUuid(0xffb0),
            BluetoothUtils.uuidFrom16BitUuid(0xffe0),
            BluetoothUtils.uuidFrom16BitUuid(0xffe5),
            BluetoothUtils.uuidFrom16BitUuid(0xfff0),
    };

    public UUID[] getAdvertisedUUIDs() {
        return ADVERTISED_UUIDS;
    }

    /*

    struct BaseCharacteristicPaths {
    static let SendCommand  = CharacteristicPathPredicate(identifier: "FFE5", characteristicIdentifier: "FFE9")
    static let ReadStatus   = CharacteristicPathPredicate(identifier: "FFE0", characteristicIdentifier: "FFE4")
}

/// Criteria that identifies a compatible RealBase peripheral
struct RealBaseCriteria : SerialDeviceCriteria {
    let readPredicate   = BaseCharacteristicPaths.ReadStatus
    let writePredicate  = BaseCharacteristicPaths.SendCommand
}

struct HackWorkaroundExtraBaseCriteria : DeviceCriteria {
    var requiredServices: [ServicePredicate] {
        return [
            ServicePredicate(identifier: "FFF0", characteristicIdentifiers: ["HACK"]),
            ServicePredicate(identifier: "FFB0", characteristicIdentifiers: ["HACK"])
        ]
    }
}

     */

/*

    // Constants for the Simblee chip.
    private static final UUID SERVICE_UUID = UUID.fromString("00002220-0000-1000-8000-00805f9b34fb");

    private static final UUID RECEIVE_CHARACTERISTIC_UUID = UUID.fromString("00002221-0000-1000-8000-00805f9b34fb");
    private static final UUID SEND_CHARACTERISTIC_UUID = UUID.fromString("00002222-0000-1000-8000-00805f9b34fb");

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

 */


}

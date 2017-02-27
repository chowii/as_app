package au.com.ahbeard.sleepsense.bluetooth.pump;

import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * Created by neal on 8/03/2016.
 */
public class PumpStatusObserver implements Consumer<byte[]> {

    private Observer<PumpEvent> mPumpStatusEventObserver;
    private Pattern mAdvancePumpEventPattern = Pattern.compile("EX\\d{12}[\\da-f]{4}");

    private int bufferPointer = -1;
    private char[] buffer = new char[128];

    public PumpStatusObserver(
            Observer<PumpEvent> pumpStatusEventObserver) {
        mPumpStatusEventObserver = pumpStatusEventObserver;
    }

    @Override
    public void accept(@NonNull byte[] bytes) throws Exception {
        for (byte aByte : bytes) {

            if (bufferPointer >= 0 && bufferPointer < 18) {
                // We have already found an 'EX', record
                buffer[bufferPointer++] = (char) aByte;
            }

            if (bufferPointer <= 0 && aByte == 'E') {
                bufferPointer = 0;
                buffer[bufferPointer++] = (char) aByte;
            }

            // Do this last, so we catch a complete mStatus.
            if (bufferPointer >= 18) {
                // Consume the buffer.
                String status = new String(buffer, 0, 18);

                if (mAdvancePumpEventPattern.matcher(status).matches()) {
                    // We have a proper mStatus string.
                    mPumpStatusEventObserver.onNext(new PumpEvent(status));
                }

                bufferPointer = -1;
            }

        }
    }
}

package au.com.ahbeard.sleepsense.bluetooth.pump;

import java.util.regex.Pattern;

import rx.Observer;

/**
 * Created by neal on 8/03/2016.
 */
public class PumpStatusObserver implements Observer<byte[]> {

    private Observer<PumpEvent> mPumpStatusEventObserver;
    private Pattern mAdvancePumpEventPattern = Pattern.compile("EX\\d{12}[\\da-f]{4}");

    int bufferPointer = -1;
    char[] buffer = new char[128];

    public PumpStatusObserver(
            Observer<PumpEvent> pumpStatusEventObserver) {
        mPumpStatusEventObserver = pumpStatusEventObserver;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(byte[] bytes) {

        for (int i = 0; i < bytes.length; i++) {

            if (bufferPointer >= 0 && bufferPointer < 18) {
                // We have already found an 'EX', record
                buffer[bufferPointer++] = (char) bytes[i];
            }

            if (bufferPointer <= 0 && bytes[i] == 'E') {
                bufferPointer = 0;
                buffer[bufferPointer++] = (char) bytes[i];
            }

            // Do this last, so we catch a complete status.
            if (bufferPointer >= 18) {
                // Consume the buffer.
                String status = new String(buffer, 0, 18);

                if (mAdvancePumpEventPattern.matcher(status).matches()) {
                    // We have a proper status string.
                    mPumpStatusEventObserver.onNext(new PumpEvent(status));
                }

                bufferPointer = -1;
            }

        }

    }

}

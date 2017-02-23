package au.com.ahbeard.sleepsense;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpStatusObserver;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static org.junit.Assert.*;

/**
 * Created by Neal Maloney on 8/03/2016.
 */
public class PumpStatusObserverUnitTest {

    @Test
    public void singleExtendedStatus() throws Exception {

        PumpEventCollector pumpEventCollector = new PumpEventCollector();

        PumpStatusObserver pumpStatusObserver = new PumpStatusObserver(pumpEventCollector);

        pumpStatusObserver.accept("EX1112223334440101".getBytes());

        assertEquals(1,pumpEventCollector.getPumpEvents().size());
    }

    @Test
    public void offsetExtendedStatus() throws Exception  {

        PumpEventCollector pumpEventCollector = new PumpEventCollector();

        PumpStatusObserver pumpStatusObserver = new PumpStatusObserver(pumpEventCollector);

        pumpStatusObserver.accept("912EX11122".getBytes());
        pumpStatusObserver.accept("233344401012192891".getBytes());

        assertEquals(1,pumpEventCollector.getPumpEvents().size());
    }

    @Test
    public void multipleExtendedStatus() throws Exception  {

        PumpEventCollector pumpEventCollector = new PumpEventCollector();

        PumpStatusObserver pumpStatusObserver = new PumpStatusObserver(pumpEventCollector);

        pumpStatusObserver.accept("912EX11122".getBytes());
        pumpStatusObserver.accept("23334440101".getBytes());
        pumpStatusObserver.accept("912EX11122".getBytes());
        pumpStatusObserver.accept("23334440101".getBytes());
        pumpStatusObserver.accept("912@#*192_JUNK_EX11122".getBytes());
        pumpStatusObserver.accept("23334440101".getBytes());

        assertEquals(3,pumpEventCollector.getPumpEvents().size());
    }

    @Test
    public void badStatus() throws Exception  {
        PumpEventCollector pumpEventCollector = new PumpEventCollector();

        PumpStatusObserver pumpStatusObserver = new PumpStatusObserver(pumpEventCollector);

        pumpStatusObserver.accept("912EXABC122".getBytes());
        pumpStatusObserver.accept("23334440101".getBytes());
        pumpStatusObserver.accept("912EX11122".getBytes());

        assertEquals(0,pumpEventCollector.getPumpEvents().size());
    }


    /**
     * Simple class to collect the results.
     */
    private class PumpEventCollector implements Observer<PumpEvent> {

        List<PumpEvent> mPumpEvents = new ArrayList<>();

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(PumpEvent pumpEvent) {
            mPumpEvents.add(pumpEvent);
        }

        public List<PumpEvent> getPumpEvents() {
            return mPumpEvents;
        }
    }
}

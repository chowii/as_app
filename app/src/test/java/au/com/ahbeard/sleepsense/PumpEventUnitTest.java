package au.com.ahbeard.sleepsense;

import org.junit.Test;

import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PumpEventUnitTest {

    @Test
    public void testPressures() throws Exception {

        PumpEvent pumpEvent = new PumpEvent("EX1112223334440101");

        System.out.println(pumpEvent.toString());

        assertEquals(111,pumpEvent.getLeftMemory());
        assertEquals(222,pumpEvent.getLeftPressure());
        assertEquals(333,pumpEvent.getRightMemory());
        assertEquals(444,pumpEvent.getRightPressure());

        assertEquals(2,pumpEvent.getStatuses().size());

    }

    @Test
    public void testStatuses() {

        assertTrue(new PumpEvent("EX1112223334440101").getStatuses().contains(PumpEvent.PumpStatus.LeftChamberActive));
        assertTrue(new PumpEvent("EX1112223334440201").getStatuses().contains(PumpEvent.PumpStatus.RightChamberActive));
        assertTrue(new PumpEvent("EX1112223334440140").getStatuses().contains(PumpEvent.PumpStatus.ReInflating));
        assertTrue(new PumpEvent("EX1112223334440210").getStatuses().contains(PumpEvent.PumpStatus.SelfAdapting));
        assertTrue(new PumpEvent("EX1112223334440148").getStatuses().contains(PumpEvent.PumpStatus.SettingMemoryPressure));
        assertTrue(new PumpEvent("EX1112223334440144").getStatuses().contains(PumpEvent.PumpStatus.CheckingPressure));
        assertTrue(new PumpEvent("EX1112223334440142").getStatuses().contains(PumpEvent.PumpStatus.Deflating));
        assertTrue(new PumpEvent("EX1112223334440101").getStatuses().contains(PumpEvent.PumpStatus.Inflating));

        assertEquals(3,new PumpEvent("EX1112223334440148").getStatuses().size());
        assertEquals(8,new PumpEvent("EX111222333444035F").getStatuses().size());

    }
}
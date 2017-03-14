package au.com.ahbeard.sleepsense.ui.onboarding

import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice
import au.com.ahbeard.sleepsense.hardware.bedBase.BedBaseHardware
import au.com.ahbeard.sleepsense.hardware.pump.PumpHardware
import au.com.ahbeard.sleepsense.hardware.tracker.TrackerHardware
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickMattressOnboardingFragment

/**
 * Created by luisramos on 24/01/2017.
 */
data class OnboardingState(
        var mattressLine: PickMattressOnboardingFragment.MattressLine = PickMattressOnboardingFragment.MattressLine.LONG_SINGLE,
        var pumpSide: PumpDevice.Side = PumpDevice.Side.Left,
        var selectedPump: PumpHardware? = null,
        var trackers: List<TrackerHardware> = arrayListOf(),
        var selectedTracker: TrackerHardware? = null,
        var bases: List<BedBaseHardware> = arrayListOf(),
        var selectedBase: BedBaseHardware? = null,
        var heightInCm: Int? = null,
        var weightInKg: Int? = null,
        var isMale: Boolean? = null,
        var age: Int? = null,
        var sleepTarget: Int? = null,
        var numberOfPeopleInBed: Int? = null
)
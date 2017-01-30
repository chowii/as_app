package au.com.ahbeard.sleepsense.ui.onboarding

import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickMattressOnboardingFragment

/**
 * Created by luisramos on 24/01/2017.
 */
data class OnboardingState(
    var mattressLine: PickMattressOnboardingFragment.MattressLine = PickMattressOnboardingFragment.MattressLine.LONG_SINGLE,
    var pumpSide: PumpDevice.Side = PumpDevice.Side.Left
)
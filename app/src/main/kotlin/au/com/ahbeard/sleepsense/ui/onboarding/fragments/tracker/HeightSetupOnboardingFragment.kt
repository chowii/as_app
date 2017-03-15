package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingRulerFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener

/**
 * Created by luisramos on 30/01/2017.
 */
class HeightSetupOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingRulerFragment(listener) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureRuler(R.string.onboarding_height_title, 80, 400, Orientation.VERTICAL)
    }

    override fun presentNextOnboardingFragment() {
        state.heightInCm = currentValue
        super.presentNextOnboardingFragment()
    }
}
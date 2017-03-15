package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingDescFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener

/**
 * Created by luisramos on 30/01/2017.
 */
class TrackerSetupFirstOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingDescFragment(listener) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER

        configureDesc(R.string.onboarding_tracker_setup_1_title, R.string.onboarding_tracker_setup_1_desc, R.drawable.tracker_setup_tracking)
    }
}
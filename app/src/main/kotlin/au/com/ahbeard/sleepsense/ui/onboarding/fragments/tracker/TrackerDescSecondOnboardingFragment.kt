package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingDescFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener

/**
 * Created by luisramos on 16/02/2017.
 */
class TrackerDescSecondOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingDescFragment(listener) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER

        val image = R.drawable.tracker_desc_lay_on_bed
        if (state.mattressLine.isSingle) {
            configureDesc(
                    R.string.onboarding_tracker_desc_2_title_single,
                    R.string.onboarding_tracker_desc_2_desc_single,
                    image)
        } else {
            configureDesc(
                    R.string.onboarding_tracker_desc_2_title_double,
                    R.string.onboarding_tracker_desc_2_desc_double,
                    image)
        }
    }
}
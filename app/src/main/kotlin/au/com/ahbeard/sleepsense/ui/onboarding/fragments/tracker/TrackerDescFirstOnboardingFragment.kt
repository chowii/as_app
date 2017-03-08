package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingDescFragment

/**
 * Created by luisramos on 16/02/2017.
 */
class TrackerDescFirstOnboardingFragment(coordinator: OnboardingCoordinator) : OnboardingDescFragment(coordinator) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER

        if (state.mattressLine.isSingle) {
            configureDesc(
                    R.string.onboarding_tracker_desc_1_title_single,
                    R.string.onboarding_tracker_desc_1_desc_single,
                    R.drawable.tracker_desc_bed_single_transition)
        } else {
            configureDesc(
                    R.string.onboarding_tracker_desc_1_title_double,
                    R.string.onboarding_tracker_desc_1_desc_double,
                    R.drawable.tracker_desc_bed_double_transition)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            val transitionDrawable = imageView.drawable as? TransitionDrawable
            transitionDrawable?.isCrossFadeEnabled = true
            transitionDrawable?.startTransition(1000)
        }, 500)
    }
}
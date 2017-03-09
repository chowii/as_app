package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment

/**
 * Created by luisramos on 23/01/2017.
 */
class PickTrackerOnboardingFragment(coordinator: OnboardingCoordinator) : OnboardingQuestionsFragment(coordinator) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER

        configureQuestions(R.string.onboarding_pick_tracker_title,
                R.string.yes, R.string.no)
    }

    override fun didSelectOption(index: Int) {
        when (index) {
            0 -> scanForTrackers()
            else -> presentNextOnboardingFragment()
        }
    }

    var connecting = false
    fun scanForTrackers() {
        if (connecting) return
        connecting = true

        onboardingActivity.showLoading(R.string.onboarding_connecting_mattress)

        //FIXME Actually connect to device
        view?.postDelayed({
            onboardingActivity.hideLoading({
                presentNextOnboardingFragment()
            })
        }, 2000)
    }

}
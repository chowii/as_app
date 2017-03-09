package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingRulerFragment

/**
 * Created by luisramos on 30/01/2017.
 */
class WeightSetupOnboardingFragment(coordinator: OnboardingCoordinator) : OnboardingRulerFragment(coordinator) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureRuler(R.string.onboarding_weight_title, 20, 250, Orientation.HORIZONTAL)
    }

    override fun presentNextOnboardingFragment() {
        state.weightInKg = currentValue
        super.presentNextOnboardingFragment()
    }
}
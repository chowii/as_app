package au.com.ahbeard.sleepsense.ui.onboarding.fragments

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment

/**
 * Created by luisramos on 30/01/2017.
 */
class DebugPickerOnboardingFragment(coordinator: OnboardingCoordinator) : OnboardingQuestionsFragment(coordinator) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureQuestions(R.string.onboarding_debug_picker_title,
                R.string.debug_pick_mattress)
    }
}
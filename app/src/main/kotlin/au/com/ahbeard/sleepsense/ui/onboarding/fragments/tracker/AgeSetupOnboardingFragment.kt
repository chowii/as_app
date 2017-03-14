package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.services.log.SSLog
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingPickerBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener

/**
 * Created by luisramos on 16/02/2017.
 */
class AgeSetupOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingPickerBaseFragment(listener) {

    private val minAge = 3
    private val maxAge = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configurePicker(R.string.onboarding_age_title, "%d", minAge, maxAge)
    }

    override fun presentNextOnboardingFragment() {
        state.age = pickerView.getSelectedValue()
        SSLog.d("SELECTED AGE ${state.age}")
        super.presentNextOnboardingFragment()
    }
}


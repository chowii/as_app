package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingPickerBaseFragment

/**
 * Created by luisramos on 16/02/2017.
 */
class AgeSetupOnboardingFragment : OnboardingPickerBaseFragment() {

    val minAge = 3
    val maxAge = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configurePicker(R.string.onboarding_age_title, "%d", (minAge..maxAge).toList())
    }
}


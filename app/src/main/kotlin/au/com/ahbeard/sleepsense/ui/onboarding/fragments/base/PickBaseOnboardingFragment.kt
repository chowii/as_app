package au.com.ahbeard.sleepsense.ui.onboarding.fragments.base

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment

/**
 * Created by luisramos on 24/01/2017.
 */
class PickBaseOnboardingFragment : OnboardingQuestionsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleRes = R.string.onboarding_pick_base_title

        data = listOf(getString(R.string.yes), getString(R.string.no)).map(::QuestionViewModel)
    }
}
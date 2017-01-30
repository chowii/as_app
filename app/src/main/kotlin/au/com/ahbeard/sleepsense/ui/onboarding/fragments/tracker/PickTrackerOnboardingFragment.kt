package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment

/**
 * Created by luisramos on 23/01/2017.
 */
class PickTrackerOnboardingFragment: OnboardingQuestionsFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureQuestions(R.string.onboarding_pick_tracker_title,
                R.string.yes, R.string.no)
    }

    override fun didSelectOption(index: Int) {
        presentNextOnboardingFragment()
    }

}
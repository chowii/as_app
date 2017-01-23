package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment

/**
 * Created by luisramos on 23/01/2017.
 */
class PickTrackerOnboardingFragment: OnboardingQuestionsFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        data = listOf(getString(R.string.yes), getString(R.string.no)).map(::QuestionViewModel)
    }


}
package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment

/**
 * Created by luisramos on 16/02/2017.
 */
class SyncTrackerOnboardingFragment : OnboardingBaseFragment() {

    override fun viewsToAnimate(): List<View> {
        return arrayListOf()
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_sync_tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER

        titleRes = R.string.onboarding_sync_tracker_title
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipButton?.visibility = View.GONE
    }
}
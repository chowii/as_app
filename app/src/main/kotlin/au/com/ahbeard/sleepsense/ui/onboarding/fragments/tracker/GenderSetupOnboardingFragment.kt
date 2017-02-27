package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment

/**
 * Created by luisramos on 1/02/2017.
 */
class GenderSetupOnboardingFragment : OnboardingBaseFragment() {

    override fun viewsToAnimate(): List<View> {
        return arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER
        titleRes = R.string.onboarding_gender_title
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_gender
}
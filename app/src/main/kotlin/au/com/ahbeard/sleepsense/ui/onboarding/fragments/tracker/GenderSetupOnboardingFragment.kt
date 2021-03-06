package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSOnboardingRadioControl
import kotterknife.bindView

/**
 * Created by luisramos on 1/02/2017.
 */
class GenderSetupOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingBaseFragment(listener) {

    val radioControl : SSOnboardingRadioControl by bindView(R.id.radioControl)

    override fun viewsToAnimate(): List<View> {
        return arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER
        titleRes = R.string.onboarding_gender_title
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_gender

    override fun presentNextOnboardingFragment() {
        state.isMale = radioControl.isLeftSideSelected
        super.presentNextOnboardingFragment()
    }
}
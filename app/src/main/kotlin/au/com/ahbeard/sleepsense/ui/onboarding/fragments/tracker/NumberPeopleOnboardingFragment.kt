package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSOnboardingRadioControl
import kotterknife.bindView

/**
 * Created by luisramos on 16/02/2017.
 */
class NumberPeopleOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingBaseFragment(listener) {

    val radioControl : SSOnboardingRadioControl by bindView(R.id.radioControl)

    override fun viewsToAnimate(): List<View> {
        return arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER
        titleRes = R.string.onboarding_nr_people_title
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_number_sleepers

    override fun presentNextOnboardingFragment() {

        state.selectedBase?.bluetoothDevice?.address

        state.numberOfPeopleInBed = if (radioControl.isLeftSideSelected) 1 else 2
        super.presentNextOnboardingFragment()
    }
}
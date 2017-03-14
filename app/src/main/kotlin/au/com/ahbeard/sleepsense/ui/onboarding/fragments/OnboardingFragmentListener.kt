package au.com.ahbeard.sleepsense.ui.onboarding.fragments

import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment

/**
 * Created by luisramos on 14/03/2017.
 */
interface OnboardingFragmentListener {
    fun shouldShowBackButton(fragment: OnboardingBaseFragment): Boolean
    fun presentPreviousOnboardingFragment()
    fun presentNextOnboardingFragment()
}
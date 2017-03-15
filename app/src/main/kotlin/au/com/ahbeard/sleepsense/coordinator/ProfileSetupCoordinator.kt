package au.com.ahbeard.sleepsense.coordinator

import android.support.v4.app.FragmentManager
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.OnboardingFlow
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.OnboardingFragmentType
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.ProfileSetupFlow
import au.com.ahbeard.sleepsense.fragments.BaseFragment
import au.com.ahbeard.sleepsense.services.PreferenceService
import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment

/**
 * Created by luisramos on 14/03/2017.
 */
class ProfileSetupCoordinator(fragmentManager: FragmentManager, flow: OnboardingFlow) : OnboardingCoordinator(fragmentManager, flow) {

    companion object {
        @JvmStatic fun newInstance(fragmentManager: FragmentManager, type: OnboardingFragmentType) : BaseFragment {
            val flow = ProfileSetupFlow(arrayListOf(type))
            val coordinator = ProfileSetupCoordinator(fragmentManager, flow)
            return coordinator.createFirstFragment()
        }
    }

    fun createFirstFragment() : BaseFragment {
        return factory(currFragmentType)
    }

    override fun presentNextOnboardingFragment() {
        if (currFragment is OnboardingBaseFragment) {
            saveData((currFragment as OnboardingBaseFragment).state)
        }
        fragmentManager.popBackStack()
    }

    override fun presentPreviousOnboardingFragment() {
        fragmentManager.popBackStack()
    }

    private fun saveData(state: OnboardingState?) {
        state?.let {
            val prefs = PreferenceService.instance()
            prefs.sleepTargetTime = state.sleepTarget?.toFloat() ?: prefs.sleepTargetTime
            prefs.userWeight = state.weightInKg ?: prefs.userWeight
            prefs.userHeight = state.heightInCm ?: prefs.userHeight
            prefs.profileAge = state.age?.toString() ?: prefs.profileAge
            state.isMale?.let {
                prefs.profileSex = if (it) "male" else "female"
            }
            prefs.peopleInBed = state.numberOfPeopleInBed ?: prefs.peopleInBed
        }
    }
}
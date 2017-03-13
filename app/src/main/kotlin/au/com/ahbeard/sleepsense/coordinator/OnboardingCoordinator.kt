package au.com.ahbeard.sleepsense.coordinator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.OnboardingFlow
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.OnboardingFragmentType
import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.base.PickBaseOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickMattressOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickPumpSideOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker.*

/**
 * Created by luisramos on 8/03/2017.
 */
class OnboardingCoordinator(
        val fragmentManager: FragmentManager,
        val flow: OnboardingFlow
) {

    private var currFragmentType = OnboardingFragmentType.PICK_MATTRESS

    val currFragment : Fragment
        get() = fragmentManager.fragments[fragmentManager.backStackEntryCount]

    fun startOnboarding() {
        val fragment = factory(currFragmentType)
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
    }

    fun canPopBackStack() : Boolean {
        return fragmentManager.backStackEntryCount > 0
    }

    fun presentPreviousOnboardingFragment() {
        fragmentManager.popBackStack()
        currFragmentType = flow.prevFragmentType(currFragmentType)
    }

    fun presentNextOnboardingFragment() {
        if (!canPopBackStack()) return

        val state = (currFragment as? OnboardingBaseFragment)?.state

        currFragmentType = flow.nextFragmentType(currFragmentType, state)
        transitionToFragment(factory(currFragmentType))
    }

    private fun transitionToFragment(fragment: OnboardingBaseFragment) {
        if (currFragment is OnboardingBaseFragment) {
            fragment.state = (currFragment as OnboardingBaseFragment).state.copy()
        }

        doTransition(fragment)
    }

    private fun doTransition(fragment: Fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(fragment.javaClass.name)
                .commit()
    }

    private fun factory(type: OnboardingFragmentType) : OnboardingBaseFragment = when (type){
        OnboardingFragmentType.PICK_MATTRESS -> PickMattressOnboardingFragment(this)
        OnboardingFragmentType.PICK_PUMP_SIDE -> PickPumpSideOnboardingFragment(this)
        OnboardingFragmentType.PICK_TRACKER -> PickTrackerOnboardingFragment(this)
        OnboardingFragmentType.PICK_BASE -> PickBaseOnboardingFragment(this)
        OnboardingFragmentType.TRACKER_SETUP_1 -> TrackerSetupFirstOnboardingFragment(this)
        OnboardingFragmentType.TRACKER_SETUP_2 -> TrackerSetupSecondOnboardingFragment(this)
        OnboardingFragmentType.HEIGHT_SELECT -> HeightSetupOnboardingFragment(this)
        OnboardingFragmentType.WEIGHT_SELECT -> WeightSetupOnboardingFragment(this)
        OnboardingFragmentType.GENDER_SELECT -> GenderSetupOnboardingFragment(this)
        OnboardingFragmentType.AGE_SELECT -> AgeSetupOnboardingFragment(this)
        OnboardingFragmentType.SLEEP_TARGET_SELECT -> SleepTargetSetupOnboardingFragment(this)
        OnboardingFragmentType.NUMBER_USERS_SELECT -> NumberPeopleOnboardingFragment(this)
        OnboardingFragmentType.TRACKER_DESC_1 -> TrackerDescFirstOnboardingFragment(this)
        OnboardingFragmentType.TRACKER_DESC_2 -> TrackerDescSecondOnboardingFragment(this)
        OnboardingFragmentType.SYNC_TRACKER -> SyncTrackerOnboardingFragment(this)
        else -> PickTrackerOnboardingFragment(this)
    }

}
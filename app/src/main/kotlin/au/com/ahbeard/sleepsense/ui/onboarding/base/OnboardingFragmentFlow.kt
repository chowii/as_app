package au.com.ahbeard.sleepsense.ui.onboarding.base

import au.com.ahbeard.sleepsense.ui.onboarding.fragments.base.PickBaseOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickMattressOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker.PickTrackerOnboardingFragment

/**
 * Created by luisramos on 24/01/2017.
 */
interface OnboardingFragment {
}

enum class OnboardingFragmentType {
    PICK_MATTRESS,
    PICK_TRACKER,
    PICK_BASE
}

class OnboardingFragmentFlow {

    var currFragmentType = OnboardingFragmentType.PICK_MATTRESS

    var screens = OnboardingFragmentType.values()

    fun firstFragment() : OnboardingBaseFragment {
        return factory(currFragmentType)
    }

    fun nextFragment() : OnboardingBaseFragment? {
        screens.forEachIndexed { i, type ->
            if (type == currFragmentType) {
                currFragmentType = screens[i+1]
                return factory(currFragmentType)
            }
        }
        return null
    }

    fun popFragment() {
        screens.forEachIndexed { i, type ->
            if (type == currFragmentType && i > 0) {
                currFragmentType = screens[i - 1]
            }
        }
    }

    private fun factory(type: OnboardingFragmentType) : OnboardingBaseFragment = when (type){
        OnboardingFragmentType.PICK_MATTRESS -> PickMattressOnboardingFragment()
        OnboardingFragmentType.PICK_TRACKER -> PickTrackerOnboardingFragment()
        OnboardingFragmentType.PICK_BASE -> PickBaseOnboardingFragment()
    }
}


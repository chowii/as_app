package au.com.ahbeard.sleepsense.ui.onboarding.base

import au.com.ahbeard.sleepsense.ui.onboarding.fragments.base.PickBaseOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickMattressOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickPumpSideOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker.*

/**
 * Created by luisramos on 24/01/2017.
 */
interface OnboardingFragment {
}

enum class OnboardingFragmentType {
    DEBUG_PICKER,
    APP_TOUR,
    READY_TO_START,
    PICK_MATTRESS,
    PICK_PUMP_SIDE,
    PICK_TRACKER,
    TRACKER_SETUP_1,
    TRACKER_SETUP_2,
    HEIGHT_SELECT,
    WEIGHT_SELECT,
    GENDER_SELECT,
    AGE_SELECT,
    SLEEP_TARGET_SELECT,
    NUMBER_USERS_SELECT,
    TRACKER_DESC_1,
    TRACKER_DESC_2,
    SYNC_TRACKER,
    PICK_BASE,
    BASE_SETUP_1,
    BASE_SETUP_2,
    SYNC_BASE,
    ALL_DONE;

    private fun typeForPos(pos: Int) : OnboardingFragmentType {
        val types = values()
        val checkPos = Math.max(0, Math.min(pos, types.size - 1))
        return types[checkPos]
    }

    fun nextFragmentType() : OnboardingFragmentType {
        return typeForPos(ordinal + 1)
    }

    fun prevFragmentType() : OnboardingFragmentType {
        return typeForPos(ordinal - 1)
    }
}

class OnboardingFragmentFlow {

    var currFragmentType = OnboardingFragmentType.NUMBER_USERS_SELECT

    fun firstFragment() : OnboardingBaseFragment {
        return factory(currFragmentType)
    }

    fun nextFragment() : OnboardingBaseFragment? {
        currFragmentType = currFragmentType.nextFragmentType()
        return factory(currFragmentType)
    }

    fun popFragment() {
        currFragmentType = currFragmentType.prevFragmentType()
    }

    private fun factory(type: OnboardingFragmentType) : OnboardingBaseFragment = when (type){
        OnboardingFragmentType.PICK_MATTRESS -> PickMattressOnboardingFragment()
        OnboardingFragmentType.PICK_PUMP_SIDE -> PickPumpSideOnboardingFragment()
        OnboardingFragmentType.PICK_TRACKER -> PickTrackerOnboardingFragment()
        OnboardingFragmentType.PICK_BASE -> PickBaseOnboardingFragment()
        OnboardingFragmentType.TRACKER_SETUP_1 -> TrackerSetupFirstOnboardingFragment()
        OnboardingFragmentType.TRACKER_SETUP_2 -> TrackerSetupSecondOnboardingFragment()
        OnboardingFragmentType.HEIGHT_SELECT -> HeightSetupOnboardingFragment()
        OnboardingFragmentType.WEIGHT_SELECT -> WeightSetupOnboardingFragment()
        OnboardingFragmentType.GENDER_SELECT -> GenderSetupOnboardingFragment()
        OnboardingFragmentType.NUMBER_USERS_SELECT -> NumberPeopleOnboardingFragment()
        OnboardingFragmentType.TRACKER_DESC_1 -> TrackerDescFirstOnboardingFragment()
        OnboardingFragmentType.TRACKER_DESC_2 -> TrackerDescSecondOnboardingFragment()
        else -> PickTrackerOnboardingFragment()
    }
}


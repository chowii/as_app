package au.com.ahbeard.sleepsense.coordinator.onboardingFlow

import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState

/**
 * Created by luisramos on 8/03/2017.
 */
class DefaultOnboardingFlow : OnboardingFlow() {

    override var screenOrder: List<OnboardingFragmentType> = arrayListOf(
//            OnboardingFragmentType.DEBUG_PICKER,
//            OnboardingFragmentType.APP_TOUR,
//            OnboardingFragmentType.READY_TO_START,
            OnboardingFragmentType.PICK_MATTRESS,
            OnboardingFragmentType.PICK_PUMP_SIDE,
            OnboardingFragmentType.PICK_TRACKER,
            OnboardingFragmentType.TRACKER_SETUP_1,
            OnboardingFragmentType.TRACKER_SETUP_2,
            OnboardingFragmentType.HEIGHT_SELECT,
            OnboardingFragmentType.WEIGHT_SELECT,
            OnboardingFragmentType.GENDER_SELECT,
            OnboardingFragmentType.AGE_SELECT,
            OnboardingFragmentType.SLEEP_TARGET_SELECT,
            OnboardingFragmentType.NUMBER_USERS_SELECT,
            OnboardingFragmentType.TRACKER_DESC_1,
            OnboardingFragmentType.TRACKER_DESC_2,
            OnboardingFragmentType.SYNC_TRACKER,
            OnboardingFragmentType.PICK_BASE,
            OnboardingFragmentType.BASE_SETUP_1,
            OnboardingFragmentType.BASE_SETUP_2,
            OnboardingFragmentType.SYNC_BASE,
            OnboardingFragmentType.SETUP_NOT_COMPLETED,
            OnboardingFragmentType.ALL_DONE
    )

    override fun getFirstFragmentType(): OnboardingFragmentType {
        return OnboardingFragmentType.PICK_MATTRESS
    }

    override fun nextFragmentType(type: OnboardingFragmentType, state: OnboardingState?): OnboardingFragmentType {
        if (state != null) {
            when (type) {
                OnboardingFragmentType.PICK_MATTRESS -> {
                    if (state.mattressLine.isSingle) {
                        return OnboardingFragmentType.PICK_PUMP_SIDE
                    } else {
                        return OnboardingFragmentType.PICK_TRACKER
                    }
                }
                OnboardingFragmentType.PICK_TRACKER -> {
                    if (state.trackers.isNotEmpty()) {
                        return OnboardingFragmentType.TRACKER_SETUP_1
                    } else {
                        return OnboardingFragmentType.PICK_BASE
                    }
                }
                OnboardingFragmentType.SLEEP_TARGET_SELECT -> {
                    if (state.mattressLine.isSingle) { //skip number users if single mattress
                        return OnboardingFragmentType.TRACKER_DESC_1
                    } else {
                        return OnboardingFragmentType.NUMBER_USERS_SELECT
                    }
                }
                OnboardingFragmentType.PICK_BASE -> {
                    if (state.bases.isNotEmpty() && state.mattressLine.hasSplitBase) {
                        return OnboardingFragmentType.BASE_SETUP_1
                    } else {
                        return allDoneOrBust(state)
                    }
                }
                else -> return super.nextFragmentType(type, state)
            }
        }
        return super.nextFragmentType(type, state)
    }

    private fun allDoneOrBust(state: OnboardingState) : OnboardingFragmentType {
        if (state.selectedBase != null || state.selectedPump != null || state.selectedTracker != null) {
            return OnboardingFragmentType.ALL_DONE
        } else {
            return OnboardingFragmentType.NOT_SURE
        }
    }
}
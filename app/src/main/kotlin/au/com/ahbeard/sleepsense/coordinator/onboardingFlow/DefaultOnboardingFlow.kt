package au.com.ahbeard.sleepsense.coordinator.onboardingFlow

/**
 * Created by luisramos on 8/03/2017.
 */
class DefaultOnboardingFlow : OnboardingFlow() {

    override var screenOrder: List<OnboardingFragmentType> = arrayListOf(
            OnboardingFragmentType.DEBUG_PICKER,
            OnboardingFragmentType.APP_TOUR,
            OnboardingFragmentType.READY_TO_START,
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
            OnboardingFragmentType.ALL_DONE
    )

}
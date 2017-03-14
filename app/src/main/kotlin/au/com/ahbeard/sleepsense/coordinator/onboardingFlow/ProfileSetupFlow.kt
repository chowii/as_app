package au.com.ahbeard.sleepsense.coordinator.onboardingFlow

/**
 * Created by luisramos on 14/03/2017.
 */
class ProfileSetupFlow(override var screenOrder: List<OnboardingFragmentType>) : OnboardingFlow() {

    override fun getFirstFragmentType(): OnboardingFragmentType {
        return screenOrder[0]
    }
}
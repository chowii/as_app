package au.com.ahbeard.sleepsense.coordinator.onboardingFlow

import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState

/**
 * Created by luisramos on 8/03/2017.
 */
abstract class OnboardingFlow {

    abstract var screenOrder: List<OnboardingFragmentType> get

    private fun typeForPos(pos: Int) : OnboardingFragmentType {
        val checkPos = Math.max(0, Math.min(pos, screenOrder.size - 1))
        return screenOrder[checkPos]
    }

    open fun nextFragmentType(type: OnboardingFragmentType, state: OnboardingState?) : OnboardingFragmentType {
        return typeForPos(screenOrder.indexOf(type) + 1)
    }

    fun prevFragmentType(type: OnboardingFragmentType) : OnboardingFragmentType {
        return typeForPos(screenOrder.indexOf(type) - 1)
    }

}
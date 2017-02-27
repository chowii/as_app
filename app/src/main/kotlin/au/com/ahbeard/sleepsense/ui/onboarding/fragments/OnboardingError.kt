package au.com.ahbeard.sleepsense.ui.onboarding.fragments

/**
 * Created by luisramos on 24/02/2017.
 */
open class OnboardingError: Throwable()

class OnboardingErrorPumpNotFound: OnboardingError()
class OnboardingErrorPumpCantConnect: OnboardingError()
class OnboardingErrorTrackerNotFound: OnboardingError()
class OnboardingErrorTrackerNotFoundTwo: OnboardingError()
class OnboardingErrorTrackerLostConnection: OnboardingError()
class OnboardingErrorBaseNotFound: OnboardingError()
class OnboardingErrorBaseNotFoundTwo: OnboardingError()
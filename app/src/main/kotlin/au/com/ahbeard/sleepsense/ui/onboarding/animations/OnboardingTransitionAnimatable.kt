package au.com.ahbeard.sleepsense.ui.onboarding.animations

import android.view.View

/**
 * Created by luisramos on 25/01/2017.
 */
interface OnboardingTransitionAnimatable {
    fun viewsToAnimate(): List<View>
}

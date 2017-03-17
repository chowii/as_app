package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingDescFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener

/**
 * Created by naveenu on 17/03/2017.
 */
class BaseDescSecondOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingDescFragment(listener) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.BASE

        configureDesc(
                R.string.onboarding_base_sync_2_title,
                R.string.onboarding_base_sync_2_description,
                R.drawable.ic_lay_down_person)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            val transitionDrawable = imageView.drawable as? TransitionDrawable
            transitionDrawable?.isCrossFadeEnabled = true
            transitionDrawable?.startTransition(1000)
        }, 500)
    }

}
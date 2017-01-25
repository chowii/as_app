package au.com.ahbeard.sleepsense.ui.onboarding.animations

import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.utils.DeviceUtils

/**
 * Created by luisramos on 25/01/2017.
 */
object OnboardingTransitionAnimator {

    private val animationDuration = 600L

    fun animatedTransitionToFragment(fragmentManager: FragmentManager, fragment: OnboardingBaseFragment) {
        val fromFragment = previousFragmentInStack(fragmentManager)

        var delay = 0L
        if (fromFragment is OnboardingBaseFragment) {
            fromFragment.viewsToAnimate().forEach {
                animateView(it, delay)
                delay += 100
            }
        }

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(fragment.javaClass.name)
                .commit()

        Handler().postDelayed({
            //conclude animation stuff
        }, delay + animationDuration)
    }

    private fun previousFragmentInStack(fragmentManager: FragmentManager) : Fragment? {
        return fragmentManager.fragments[fragmentManager.backStackEntryCount]
    }

    private fun animateViews(views: List<View>, fromFragment: OnboardingBaseFragment, toFragment: OnboardingBaseFragment, animateLeft: Boolean) {

    }

    private fun animateView(view: View, delay: Long) {
        view.animate()
                .translationXBy(-DeviceUtils.deviceWidthPx.toFloat())
                .setDuration(animationDuration)
                .setStartDelay(delay)
                .start()
    }

    fun prepareViewsForEntryAnim(fragment: OnboardingBaseFragment) {
        fragment.view?.background?.alpha = 0

        fragment.viewsToAnimate().forEach {
            it.animate().translationXBy(400f)
        }
    }
}
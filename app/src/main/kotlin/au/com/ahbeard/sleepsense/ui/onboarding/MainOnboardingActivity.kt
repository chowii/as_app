package au.com.ahbeard.sleepsense.ui.onboarding


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.activities.BaseActivity
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingFragmentFlow
import au.com.ahbeard.sleepsense.utils.DeviceUtils

class MainOnboardingActivity : BaseActivity() {

    val flow = OnboardingFragmentFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, flow.firstFragment())
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            flow.popFragment()
        } else {
            super.onBackPressed()
        }
    }

    fun presentNextOnboardingFragment() {
        flow.nextFragment()?.let { transitionToFragment(it) }
    }

    fun transitionToFragment(fragment: OnboardingBaseFragment) {
//        OnboardingTransitionAnimator.animatedTransitionToFragment(supportFragmentManager, fragment)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(fragment.javaClass.name)
                .commit()
    }
}

interface OnboardingTransitionAnimatable {
    fun viewsToAnimate(): List<View>
}

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
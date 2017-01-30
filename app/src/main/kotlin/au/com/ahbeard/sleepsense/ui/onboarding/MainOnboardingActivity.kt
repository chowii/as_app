package au.com.ahbeard.sleepsense.ui.onboarding


import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.activities.BaseActivity
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingFragmentFlow
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingLoadingFragment

class MainOnboardingActivity : BaseActivity() {

    val flow = OnboardingFragmentFlow()

    val currFragment : Fragment
        get() = supportFragmentManager.fragments[fragmentManager.backStackEntryCount]

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

    fun showLoading(@StringRes alertText: Int, @StringRes doneText: Int = R.string.connected) {
        val loadingFragment = OnboardingLoadingFragment(getString(alertText), getString(doneText))

        loadingFragment.backgroundToUse = currFragment.view?.background

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.alpha_loading_enter, R.anim.alpha_loading_exit)
                .add(R.id.fragmentContainer, loadingFragment, loadingFragment.javaClass.name)
                .commit()
    }

    fun hideLoading(completion: () -> Unit) {
        val loadingFrag = supportFragmentManager.findFragmentByTag(OnboardingLoadingFragment::class.java.name) as? OnboardingLoadingFragment
        loadingFrag?.stopAnimations {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.alpha_loading_enter, R.anim.alpha_loading_exit)
                    .remove(loadingFrag)
                    .commit()
            completion()
        }
    }

    fun presentNextOnboardingFragment() {
        flow.nextFragment()?.let { transitionToFragment(it) }
    }

    fun transitionToFragment(fragment: OnboardingBaseFragment) {
        if (currFragment is OnboardingBaseFragment) {
            fragment.state = (currFragment as OnboardingBaseFragment).state.copy()
        }

//        OnboardingTransitionAnimator.animatedTransitionToFragment(supportFragmentManager, fragment)

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(fragment.javaClass.name)
                .commit()
    }
}
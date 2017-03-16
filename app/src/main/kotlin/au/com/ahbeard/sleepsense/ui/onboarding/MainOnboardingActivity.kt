package au.com.ahbeard.sleepsense.ui.onboarding


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.activities.BaseActivity
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.DefaultOnboardingFlow
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingLoadingFragment

class MainOnboardingActivity : BaseActivity() {

    val coordinator: OnboardingCoordinator by lazy {
        OnboardingCoordinator(supportFragmentManager, DefaultOnboardingFlow())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_main)

        if (savedInstanceState == null) {
            coordinator.startOnboarding()
        }

        //FIXME: This should be done in the App Tour fragments
        requestBackgroundScanningPermissions()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            coordinator.presentPreviousOnboardingFragment()
        } else {
            super.onBackPressed()
        }
    }

    fun showLoading(@StringRes alertText: Int, @StringRes doneText: Int = R.string.connected) {
        Handler(Looper.getMainLooper()).post {
            val loadingFragment = OnboardingLoadingFragment(getString(alertText), getString(doneText))

            loadingFragment.backgroundToUse = coordinator.currFragment.view?.background

            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.alpha_loading_enter, R.anim.alpha_loading_exit)
                    .add(R.id.fragmentContainer, loadingFragment, loadingFragment.javaClass.name)
                    .commit()
        }
    }

    fun hideLoading(completion: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            val loadingFrag = supportFragmentManager.findFragmentByTag(OnboardingLoadingFragment::class.java.name) as? OnboardingLoadingFragment
            loadingFrag?.stopAnimations {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.alpha_loading_enter, R.anim.alpha_loading_exit)
                        .remove(loadingFrag)
                        .commit()
                completion()
            }
        }
    }

    fun hideLoadingWhenAnError(completion: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            val loadingFrag = supportFragmentManager.findFragmentByTag(OnboardingLoadingFragment::class.java.name) as? OnboardingLoadingFragment
            loadingFrag?.stopAnimationsOnError {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.alpha_loading_enter, R.anim.alpha_loading_exit)
                        .remove(loadingFrag)
                        .commit()
                completion()
            }
        }
    }
}
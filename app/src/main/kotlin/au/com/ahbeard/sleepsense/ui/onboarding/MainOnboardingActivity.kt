package au.com.ahbeard.sleepsense.ui.onboarding


import android.os.Bundle
import android.support.v4.app.Fragment
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.activities.BaseActivity
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress.PickMattressOnboardingFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker.PickTrackerOnboardingFragment

class MainOnboardingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_main)

        if (savedInstanceState == null) {
            val firstFragment = PickMattressOnboardingFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, firstFragment)
                    .commit()
        }
    }

    fun presentPreviousOnboardingFragment() {

    }

    fun presentNextOnboardingFragment() {
        //TODO show the actual next fragment

        transitionToFragment(PickTrackerOnboardingFragment(), true)
    }

    fun transitionToFragment(fragment: Fragment, isPushing: Boolean) {
        val enterAnim = if (isPushing) R.anim.enter_from_right else R.anim.enter_from_left
        val exitAnim = if (isPushing) R.anim.exit_to_left else R.anim.exit_to_right
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
    }
}


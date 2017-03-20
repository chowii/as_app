package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.services.PreferenceService
import au.com.ahbeard.sleepsense.services.log.SSLog
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingPickerBaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import kotterknife.bindView

/**
 * Created by luisramos on 20/02/2017.
 */
class SleepTargetSetupOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingPickerBaseFragment(listener) {

    private val minSleepTarget = 2
    private val maxSleepTarget = 16

    val descTextView: TextView by bindView(R.id.descTextView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configurePicker(R.string.onboarding_sleep_target_title, "%d hours", minSleepTarget, maxSleepTarget,
                PreferenceService.instance().sleepTargetTime.toInt())
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        descTextView.visibility = View.VISIBLE
        descTextView.text = getString(R.string.sleep_target_desc)
    }

    override fun presentNextOnboardingFragment() {
        state.sleepTarget = pickerView.getSelectedValue()
        SSLog.d("SELECTED SLEEP TARGET ${state.sleepTarget}")
        super.presentNextOnboardingFragment()
    }
}
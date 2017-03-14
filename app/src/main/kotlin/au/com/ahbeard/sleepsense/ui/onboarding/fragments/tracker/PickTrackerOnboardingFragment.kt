package au.com.ahbeard.sleepsense.ui.onboarding.fragments.tracker

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.hardware.tracker.TrackerHardware
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingErrorTrackerNotFound
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingErrorTrackerOnlyFoundOne
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable

/**
 * Created by luisramos on 23/01/2017.
 */
class PickTrackerOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingQuestionsFragment(listener) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER

        configureQuestions(R.string.onboarding_pick_tracker_title,
                R.string.yes, R.string.no)
    }

    override fun didSelectOption(index: Int) {
        when (index) {
            0 -> scanForTrackers()
            else -> presentNextOnboardingFragment()
        }
    }

    var connecting = false
    fun scanForTrackers() {
        if (connecting) return
        connecting = true

        onboardingActivity.showLoading(R.string.onboarding_connecting_tracker)

        getScanTrackersObservable()
                .subscribe({
                    state.trackers = it

                    connecting = false
                    onboardingActivity.hideLoading({
                        presentNextOnboardingFragment()
                    })
                }, { error ->
                    connecting = false
                    onboardingActivity.hideLoading {
                        handleError(error)
                    }
                })
    }

    private fun getScanTrackersObservable() : Observable<List<TrackerHardware>> {
        return SleepSenseDeviceService.instance().scanTrackers()
                .bindToLifecycle(this)
                .flatMap {
                    if (it.size <= 0) {
                        Observable.error<List<TrackerHardware>> { OnboardingErrorTrackerNotFound() }
                    } else if (state.mattressLine.isSingle && it.size <= 1) {
                        Observable.error<List<TrackerHardware>> { OnboardingErrorTrackerOnlyFoundOne() }
                    } else {
                        Observable.just(it)
                    }
                }
    }

}
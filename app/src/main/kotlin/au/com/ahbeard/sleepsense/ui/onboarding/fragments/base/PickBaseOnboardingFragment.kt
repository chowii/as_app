package au.com.ahbeard.sleepsense.ui.onboarding.fragments.base

import android.os.Bundle
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.hardware.bedBase.BedBaseHardware
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingErrorBaseNotFound
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingErrorBaseOnlyFoundOne
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable

/**
 * Created by luisramos on 24/01/2017.
 */
class PickBaseOnboardingFragment(listener: OnboardingFragmentListener) : OnboardingQuestionsFragment(listener) {

    private var connecting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleRes = R.string.onboarding_pick_base_title

        data = listOf(getString(R.string.yes), getString(R.string.no)).map(::QuestionViewModel)
    }

    override fun didSelectOption(index: Int) {
        when (index) {
            0 -> scanForBases() //Yes
            else -> presentNextOnboardingFragment()
        }
    }

    fun scanForBases() {
        if (connecting) return
        connecting = true
        onboardingActivity.showLoading(R.string.onboarding_connecting_base)

        getScanBaseObservable()
                .subscribe({

                    state.bases = it

                    // if our mattres only has one base
                    // and we have found more than one
                    // pick the closest one
                    if (state.mattressLine.hasSplitBase && it.isNotEmpty()) {
                        state.selectedBase = it[0]
                    }

                    connecting = false
                    onboardingActivity.hideLoading({
                        presentNextOnboardingFragment()
                    })
                }, { error ->
                    connecting = false
                    onboardingActivity.hideLoadingWhenAnError {
                        handleError(error)
                    }
                })
    }

    private fun getScanBaseObservable() : Observable<List<BedBaseHardware>> {
        return SleepSenseDeviceService.instance().scanBases()
                .bindToLifecycle(this)
                .flatMap {
                    if (it.size <= 0) {
                        Observable.error<List<BedBaseHardware>> { OnboardingErrorBaseNotFound() }
                    } else if (state.mattressLine.hasSplitBase && it.size <= 1) {
                        Observable.error<List<BedBaseHardware>> { OnboardingErrorBaseOnlyFoundOne() }
                    } else {
                        Observable.just(it)
                    }
                }
    }
}
package au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.view.View
import android.widget.RelativeLayout
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.hardware.pump.PumpHardware
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingErrorPumpCantConnect
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingErrorPumpNotFound
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSNotSureOverlayView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable

/**
* Created by luisramos on 23/01/2017.
*/
class PickMattressOnboardingFragment(coordinator: OnboardingCoordinator) : OnboardingQuestionsFragment(coordinator) {

    var connecting = false

    val notSureOverlayView : SSNotSureOverlayView by lazy {
        val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)

        val notOverlayView = SSNotSureOverlayView(view!!.context)
        notOverlayView.layoutParams = layoutParams
        notOverlayView.visibility = View.INVISIBLE

        containerView.addView(notOverlayView)

        notOverlayView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleRes = R.string.onboarding_pickMattress_title

        var array = MattressLine.values().map { QuestionViewModel(activity.getString(it.nameRes)) }
        array += arrayOf(QuestionViewModel(activity.getString(R.string.not_sure), true))
        data = array

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notSureOverlayView.closeButton.setOnClickListener {
            hideNotSureOverlay()
        }
    }

    override fun didSelectOption(index: Int) {
        if (index == data.size - 1) {
            showNotSureOverlay()
        } else {
            state.mattressLine = MattressLine.values()[index]
            scanForPumps()
        }
    }

    private fun showNotSureOverlay() {
        notSureOverlayView.animateEntry(view!!)
    }

    private fun hideNotSureOverlay() {
        notSureOverlayView.animateExit()
    }

    fun scanForPumps() {
        if (connecting) return
        connecting = true
        onboardingActivity.showLoading(R.string.onboarding_connecting_mattress)

        getScanPumpObservable()
                .subscribe({
                    state.selectedPump = it

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

    private fun getScanPumpObservable() : Observable<PumpHardware> {
        return SleepSenseDeviceService.instance().scanPumps()
                .bindToLifecycle(this)
                .flatMap {
                    if (it.size <= 0) {
                        Observable.error<PumpHardware> { OnboardingErrorPumpNotFound() }
                    } else {
                        Observable.just(it[0])
                    }
                }
                .flatMap {
                    it.connect().toObservable()
                            .onErrorResumeNext { error: Throwable ->
                                Observable.error<PumpHardware> { OnboardingErrorPumpCantConnect() }
                            }
                }
    }

    enum class MattressLine(@IntegerRes val nameRes: Int) {
        LONG_SINGLE(R.string.onboarding_pickMattress_long_single),
        KING_SINGLE(R.string.onboarding_pickMattress_king_single),
        DOUBLE(R.string.onboarding_pickMattress_double),
        QUEEN(R.string.onboarding_pickMattress_queen),
        KING(R.string.onboarding_pickMattress_king),
        SUPER_KING(R.string.onboarding_pickMattress_super_king),
        KING_SPLIT(R.string.onboarding_pickMattress_kingSplit);

        val isSingle: Boolean
            get() = this == LONG_SINGLE || this == KING_SINGLE

        val hasSplitBase: Boolean
            get() = this == KING_SPLIT
    }
}
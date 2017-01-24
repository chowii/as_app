package au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.widget.Toast
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment

/**
* Created by luisramos on 23/01/2017.
*/
class PickMattressOnboardingFragment : OnboardingQuestionsFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleRes = R.string.onboarding_pickMattress_title

        var array = MattressLine.values().map { QuestionViewModel(activity.getString(it.nameRes)) }
        array += arrayOf(QuestionViewModel(activity.getString(R.string.not_sure), true))
        data = array

        super.onCreate(savedInstanceState)
    }

    override fun didSelectOption(index: Int) {
        if (index == data.size - 1) {
            //Show not sure screen
            Toast.makeText(activity, "Not sure screen not implemented", Toast.LENGTH_SHORT).show()
        } else {
            state.mattressLine = MattressLine.values()[index]
            scanForPumps()
        }
    }

    fun scanForPumps() {
        onboardingActivity.showLoading(R.string.onboarding_connecting_mattress)
    }

    enum class MattressLine(@IntegerRes val nameRes: Int) {
        LONG_SINGLE(R.string.onboarding_pickMattress_long_single),
        KING_SINGLE(R.string.onboarding_pickMattress_king_single),
        DOUBLE(R.string.onboarding_pickMattress_double),
        QUEEN(R.string.onboarding_pickMattress_queen),
        KING(R.string.onboarding_pickMattress_king),
        SUPER_KING(R.string.onboarding_pickMattress_super_king),
        KING_SPLIT(R.string.onboarding_pickMattress_kingSplit)
    }
}
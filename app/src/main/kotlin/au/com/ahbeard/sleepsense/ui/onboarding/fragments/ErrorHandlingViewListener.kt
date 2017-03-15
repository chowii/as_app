package au.com.ahbeard.sleepsense.ui.onboarding.fragments

/**
 * Created by naveenu on 15/03/2017.
 */
interface ErrorHandlingViewListener {
    fun onTryAgainClick()
    fun onSetUpLaterClick()
    fun onTroubleshootingClick(errorTitle: String)
}
package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by luisramos on 16/02/2017.
 */
class ShadowOverscrollOnScrollListener(val shadowView: View) : RecyclerView.OnScrollListener() {
    private var overallYScroll = 0
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        overallYScroll += dy

        if (overallYScroll > 0 && shadowView.alpha != 1f) {
            shadowView.animate().setDuration(150).alpha(1f).start()
        } else if (overallYScroll <= 0 && shadowView.alpha != 0f) {
            shadowView.animate().setDuration(150).alpha(0f).start()
        }
    }
}
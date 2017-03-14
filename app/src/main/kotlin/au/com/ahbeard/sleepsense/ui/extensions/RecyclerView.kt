package au.com.ahbeard.sleepsense.ui.extensions

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by luisramos on 14/03/2017.
 */
fun RecyclerView.getVisibleViews() : List<View> {
    val layoutManager = this.layoutManager as? LinearLayoutManager
    layoutManager?.let {
        val firstChildPos = layoutManager. findFirstVisibleItemPosition()
        val lastChildPos = layoutManager.findLastVisibleItemPosition()
        return (firstChildPos..lastChildPos).map { layoutManager.findViewByPosition(it) } .filter { it != null }
    }
    return arrayListOf()
}
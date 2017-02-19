package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by luisramos on 16/02/2017.
 *
 * This method dependes
 */
abstract class RecyclerWithMarginsAdapter(val itemViewSize: Int) : RecyclerView.Adapter<RecyclerWithMarginsAdapter.MarginViewHolder>() {

    constructor() : this(0)

    var recyclerViewSize = 0
        set(value) {
            //Only notify if recyclerViewSize changes
            val prev = field
            field = value
            if (prev != field) {
                notifyDataSetChanged()
            }
        }

    open class MarginViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        open fun setMargins(topMargin: Int, bottomMargin: Int, leftMargin: Int, rightMargin: Int) {
            val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.topMargin = topMargin
            layoutParams.bottomMargin = bottomMargin
            layoutParams.leftMargin= leftMargin
            layoutParams.rightMargin= rightMargin
            itemView.layoutParams = layoutParams
        }
    }

    override fun onBindViewHolder(holder: RecyclerWithMarginsAdapter.MarginViewHolder?, position: Int) {
        val marginTop = if (position == 0) recyclerViewSize / 2 - itemViewSize else 0
        val marginBottom = if (position == itemCount - 1) recyclerViewSize / 2 - itemViewSize else 0

        holder?.setMargins(marginTop, marginBottom, 0, 0)
    }

}
package au.com.ahbeard.sleepsense.ui.onboarding.views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import kotterknife.bindView

/**
 * Created by luisramos on 1/02/2017.
 */
class SSOnboardingRadioButton : RelativeLayout {

    val circleView: View by bindView(R.id.backgroundCircleView)
    val imageButton: ImageButton by bindView(R.id.imageButton)
    val textView: TextView by bindView(R.id.textView)

    val delegateClickListener: View.OnClickListener? = null

    constructor(context: Context?) : super(context) {
        setup(null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setup(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(attrs)
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        setup(attrs)
    }

    private fun setup(attrs: AttributeSet?) {
        View.inflate(context, R.layout.onboarding_radio_button, this)

        attrs?.let {
            val a = context.theme.obtainStyledAttributes(it, R.styleable.SSOnboardingRadioButton, 0 ,0)

            val imageRes = a.getResourceId(R.styleable.SSOnboardingRadioButton_icon, R.drawable.ic_male_symbol_white)
            val text = a.getString(R.styleable.SSOnboardingRadioButton_text)

            imageButton.setImageResource(imageRes)
            textView.text = text
        }

        circleView.scaleX = 0f
        circleView.scaleY = 0f
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        imageButton.isSelected = selected
        animateWhiteCircle()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        imageButton.setOnClickListener(l)
    }

    private fun animateWhiteCircle() {
        val scale = if (isSelected) 1f else 0f

        circleView.animate()
                .setDuration(200)
                .scaleY(scale)
                .scaleX(scale)
                .start()
    }
}
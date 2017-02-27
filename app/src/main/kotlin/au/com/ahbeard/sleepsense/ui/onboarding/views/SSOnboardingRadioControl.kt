package au.com.ahbeard.sleepsense.ui.onboarding.views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import au.com.ahbeard.sleepsense.R
import kotterknife.bindView

/**
 * Created by luisramos on 1/02/2017.
 */
class SSOnboardingRadioControl: RelativeLayout {

    val leftButton: SSOnboardingRadioButton by bindView(R.id.leftButton)
    val rightButton: SSOnboardingRadioButton by bindView(R.id.rightButton)

    val isLeftSideSelected: Boolean
        get() = leftButton.isSelected

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
        View.inflate(context, R.layout.onboarding_radio_control, this)

        attrs?.let {
            val a = context.theme.obtainStyledAttributes(it, R.styleable.SSOnboardingRadioControl, 0, 0)

            val leftIcon = a.getResourceId(R.styleable.SSOnboardingRadioControl_leftIcon, R.drawable.ic_male_symbol_white)
            val leftText = a.getString(R.styleable.SSOnboardingRadioControl_leftText)
            val rightIcon = a.getResourceId(R.styleable.SSOnboardingRadioControl_rightIcon, R.drawable.ic_male_symbol_white)
            val rightText = a.getString(R.styleable.SSOnboardingRadioControl_rightText)

            leftButton.imageButton.setImageResource(leftIcon)
            leftButton.textView.text = leftText
            rightButton.imageButton.setImageResource(rightIcon)
            rightButton.textView.text = rightText
        }

        leftButton.isSelected = true

        leftButton.setOnClickListener {
            if (leftButton.isSelected) return@setOnClickListener

            toggleSelectedButton()
        }

        rightButton.setOnClickListener {
            if (rightButton.isSelected) return@setOnClickListener

            toggleSelectedButton()
        }
    }

    private fun toggleSelectedButton() {
        leftButton.isSelected = !leftButton.isSelected
        rightButton.isSelected = !rightButton.isSelected
    }
}
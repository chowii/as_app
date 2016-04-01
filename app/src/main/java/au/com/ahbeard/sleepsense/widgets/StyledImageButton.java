package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * So far this just makes the button square.
 */
public class StyledImageButton extends ImageButton {

    private boolean mMakeSquareBasedOnWidth = true;

    public StyledImageButton(Context context) {
        super(context);
    }

    public StyledImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StyledImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean ismakeSquareBasedOnWidth() {
        return mMakeSquareBasedOnWidth;
    }

    public void setMakeSquareBasedOnWidth(boolean mMakeSquareBasedOnWidth) {
        this.mMakeSquareBasedOnWidth = mMakeSquareBasedOnWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMakeSquareBasedOnWidth) {
            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }
    }
}

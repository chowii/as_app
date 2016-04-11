package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * Created by neal on 30/03/2016.
 */
public class StyledLinearLayout extends LinearLayout {

    private Paint mBorderPaint = new Paint();

    private boolean mDrawTopBorder;
    private boolean mDrawBottomBorder;

    private boolean mActuallyDrawTopBorder;
    private boolean mActuallyDrawBottomBorder;

    public StyledLinearLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public StyledLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StyledLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (mActuallyDrawTopBorder) {
            canvas.drawLine(0, 0, canvas.getWidth(), 0, mBorderPaint);
        }

        if (mActuallyDrawBottomBorder) {
            canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), mBorderPaint);
        }

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mActuallyDrawTopBorder = mDrawTopBorder && (isPressed()|| isSelected());
        mActuallyDrawBottomBorder = mDrawBottomBorder && (isPressed()||isSelected());
        invalidate();
    }



//    @Override
//    public void setSelected(boolean selected) {
//        super.setSelected(selected);
//        for (int i=0; i< getChildCount();i++) {
//            getChildAt(i).setSelected(selected);
//        }
//        invalidate();
//    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StyledView, defStyleAttr, 0);

        mDrawTopBorder = a.getBoolean(R.styleable.StyledView_topBorder, false);
        mDrawBottomBorder = a.getBoolean(R.styleable.StyledView_bottomBorder, false);

        mBorderPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics()));
        mBorderPaint.setColor(a.getColor(R.styleable.StyledView_borderColor, Color.TRANSPARENT));

        a.recycle();
    }


}

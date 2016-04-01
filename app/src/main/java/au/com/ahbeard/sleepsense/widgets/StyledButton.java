package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * Created by neal on 30/03/2016.
 */
public class StyledButton extends Button {

    private Paint mBorderPaint = new Paint();

    private boolean mDrawTopBorder;

    public StyledButton(Context context) {
        super(context);
        init(null, 0);
    }

    public StyledButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StyledButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mBorderPaint.setColor(getCurrentTextColor());

        if (mDrawTopBorder) {
            canvas.drawLine(0, 0, canvas.getWidth(), 0, mBorderPaint);
        }

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mDrawTopBorder = selected;
//        for (int i=0 ; i < getCompoundDrawables().length;i++) {
//            if( getCompoundDrawables()[i]!=null) {
//                Log.d("STYLED_BUTTON","getDrawableState()"+getDrawableState());
//                getCompoundDrawables()[i].setState(getDrawableState());
//            }
//        }
        invalidate();
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StyledView, defStyleAttr, 0);

        mDrawTopBorder = a.getBoolean(R.styleable.StyledView_topBorder, false);

        String typefaceName = a.getString(R.styleable.StyledView_typeface);

        if (typefaceName != null) {
            Typeface typeface;

            if (isInEditMode()) {
                typeface = TypefaceService.instance(getContext()).getTypeface(typefaceName);
            } else {
                typeface = TypefaceService.instance().getTypeface(typefaceName);
            }

            if (typeface != null) {
                setTypeface(typeface);
            }
        }

        mBorderPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics()));

        a.recycle();
    }


}

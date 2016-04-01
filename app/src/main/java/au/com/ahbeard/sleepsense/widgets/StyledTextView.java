package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * Created by neal on 3/03/2016.
 */
public class StyledTextView extends TextView {

    private Paint mBorderPaint = new Paint();

    private boolean mDrawTopBorder = false;

    private Typeface mTypeface;
    private Typeface mSelectedTypeface;

    public StyledTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StyledTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StyledView, defStyleAttr, 0);

        String typefaceName = a.getString(R.styleable.StyledView_typeface);
        String selectedTypefaceName = a.getString(R.styleable.StyledView_selectedTypeface);

        mDrawTopBorder = a.getBoolean(R.styleable.StyledView_topBorder,false);

        if (typefaceName != null) {

            if (isInEditMode()) {
                mTypeface = TypefaceService.instance(getContext()).getTypeface(typefaceName);
            } else {
                mTypeface = TypefaceService.instance().getTypeface(typefaceName);
            }

            if (mTypeface != null) {
                setTypeface(mTypeface);
            }
        } else {
            mTypeface = getTypeface();
        }

        mSelectedTypeface = mTypeface;

        if (selectedTypefaceName != null) {
            if (isInEditMode()) {
                mSelectedTypeface = TypefaceService.instance(getContext()).getTypeface(selectedTypefaceName);
            } else {
                mSelectedTypeface = TypefaceService.instance().getTypeface(selectedTypefaceName);
            }
        }

        mBorderPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics()));
        mBorderPaint.setColor(getCurrentTextColor());

        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (StateSet.stateSetMatches(getDrawableState(), View.SELECTED_STATE_SET ) ) {
            if ( mTypeface != null ) {
                setTypeface(mSelectedTypeface);
            }
        } else {
            if ( mSelectedTypeface != null ) {
                setTypeface(mTypeface);
            }
        }
    }

    public void setDrawTopBorder(boolean mDrawTopBorder) {
        this.mDrawTopBorder = mDrawTopBorder;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mBorderPaint.setColor(color);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawTopBorder) {
            canvas.drawLine(0, 0, canvas.getWidth(), 0, mBorderPaint);
        }
    }
}

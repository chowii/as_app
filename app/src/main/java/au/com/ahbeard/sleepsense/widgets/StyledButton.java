package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by neal on 30/03/2016.
 */
public class StyledButton extends Button {

    public interface OnPressPulseListener {
        void onPressPulse(StyledButton view);
    }

    private OnPressPulseListener mOnPressPulseListener;

    private Paint mBorderPaint = new Paint();

    private boolean mDrawTopBorder;
    private boolean mDrawBottomBorder;

    private boolean mActuallyDrawTopBorder;
    private boolean mActuallyDrawBottomBorder;

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
        mActuallyDrawTopBorder = mDrawTopBorder && (isPressed()||StateSet.stateSetMatches(getDrawableState(),SELECTED_STATE_SET));
        mActuallyDrawBottomBorder = mDrawBottomBorder && (isPressed()||StateSet.stateSetMatches(getDrawableState(),SELECTED_STATE_SET));
        invalidate();
    }

    private Disposable mPulseTimerSubscription;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
            mPulseTimerSubscription = AndroidSchedulers.mainThread().createWorker().schedulePeriodically(new Runnable() {
                @Override
                public void run() {
                    if (mOnPressPulseListener!=null) {
                        mOnPressPulseListener.onPressPulse(StyledButton.this);
                    }
                }
            },0,200, TimeUnit.MILLISECONDS);

        } else if (event.getAction() == MotionEvent.ACTION_UP ) {
            if ( mPulseTimerSubscription != null ) {
                mPulseTimerSubscription.dispose();
                mPulseTimerSubscription = null;
            }
        }

        return super.onTouchEvent(event);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StyledView, defStyleAttr, 0);

        mDrawTopBorder = a.getBoolean(R.styleable.StyledView_topBorder, false);
        mDrawBottomBorder = a.getBoolean(R.styleable.StyledView_bottomBorder, false);

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

    public void setOnPressPulseListener(OnPressPulseListener onPressPulseListener) {
        mOnPressPulseListener = onPressPulseListener;
    }
}

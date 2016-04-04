package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * So far this just makes the button square.
 */
public class StyledImageButton extends ImageButton {

    private boolean mMakeSquareBasedOnWidth = true;

    public interface OnPressPulseListener {
        void onPressPulse(StyledImageButton view);
    }

    private OnPressPulseListener mOnPressPulseListener;

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

    private Subscription mPulseTimerSubscription;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
            mPulseTimerSubscription = AndroidSchedulers.mainThread().createWorker().schedulePeriodically(new Action0() {
                @Override
                public void call() {
                    if (mOnPressPulseListener!=null) {
                        mOnPressPulseListener.onPressPulse(StyledImageButton.this);
                    }
                }
            },0,200, TimeUnit.MILLISECONDS);

        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if ( mPulseTimerSubscription != null ) {
                mPulseTimerSubscription.unsubscribe();
                mPulseTimerSubscription = null;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setOnPressPulseListener(OnPressPulseListener onPressPulseListener) {
        mOnPressPulseListener = onPressPulseListener;
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

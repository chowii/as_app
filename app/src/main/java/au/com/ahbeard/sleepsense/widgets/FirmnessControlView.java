package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by neal on 8/03/2016.
 */
public class FirmnessControlView extends View {

    private Drawable mLevelDrawable;
    private Drawable mForegroundDrawable;
    private Drawable mKnobDrawable;
    private Drawable mRotatingDrawable;

    private Point mCenterPoint = new Point();
    private Point mKnobCenterPoint = new Point();

    private float mValue;

    public FirmnessControlView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FirmnessControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FirmnessControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.FirmnessControlView, defStyleAttr, 0);

        mLevelDrawable = a.getDrawable(R.styleable.FirmnessControlView_levelDrawable);
        mForegroundDrawable = a.getDrawable(R.styleable.FirmnessControlView_foregroundDrawable);
        mKnobDrawable = a.getDrawable(R.styleable.FirmnessControlView_knobDrawable);

    }

    public Drawable getLevelDrawable() {
        return mLevelDrawable;
    }

    public void setLevelDrawable(Drawable levelDrawable) {
        mLevelDrawable = levelDrawable;
    }

    public Drawable getForegroundDrawable() {
        return mForegroundDrawable;
    }

    public void setForegroundDrawable(Drawable foregroundDrawable) {
        mForegroundDrawable = foregroundDrawable;
    }

    public void setLevel(int level) {
        Log.d("TOUCH",String.format("setLevel: %d",level));
        mLevelDrawable.setLevel(level);
    }

    private boolean mUpdating;
    private int mXTranslate;
    private int mYTranslate;
    private float mLastAngle;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mUpdating = true;
                mXTranslate = getWidth() / 2;
                mYTranslate = getHeight() / 2;
                mLastAngle = (float) Math.atan2(event.getX() - mXTranslate, -(event.getY() - mYTranslate));

                Log.d("TOUCH", String.format("lastAngle: %f", mLastAngle));

                return true;

            case MotionEvent.ACTION_MOVE:

                if (mUpdating) {

                    float eventAngle = (float) (Math.atan2(event.getX() - mXTranslate, -(event.getY() - mYTranslate)));

                    float deltaAngle = eventAngle - mLastAngle;

//                    if (deltaAngle < Math.PI) {
//                        deltaAngle += Math.PI * 2;
//                    }

                    Log.d("TOUCH", String.format("lastAngle: %f", mLastAngle));
                    float deltaValue = 8f * deltaAngle / (float) Math.PI;

                    if (Math.abs(deltaValue) > 0.5) {

                        mValue += deltaValue;

                        if (mValue < 1.0f) {
                            mValue = 1.0f;
                        }
                        if (mValue > 10.0f) {
                            mValue = 10.0f;
                        }

                        mLastAngle = eventAngle;

                        setLevel(Math.round(mValue));

//                        if ( mRingDialUpdateListener != null ) {
//                            mRingDialUpdateListener.valueUpdated(mValue);
//                        }

                        postInvalidate();
                    }

                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                mUpdating = false;

//                if ( mRingDialUpdateListener != null ) {
//                    mRingDialUpdateListener.finishedUpdatingValue(mValue);
//                }

                break;
        }

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (mLevelDrawable != null) {
            mLevelDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mLevelDrawable.draw(canvas);
        }

        if (mForegroundDrawable != null) {
            mForegroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mForegroundDrawable.draw(canvas);
        }

        if (mKnobDrawable != null) {

            // How much to scale the knob by.
            float mScaleWidth = (float) canvas.getWidth() / (float) mForegroundDrawable.getIntrinsicWidth();
            float mScaleHeight = (float) canvas.getHeight() / (float) mForegroundDrawable.getIntrinsicHeight();

            int width = (int) (mKnobDrawable.getIntrinsicWidth() * mScaleWidth);
            int height = (int) (mKnobDrawable.getIntrinsicHeight() * mScaleHeight);

            mKnobDrawable.setBounds(mKnobCenterPoint.x, mKnobCenterPoint.y, width, height);
            mKnobDrawable.draw(canvas);
        }


    }
}

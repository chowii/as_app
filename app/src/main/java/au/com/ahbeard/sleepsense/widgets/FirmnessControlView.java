package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by neal on 8/03/2016.
 */
public class FirmnessControlView extends View {

    public static final float TO_RADIANS = 0.01745329252f;
    public static final float FROM_RADIANS = 1f / TO_RADIANS;

    private Drawable mLevelDrawable;
    private Drawable mForegroundDrawable;
    private Drawable mKnobDrawable;
    private Drawable mRotatingDrawable;

    private int mDotColor = Color.DKGRAY;
    private float mDotRadius;

    private PointF mKnobCenterPoint = new PointF(0.0f, -0.6f);
    private PointF mDotCenterPoint = new PointF(0.0f, -0.8f);

    private Paint mDotPaint = new Paint();

    private PointF mRotatedKnobCenterPoint = new PointF();
    private PointF mRotatedDotCenterPoint = new PointF();

    private float mValue = 0.0f;

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

        mDotPaint.setColor(a.getColor(R.styleable.FirmnessControlView_dotColor, Color.DKGRAY));
        mDotRadius = a.getDimension(R.styleable.FirmnessControlView_dotRadius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));

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

    public void setValue(float value) {
        mValue = value;
        if (mLevelDrawable != null) {
            Log.d("TOUCH", String.format("setLevel: %d", Math.round(mValue)));
            mLevelDrawable.setLevel(Math.round(mValue));
        }
        postInvalidate();
    }

    public void setDotColor(int dotColor) {
        mDotColor = dotColor;
        postInvalidate();
    }

    public void setDotRadius(int dotRadius) {
        mDotRadius = dotRadius;
        postInvalidate();
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

                    // Crossing angle boundaries.
                    if (deltaAngle < -Math.PI) {
                        deltaAngle += Math.PI * 2;
                    } else if (deltaAngle > Math.PI) {
                        deltaAngle -= Math.PI * 2;
                    }

                    Log.d("TOUCH", String.format("lastAngle: %f", mLastAngle));

                    float deltaValue = 8f * deltaAngle / (float) Math.PI;

                    if (Math.abs(deltaValue) > 0.01f) {

                        float newValue = mValue + deltaValue;

                        if (newValue < 0.0f) {
                            newValue = 0.0f;
                        }
                        if (newValue > 10.0f) {
                            newValue = 10.0f;
                        }

                        setValue(newValue);

                        mLastAngle = eventAngle;

                        Log.d("TOUCH", String.format("%.5f", mValue));

//                        if ( mRingDialUpdateListener != null ) {
//                            mRingDialUpdateListener.valueUpdated(mValue);
//                        }

                    }

                    postInvalidate();

                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:

                mUpdating = false;

                mValue = Math.round(mValue);
                Log.d("TOUCH", String.format("%.5f", mValue));

                postInvalidate();

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

        if (isInEditMode()) {
            Paint paint = new Paint();
            paint.setColor(Color.LTGRAY);
            canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,paint);
            return;
        }

        if (mLevelDrawable != null) {

            mLevelDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mLevelDrawable.draw(canvas);

        }

        if (mForegroundDrawable != null) {

            mForegroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mForegroundDrawable.draw(canvas);

        }

        if (mKnobDrawable != null) {

            canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

            // How much to scale the knob by.
            float mScaleWidth = (float) canvas.getWidth() / (float) mForegroundDrawable.getIntrinsicWidth();
            float mScaleHeight = (float) canvas.getHeight() / (float) mForegroundDrawable.getIntrinsicHeight();

            int width = (int) (mKnobDrawable.getIntrinsicWidth() * mScaleWidth);
            int height = (int) (mKnobDrawable.getIntrinsicHeight() * mScaleHeight);

            float rotationAngle = -137f * TO_RADIANS + 274f * (mValue) / 10f * TO_RADIANS;

            mRotatedKnobCenterPoint.x = (float) (mKnobCenterPoint.x * Math.cos(rotationAngle)
                    - mKnobCenterPoint.y * Math.sin(rotationAngle));
            mRotatedKnobCenterPoint.y = (float) (mKnobCenterPoint.y * Math.cos(rotationAngle)
                    + mKnobCenterPoint.x * Math.sin(rotationAngle));

            mKnobDrawable.setBounds(
                    (int) (-width / 2 + mRotatedKnobCenterPoint.x * canvas.getWidth() / 2),
                    (int) (-height / 2 + mRotatedKnobCenterPoint.y * canvas.getHeight() / 2),
                    (int) (width / 2 + mRotatedKnobCenterPoint.x * canvas.getWidth() / 2),
                    (int) (height / 2 + +mRotatedKnobCenterPoint.y * canvas.getHeight() / 2)
            );

            mKnobDrawable.draw(canvas);

            mRotatedDotCenterPoint.x = (float) (mDotCenterPoint.x * Math.cos(rotationAngle)
                    - mDotCenterPoint.y * Math.sin(rotationAngle));
            mRotatedDotCenterPoint.y = (float) (mDotCenterPoint.y * Math.cos(rotationAngle)
                    + mDotCenterPoint.x * Math.sin(rotationAngle));

            Log.d("TOUCH", "rotatedDOt: " + mRotatedDotCenterPoint);

            canvas.drawCircle(mRotatedDotCenterPoint.x * canvas.getWidth() / 2,
                    mRotatedDotCenterPoint.y * canvas.getHeight() / 2, mDotRadius, mDotPaint);

        }


    }
}

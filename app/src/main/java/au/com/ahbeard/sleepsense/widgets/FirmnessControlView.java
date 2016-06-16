package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.Firmness;

/**
 * Created by neal on 8/03/2016.
 * <p/>
 * This control is
 */
public class FirmnessControlView extends View {

    private static float START_ANGLE = -139f;
    private static float SWEEP = 274f;

    private OnTargetValueSetListener mOnTargetValueSetListener;
    private float mActualValue;
    private float mForegroundInset;
    private float mForegroundShadowOffset;
    private RectF mValueRect;
    private RectF mForegroundRect;

    public interface OnTargetValueSetListener {
        void onTargetValueSet(float targetValue);
    }

    public static final float TO_RADIANS = 0.01745329252f;
    public static final float FROM_RADIANS = 1f / TO_RADIANS;

    private Drawable mKnobDrawable;

    private int mTargetMetDotColor = Color.DKGRAY;
    private int mTargetNotMetDotColor = Color.DKGRAY;

    private float mDotRadius;

    private PointF mKnobCenterPoint = new PointF(0.0f, -0.6f);
    private PointF mDotCenterPoint = new PointF(0.0f, -0.785f);

    private Paint mDotPaint = new Paint();
    private Paint mWedgePaint = new Paint();
    private Paint mBackgroundPaint = new Paint();
    private Paint mForegroundPaint = new Paint();
    private Paint mForegroundShadowPaint = new Paint();

    private PointF mRotatedKnobCenterPoint = new PointF();
    private PointF mRotatedDotCenterPoint = new PointF();

    private float mTargetValue = 0.0f;

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

        mKnobDrawable = a.getDrawable(R.styleable.FirmnessControlView_knobDrawable);

        mTargetMetDotColor = a.getColor(R.styleable.FirmnessControlView_targetMetDotColor, Color.DKGRAY);
        mTargetNotMetDotColor = a.getColor(R.styleable.FirmnessControlView_targetNotMetDotColor, Color.DKGRAY);

        mDotRadius = a.getDimension(R.styleable.FirmnessControlView_dotRadius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));

        mWedgePaint.setColor(getResources().getColor(R.color.firmnessControllerColor));
        mBackgroundPaint.setColor(getResources().getColor(R.color.firmnessControllerBackgroundColor));
        mForegroundPaint.setColor(getResources().getColor(R.color.firmnessControllerForegroundColor));
        mForegroundShadowPaint.setColor(getResources().getColor(R.color.firmnessControllerForegroundShadowColor));

        mForegroundInset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
        mForegroundShadowOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

    }

    private boolean mSetTargetValueWithActualValue = true;

    public void setActualValue(float value) {

        mActualValue = value;

        if (mSetTargetValueWithActualValue) {
            mTargetValue = mActualValue;
            mSetTargetValueWithActualValue = false;
        }

        if (mActualValue > mTargetValue - 0.025f && mActualValue < mTargetValue + 0.025f) {
            mDotPaint.setColor(mTargetMetDotColor);
        } else {
            mDotPaint.setColor(mTargetNotMetDotColor);
        }


        postInvalidate();
    }

    public void setDotRadius(int dotRadius) {
        mDotRadius = dotRadius;
        postInvalidate();
    }

    public void setOnTargetValueSetListener(OnTargetValueSetListener onTargetValueSetListener) {
        mOnTargetValueSetListener = onTargetValueSetListener;
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

                    float deltaValue = 0.8f * deltaAngle / (float) Math.PI;

                    if (Math.abs(deltaValue) > 0.01f) {

                        float newValue = mTargetValue + deltaValue;

                        if (newValue < 0.0f) {
                            newValue = 0.0f;
                        }
                        if (newValue > 1.0f) {
                            newValue = 1.0f;
                        }

                        mTargetValue = newValue;
                        postInvalidate();

                        mLastAngle = eventAngle;

                    }

                    postInvalidate();

                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:

                mUpdating = false;

                // Round the value (so we have a 0-9 range).
                mTargetValue = Firmness.snapControlValue(mTargetValue);
                mSetTargetValueWithActualValue = false;

                if (mOnTargetValueSetListener != null) {
                    mOnTargetValueSetListener.onTargetValueSet(mTargetValue);
                }

                postInvalidate();

                break;
        }

        return false;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        mValueRect = new RectF(0, 0, width, height);
        mForegroundInset=width*0.065f;
        mForegroundRect = new RectF(mForegroundInset, mForegroundInset, width - mForegroundInset, height - mForegroundInset);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int fullWidth = canvas.getWidth();
        int fullHeight = canvas.getHeight();

        if (isInEditMode()) {
            Paint paint = new Paint();
            paint.setColor(Color.LTGRAY);
            canvas.drawCircle(fullWidth / 2, fullHeight / 2, fullWidth / 2, paint);
            return;
        }

        canvas.drawArc(mValueRect, START_ANGLE - 90, SWEEP, true, mBackgroundPaint);
        canvas.drawArc(mValueRect, START_ANGLE - 90, SWEEP * mActualValue, true, mWedgePaint);


        for ( int i=0; i <= 10; i++) {
            float sweepPercentage = i/10.0f;
            canvas.drawArc(mValueRect, START_ANGLE - 90 +sweepPercentage*SWEEP, 0.5f, true, mForegroundPaint);
        }

        canvas.save();
        canvas.translate(0,mForegroundShadowOffset);
        canvas.drawArc(mForegroundRect, START_ANGLE - 90, 360, false, mForegroundShadowPaint);
        canvas.restore();
        canvas.drawArc(mForegroundRect, START_ANGLE - 90, 360, false, mForegroundPaint);

        if (mKnobDrawable != null) {

            canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

            int width = mKnobDrawable.getIntrinsicWidth();
            int height = mKnobDrawable.getIntrinsicHeight();

            //
            float rotationAngle = START_ANGLE * TO_RADIANS + SWEEP * (mTargetValue) * TO_RADIANS;

            mRotatedKnobCenterPoint.x = (float) (mKnobCenterPoint.x * Math.cos(rotationAngle)
                    - mKnobCenterPoint.y * Math.sin(rotationAngle));
            mRotatedKnobCenterPoint.y = (float) (mKnobCenterPoint.y * Math.cos(rotationAngle)
                    + mKnobCenterPoint.x * Math.sin(rotationAngle));

            mKnobDrawable.setBounds(
                    (int) (-width / 2 + mRotatedKnobCenterPoint.x * fullWidth / 2),
                    (int) (-height / 2 + mRotatedKnobCenterPoint.y * fullHeight / 2),
                    (int) (width / 2 + mRotatedKnobCenterPoint.x * fullWidth / 2),
                    (int) (height / 2 + +mRotatedKnobCenterPoint.y * fullHeight / 2)
            );

            mKnobDrawable.draw(canvas);

            mRotatedDotCenterPoint.x = (float) (mDotCenterPoint.x * Math.cos(rotationAngle)
                    - mDotCenterPoint.y * Math.sin(rotationAngle));
            mRotatedDotCenterPoint.y = (float) (mDotCenterPoint.y * Math.cos(rotationAngle)
                    + mDotCenterPoint.x * Math.sin(rotationAngle));

            // Log.d("TOUCH", "rotatedDot: " + mRotatedDotCenterPoint);

            canvas.drawCircle(mRotatedDotCenterPoint.x * fullWidth / 2,
                    mRotatedDotCenterPoint.y * fullHeight / 2, mDotRadius, mDotPaint);

        }


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width < height) {
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(height, height);
        }
    }

}

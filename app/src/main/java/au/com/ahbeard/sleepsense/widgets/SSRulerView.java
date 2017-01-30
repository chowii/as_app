package au.com.ahbeard.sleepsense.widgets;

/**
 * Created by vimal on 11/17/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;
import au.com.ahbeard.sleepsense.utils.DeviceUtilsKt;

public class SSRulerView extends View {
    static int screenSize = 480;
    static private float pxmm = screenSize / 67.f;

    private float mainPoint = 0;
    private float mainPointClone = 0;
    private boolean isMove;
    private onViewUpdateListener mListener;
    private Paint midLinePaint;
    private float rulersize = 0;
    private Paint rulerPaint, textPaint, goldenPaint;

    private int scaleLineSmall;
    private int scaleLineMedium;
    private int scaleLineLarge;
    private int textStartPoint;
    private int rulerPointer = 0; //this variable will cut the ruler pointer by desired point

    int width, height, midScreenPoint;

    float downpoint = 0, movablePoint = 0, downPointClone = 0;
    boolean isDown = false;
    boolean isUpward = false;
    boolean isSizeChanged = false;
    float userStartingPoint = 0f;
    boolean isFirstTime = true;


    public SSRulerView(Context context) {
        super(context);
        init(null, 0);
    }

    public SSRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SSRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SSRulerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        midLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        rulersize = pxmm * 10;
        rulerPaint = new Paint();
        rulerPaint.setStyle(Paint.Style.STROKE);
        rulerPaint.setStrokeWidth(0);
        rulerPaint.setAntiAlias(false);
        rulerPaint.setColor(Color.WHITE);

        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(0);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(getResources().getDimension(R.dimen.ruler_txt_size));
        textPaint.setColor(Color.WHITE);

//        goldenPaint = new Paint();
//        goldenPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        goldenPaint.setColor(Color.WHITE);
//        goldenPaint.setStrokeWidth(yellowLineStrokeWidth);
//        goldenPaint.setStrokeJoin(Paint.Join.ROUND);
//        goldenPaint.setStrokeCap(Paint.Cap.ROUND);
//        goldenPaint.setPathEffect(new CornerPathEffect(10));
//        goldenPaint.setAntiAlias(true);

        scaleLineSmall = (int) getResources().getDimension(R.dimen.scale_line_small);
        scaleLineMedium = (int) getResources().getDimension(R.dimen.scale_line_medium);
        scaleLineLarge = (int) getResources().getDimension(R.dimen.scale_line_large);
        textStartPoint = (int) getResources().getDimension(R.dimen.text_start_point);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SSRulerView, defStyleAttr, 0);

        int midLineColor = a.getColor(R.styleable.SSRulerView_midLineColor, ContextCompat.getColor(getContext(), android.R.color.white));
        int ruleLineColor = a.getColor(R.styleable.SSRulerView_ruleLineColor, ContextCompat.getColor(getContext(), android.R.color.white));
        int textColor = a.getColor(R.styleable.SSRulerView_textColor, ContextCompat.getColor(getContext(), android.R.color.white));

        midLinePaint.setColor(midLineColor);
        rulerPaint.setColor(ruleLineColor);
        textPaint.setColor(textColor);

        if (!isInEditMode()) {
            String textFont = a.getString(R.styleable.SSRulerView_textFont);
            if (textFont == null) {
                textFont = "AvertaRegular";
            }
            Typeface typeface = TypefaceService.instance().getTypeface(textFont);
            if (typeface != null) {
                textPaint.setTypeface(typeface);
            }
        }

        a.recycle();
    }

    public void setUpdateListener(onViewUpdateListener onViewUpdateListener) {
        mListener = onViewUpdateListener;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;
        screenSize = height;
        pxmm = screenSize / 67.f;
        midScreenPoint = height / 2;
        if (isSizeChanged) {
            isSizeChanged = false;
            mainPoint = midScreenPoint - (userStartingPoint * 10 * pxmm);
        }
//        midLinePaint.setShader(new LinearGradient(0, 0, width, rulersize,
//                ContextCompat.getColor(getContext(), R.color.whiteTextColor),
//                ContextCompat.getColor(getContext(), R.color.whiteTextColor),
//                android.graphics.Shader.TileMode.MIRROR));
    }

    @Override
    public void onDraw(Canvas canvas) {

        RectF textBounds = drawCenterText(canvas, "Test");

        drawMidLine(canvas, textBounds);

        drawRulerLines(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mainPointClone = mainPoint;
        if (mainPoint < 0) {
            mainPointClone = -mainPoint;
        }
        float clickPoint = ((midScreenPoint + mainPointClone) / (pxmm * 10));
        if (mListener != null) {
            mListener.onViewUpdate((midScreenPoint + mainPointClone) / (pxmm * 10));
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = true;
                isDown = false;
                isUpward = false;
                downpoint = event.getY();
                downPointClone = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                movablePoint = event.getY();
                if (downPointClone > movablePoint) {
                    /**
                     * if user first starts moving downward and then upwards then
                     * this method makes it to move upward
                     */
                    if (isUpward) {
                        downpoint = event.getY();
                        downPointClone = downpoint;
                    }
                    isDown = true;
                    isUpward = false;
                    /**
                     * make this differnce of 1, otherwise it moves very fast and
                     * nothing shows clearly
                     */
                    if (downPointClone - movablePoint > 1) {
                        mainPoint = mainPoint + (-(downPointClone - movablePoint));
                        downPointClone = movablePoint;
                        invalidate();
                    }
                } else {
                    // downwards
                    if (isMove) {
                        /**
                         * if user first starts moving upward and then downwards,
                         * then this method makes it to move upward
                         */
                        if (isDown) {
                            downpoint = event.getY();
                            downPointClone = downpoint;
                        }
                        isDown = false;
                        isUpward = true;
                        if (movablePoint - downpoint > 1) {
                            mainPoint = mainPoint + ((movablePoint - downPointClone));
                            downPointClone = movablePoint;
                            if (mainPoint > 0) {
                                mainPoint = 0;
                                isMove = false;
                            }
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    public void setStartingPoint(float point) {
        userStartingPoint = point;
        isSizeChanged = true;
        if (isFirstTime) {
            isFirstTime = false;
            if (mListener != null) {
                mListener.onViewUpdate(point);
            }
        }
    }

    public void setRulerPointer(int value) {
        rulerPointer = value;
    }

    public interface onViewUpdateListener {
        void onViewUpdate(float value);
    }

    private RectF drawCenterText(Canvas canvas, String text) {
        //TODO draw "cm" small text
        //TODO draw inches text

        Rect clipBounds = canvas.getClipBounds();
        RectF bounds = new RectF();

        bounds.right = textPaint.measureText(text, 0 ,text.length());

        bounds.bottom = textPaint.descent() - textPaint.ascent();
        bounds.left += (clipBounds.width() - bounds.right) / 2f;
        bounds.top += (clipBounds.height() - bounds.bottom) / 2f;

        canvas.drawText(text, bounds.left, bounds.top - textPaint.ascent(), textPaint);

        return bounds;
    }

    private void drawMidLine(Canvas canvas, RectF textBounds) {
        Rect clipBounds = canvas.getClipBounds();
        RectF bounds = new RectF();
        
        bounds.left = textBounds.left + textBounds.right + DeviceUtilsKt.dpToPx(getContext(), 10);
        bounds.right = clipBounds.width();
        bounds.top = clipBounds.height() / 2f - 1f;
        bounds.bottom = bounds.top + 2f;

        canvas.drawRoundRect(bounds, 1, 1, midLinePaint);
    }

    private void drawRulerLines(Canvas canvas) {
        float top = mainPoint;

        int i = 1;
        while (top <= screenSize) {
            top = top + pxmm;

            int size = (i % 10 == 0) ? scaleLineLarge : (i % 5 == 0) ? scaleLineMedium : scaleLineSmall;

            float right = canvas.getWidth();
            canvas.drawLine(right - size, top, right, top, rulerPaint);

            //TODO draw text indicators
//            if (i % 10 == 0) {
//                canvas.drawText((i / 10) + " cm", endPoint - textStartPoint, startingPoint + 8, textPaint);
//                canvas.drawText("test", endPoint - textStartPoint, startingPoint + 8, textPaint);
//            }
            i++;
        }

    }
}

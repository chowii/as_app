package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by neal on 10/03/2016.
 */
public class SleepScoreView extends View {

    // The "grey" is white at 25% opacity.
    public RectF bounds = new RectF();
    public Paint fullPaint = new Paint();
    public Paint arcPaint = new Paint();

    private Float mSleepScore;

    public SleepScoreView(Context context) {
        super(context);
        init(null,0);
    }

    public SleepScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public SleepScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        fullPaint.setColor(Color.WHITE);
        fullPaint.setAlpha(63);
        fullPaint.setStyle(Paint.Style.STROKE);
        fullPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics()));

        arcPaint.setColor(Color.WHITE);
        arcPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getResources().getDisplayMetrics()));
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);


    }

    @Override
    protected void onDraw(Canvas canvas) {

        bounds.set(0,0,canvas.getWidth(),canvas.getHeight());
        bounds.inset(4,4);

        canvas.drawArc(bounds,0,360,false,fullPaint);

        if ( mSleepScore != null ) {
            canvas.drawArc(bounds,270f,360f/100f*mSleepScore,false,arcPaint);
        }

    }

    public void setSleepScore(Float sleepScore) {
        mSleepScore = sleepScore;
    }
}

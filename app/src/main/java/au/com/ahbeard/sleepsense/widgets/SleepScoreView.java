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

    public SleepScoreView(Context context) {
        super(context);
    }

    public SleepScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SleepScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // TODO: Create attributes for these.
        fullPaint.setColor(Color.WHITE);
        fullPaint.setAlpha(63);
        fullPaint.setStyle(Paint.Style.STROKE);
        fullPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics()));

        arcPaint.setColor(Color.WHITE);
        arcPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics()));
        arcPaint.setStyle(Paint.Style.STROKE);

        bounds.set(0,0,canvas.getWidth(),canvas.getHeight());
        bounds.inset(2,2);

        canvas.drawArc(bounds,0,360,false,fullPaint);
        canvas.drawArc(bounds,-90,270,false,arcPaint);
    }
}

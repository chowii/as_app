package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */

public class LiveFeedbackGraph extends View {

    private LinkedList<Integer> mLeftChannel = new LinkedList<>();
    private LinkedList<Integer> mRightChannel = new LinkedList<>();

    private Path mLeftPath = new Path();
    private Path mRightPath = new Path();

    private Paint mLinePaint;

    private int mMaximumPoints = 512;


    public LiveFeedbackGraph(Context context) {
        super(context);
        init(context,null,0);
    }

    public LiveFeedbackGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public LiveFeedbackGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mLinePaint.setStyle(Paint.Style.STROKE);

    }

    public void addToLeftChannel(int point) {

        mLeftChannel.add(point);

        // Trim the channel.
        while ( mLeftChannel.size() > mMaximumPoints ) {
            mLeftChannel.removeFirst();
        }

        createPath(mLeftPath,mLeftChannel,0.25f,0.20f,getWidth(),getHeight());

        invalidate();
    }

    public void addToRightChannel(int point) {

        mRightChannel.add(point);

        // Trim the channel.
        while ( mRightChannel.size() > mMaximumPoints ) {
            mRightChannel.removeFirst();
        }

        createPath(mRightPath,mRightChannel,0.75f,0.20f,getWidth(),getHeight());

        invalidate();
    }

    public void setMaximumPoints(int maximumPoints) {
        mMaximumPoints = maximumPoints;
    }

    private void createPath(Path path, List<Integer> data, float center, float extent, float width, float height) {

        path.reset();

        float yPerPoint = height / (float)mMaximumPoints;

        float yOffset = height - data.size() * yPerPoint;
        float xOffset = center * width;

        float maximum = 512;

        for (int i=0; i < data.size(); i++ ) {
            maximum = Math.max(maximum,Math.abs(data.get(i)));
        }

        float multiplier = maximum == 0 ? 0: extent * width / maximum;

        for (int i=0; i < data.size(); i++ ) {
            if ( i==0 ) {
                path.moveTo(xOffset+data.get(i)*multiplier,yOffset+yPerPoint*i);
            } else {
                path.lineTo(xOffset+data.get(i)*multiplier,yOffset+yPerPoint*i);
            }
        }

//        path.moveTo(center*width,0);
//        path.lineTo(center*width,height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createPath(mLeftPath,mLeftChannel,0.25f,0.20f,w,h);
        createPath(mRightPath,mRightChannel,0.75f,0.20f,w,h);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.drawPath(mLeftPath,mLinePaint);
        canvas.drawPath(mRightPath,mLinePaint);

    }
}

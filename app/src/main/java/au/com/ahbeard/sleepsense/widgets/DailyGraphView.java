package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.beddit.TimestampAndFloat;
import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * This is a graph divided into 2 areas. One is a scaled representation of the data from minimum value to maximum value
 * the other is an area for the graph labels (see the designs). The graph labels sit centered in that area and lined up
 * with the data point on the x axis.
 */
public class DailyGraphView extends ViewGroup {

    public static final float GRAPH_LEGEND_SPLIT = 0.9f;

    private Paint mLinePaint;
    private Paint mAreaPaint;
    private Paint mLabelPaint;
    private Paint mSideLabelPaint;

    private float mGraphExtent;
    private float mGraphRegionHeight;

    private Path mPath;
    private Path mAreaPath;

    private LinearGradient mAreaGradient;

    // Yes, I know I would probably have been better using sparse arrays here ;-)
    private List<TimestampAndFloat> mValues;
    private ArrayList<TimestampAndFloat> mNormalisedValues;

    private PointF[] mPoints;

    private boolean mGraphNeedsRelayout = true;
    private ArrayList<Legend> mLegends;


    public DailyGraphView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DailyGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DailyGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Yes we need to do something here!
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        setWillNotDraw(false);

        mAreaPaint = new Paint();
        mAreaPaint.setShader(mAreaGradient);
        mAreaPaint.setAlpha(190);
        mAreaPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mAreaPaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mAreaPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mLinePaint.setStyle(Paint.Style.STROKE);

        mLabelPaint = new Paint();
        mLabelPaint.setColor(Color.parseColor("#778CA2"));
        mLabelPaint.setStyle(Paint.Style.FILL);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        mLabelPaint.setTypeface(TypefaceService.instance().getTypeface("OpenSansRegular"));

        mSideLabelPaint = new Paint(mLabelPaint);
        mSideLabelPaint.setColor(Color.parseColor("#FFFFFF"));
        mSideLabelPaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mSideLabelPaint.setAlpha(180);

        mPath = new Path();
        mAreaPath = new Path();

    }

    // We actually need to think of this as a window on some data not
    public void setValues(List<TimestampAndFloat> values, float minimum, float maximum) {

        mNormalisedValues = new ArrayList<>(values);

        float range = maximum - minimum;

        // Normalise the values
        for (TimestampAndFloat timestampAndFloat : mNormalisedValues) {
            timestampAndFloat.setValue((timestampAndFloat.getValue() - minimum) / range);
        }

        removeAllViews();

        mGraphNeedsRelayout = true;

    }


    /*

        This is the meat of the code, where we get our individual line segments.
        For the moment, this will be left as the

     */
    private void layout(int width, int height) {

        // Have to allocate here, since we don't know the size. Optimally we could do this when the graph canvas
        // changes size... actually if the values don't change, none of this changes, so we can set a "dirty" flag.

        mAreaPaint.setShader(new LinearGradient(
                0, 0, 0, getHeight() * GRAPH_LEGEND_SPLIT,
                getResources().getColor(R.color.graphAreaGradientStart),
                getResources().getColor(R.color.graphAreaGradientEnd),
                Shader.TileMode.MIRROR));

        mGraphRegionHeight = height * GRAPH_LEGEND_SPLIT;
        mGraphExtent = height * GRAPH_LEGEND_SPLIT - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        mPath.reset();
        mAreaPath.reset();

        if (mNormalisedValues == null || mNormalisedValues.isEmpty()) {
            mPoints = new PointF[0];
            mGraphNeedsRelayout = false;
            return;
        }

        mPoints = new PointF[mNormalisedValues.size()];

        double startTime = mNormalisedValues.get(0).getTimestamp();
        double endTime = mNormalisedValues.get(mNormalisedValues.size() - 1).getTimestamp();
        double totalTimeUnits = endTime - startTime;
        double widthPerTimeUnit = getWidth() / totalTimeUnits;

        // Lay out the raw points.
        for (int i = 0; i < mNormalisedValues.size(); i++) {
            if (mNormalisedValues.get(i) != null) {
                mPoints[i] = new PointF();
                mPoints[i].x = (float) ((mNormalisedValues.get(i).getTimestamp() - startTime) * widthPerTimeUnit);
                mPoints[i].y = mGraphRegionHeight - mGraphExtent * mNormalisedValues.get(i).getValue();
                // Log.d("Points", "point[" + i + "]: " + mPoints[i]);
            }
        }

        mAreaPath.moveTo(0, getHeight());

        for (int i = 0; i < mPoints.length; i++) {
            if (i == 0) {
                mPath.moveTo(mPoints[i].x, mPoints[i].y);
            } else {
                if ( mNormalisedValues.get(i).getTimestamp() - mNormalisedValues.get(i-1).getTimestamp() > 180f ) {
                    mPath.moveTo(mPoints[i].x, mPoints[i].y);
                } else {
                    mPath.lineTo(mPoints[i].x, mPoints[i].y);
                }

            }
            mAreaPath.lineTo(mPoints[i].x, mPoints[i].y);
        }

        mAreaPath.lineTo(getWidth(), getHeight());
        mAreaPath.close();

        mLegends = new ArrayList<>();

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis((long) (startTime * 1000f));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis((long) (endTime * 1000f));

        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.add(Calendar.HOUR_OF_DAY, 1);

        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);

        while (startCalendar.get(Calendar.HOUR_OF_DAY) != endHour) {

            int hour = startCalendar.get(Calendar.HOUR);

            if (hour == 0) {
                hour = 12;
            }

            mLegends.add(new Legend(new PointF((float) ((startCalendar.getTimeInMillis() / 1000f - startTime) * widthPerTimeUnit), getHeight() - getPaddingBottom()),
                    String.format("%02d",hour)));

            startCalendar.add(Calendar.HOUR_OF_DAY, 1);

        }

        mLegends.add(new Legend(new PointF((float) ((endCalendar.getTimeInMillis() / 1000f - startTime) * widthPerTimeUnit), getHeight() - getPaddingBottom()),
                String.format("%02d",endCalendar.get(Calendar.HOUR))));

        mGraphNeedsRelayout = false;




    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mGraphNeedsRelayout) {
            layout(canvas.getWidth(), canvas.getHeight());
        }

        super.onDraw(canvas);

        canvas.drawPath(mAreaPath, mAreaPaint);
        canvas.drawPath(mPath, mLinePaint);

        if (mLegends != null) {

            canvas.clipPath(mAreaPath);

            for (Legend legend : mLegends) {

                float legendWidth = mLabelPaint.measureText(legend.value);

                canvas.drawText(legend.value, legend.center.x - legendWidth / 2f, legend.center.y - mLabelPaint.getFontMetrics().ascent/2, mLabelPaint);
                canvas.drawLine(legend.center.x,0,legend.center.x,legend.center.y-mLabelPaint.getTextSize()/2,mSideLabelPaint);
                canvas.drawLine(legend.center.x,legend.center.y+mLabelPaint.getFontMetrics().descent+mLabelPaint.getTextSize()/2,legend.center.x,canvas.getHeight(),mSideLabelPaint);

                Log.d("POINTS",String.format("%f,%f -> %f,%f",legend.center.x,legend.center.y,legend.center.x,(float)canvas.getHeight() ));
            }
        }

    }


    class Legend {
        PointF center;
        String value;

        public Legend(PointF center, String value) {
            this.center = center;
            this.value = value;
        }
    }
}

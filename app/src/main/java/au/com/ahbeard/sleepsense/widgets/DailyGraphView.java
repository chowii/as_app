package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.os.Build;
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

    private Paint mLinePaint;
    private Paint mAreaPaint;
    private Paint mLabelPaint;
    private Paint mSideLabelPaint;
    private Paint mDashedLinePaint;
    private Paint mLabelLinePaint;

    private float mGraphExtent;
    private float mGraphRegionHeight;
    private float mGraphRegionEnd;
    private float mGraphRegionStart;
    private float mGraphRegionWidth;

    private float mXAxisLabelSpace;
    private float mYAxisLabelSpace;

    private Path mPath;
    private Path mAreaPath;

    private LinearGradient mAreaGradient;

    // Yes, I know I would probably have been better using sparse arrays here ;-)
    private List<TimestampAndFloat> mValues;
    private ArrayList<TimestampAndFloat> mNormalisedValues;

    private PointF[] mPoints;

    private boolean mGraphNeedsRelayout = true;
    private ArrayList<Legend> mLegends;
    private Path mDashedLinePaths;
    private float mSideLabelPadding;


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

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Graph, defStyleAttr, 0);

        mXAxisLabelSpace = a.getDimension(R.styleable.Graph_xAxisLabelSpace, 0);
        mYAxisLabelSpace = a.getDimension(R.styleable.Graph_yAxisLabelSpace, 0);

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
        mLinePaint.setAntiAlias(true);

        mLabelLinePaint = new Paint();
        mLabelLinePaint.setAntiAlias(true);
        mLabelLinePaint.setColor(Color.WHITE);
        mLabelLinePaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mLabelLinePaint.setStyle(Paint.Style.STROKE);

        mLabelPaint = new Paint();
        mLabelPaint.setColor(Color.parseColor("#778CA2"));
        mLabelPaint.setStyle(Paint.Style.FILL);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        mLabelPaint.setTypeface(TypefaceService.instance().getTypeface("OpenSansRegular"));

        mSideLabelPaint = new Paint(mLabelPaint);
        mSideLabelPaint.setColor(Color.parseColor("#626C83"));
        mSideLabelPaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mSideLabelPaint.setAlpha(255);
        mSideLabelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSideLabelPaint.setLetterSpacing(0.1f);
        }

        mSideLabelPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());


        float dashWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());

        mDashedLinePaint = new Paint();
        mDashedLinePaint.setColor(Color.WHITE);
        mDashedLinePaint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashWidth * 2}, 0));
        mDashedLinePaint.setAlpha(128);
        mDashedLinePaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mDashedLinePaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mAreaPath = new Path();
        mDashedLinePaths = new Path();

    }

    // We actually need to think of this as a window on some data not
    public void setValues(List<TimestampAndFloat> values, float minimum, float maximum) {

        mNormalisedValues = new ArrayList<>(values);

        float range = maximum - minimum;

        // Normalise the values
        for (TimestampAndFloat timestampAndFloat : mNormalisedValues) {
            timestampAndFloat.setValue(((1.0f - timestampAndFloat.getValue()) - minimum) / range);
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

        mGraphRegionHeight = height - mXAxisLabelSpace;

        float labelWidth = mLabelPaint.measureText("AWAKE") + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        mGraphRegionWidth = width - labelWidth;
        mGraphRegionStart = labelWidth;
        mGraphRegionEnd = width;

        mGraphExtent = height - mXAxisLabelSpace - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        mPath.reset();
        mAreaPath.reset();

        mDashedLinePaths.reset();

        mDashedLinePaths.moveTo(0, mGraphRegionHeight - mGraphExtent * 0.0f);
        mDashedLinePaths.lineTo(mGraphRegionEnd, mGraphRegionHeight - mGraphExtent * 0.0f);
        mDashedLinePaths.moveTo(0, mGraphRegionHeight - mGraphExtent * 0.25f);
        mDashedLinePaths.lineTo(mGraphRegionEnd, mGraphRegionHeight - mGraphExtent * 0.25f);

        if (mNormalisedValues == null || mNormalisedValues.isEmpty()) {
            mPoints = new PointF[0];
            mGraphNeedsRelayout = false;
            return;
        }

        mPoints = new PointF[mNormalisedValues.size()];

        double startTime = mNormalisedValues.get(0).getTimestamp();
        double endTime = mNormalisedValues.get(mNormalisedValues.size() - 1).getTimestamp();
        double totalTimeUnits = endTime - startTime;
        double widthPerTimeUnit = mGraphRegionWidth / totalTimeUnits;

        float minY = Float.MAX_VALUE;

        // Lay out the raw points.
        for (int i = 0; i < mNormalisedValues.size(); i++) {
            if (mNormalisedValues.get(i) != null) {
                mPoints[i] = new PointF();
                mPoints[i].x = (float) ((mNormalisedValues.get(i).getTimestamp() - startTime) * widthPerTimeUnit) + mGraphRegionStart;
                mPoints[i].y = mGraphRegionHeight - mGraphExtent * mNormalisedValues.get(i).getValue();
                minY = Math.min(mPoints[i].y, minY);
            }
        }

        mPath.moveTo(0,mPoints[0].y);
        mAreaPath.moveTo(0, getHeight());
        mAreaPath.lineTo(0, mPoints[0].y);

        mAreaPaint.setShader(new LinearGradient(
                0, minY, 0, mGraphRegionHeight,
                getResources().getColor(R.color.graphAreaGradientStart),
                getResources().getColor(R.color.graphAreaGradientEnd),
                Shader.TileMode.CLAMP));


        for (int i = 0; i < mPoints.length; i++) {

            mPath.lineTo(mPoints[i].x, mPoints[i].y);
            mAreaPath.lineTo(mPoints[i].x, mPoints[i].y);
        }

        mAreaPath.lineTo(mGraphRegionEnd, getHeight());
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

            mLegends.add(new Legend(new PointF((float) ((startCalendar.getTimeInMillis() / 1000f - startTime) * widthPerTimeUnit) + mGraphRegionStart, getHeight() - mXAxisLabelSpace / 2),
                    String.format("%02d", hour)));

            startCalendar.add(Calendar.HOUR_OF_DAY, 1);

        }

        mLegends.add(new Legend(new PointF((float) ((endCalendar.getTimeInMillis() / 1000f - startTime) * widthPerTimeUnit) + mGraphRegionStart, getHeight() - mXAxisLabelSpace / 2),
                String.format("%02d", endCalendar.get(Calendar.HOUR))));


        mGraphNeedsRelayout = false;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mGraphNeedsRelayout) {
            layout(canvas.getWidth(), canvas.getHeight());
        }

        super.onDraw(canvas);

        if (mAreaPath != null && mPath != null) {
            canvas.drawPath(mAreaPath, mAreaPaint);
            canvas.drawPath(mPath, mLinePaint);

            canvas.drawPath(mDashedLinePaths, mDashedLinePaint);

            float deepSleepHeight = mGraphRegionHeight - mGraphExtent * 0.25f - mSideLabelPaint.getFontMetrics().ascent + mSideLabelPadding * 0.75f;
            canvas.drawText("DEEP", mSideLabelPadding, deepSleepHeight, mSideLabelPaint);
            canvas.drawText("SLEEP", mSideLabelPadding, deepSleepHeight + mSideLabelPaint.getTextSize() * 1.25f, mSideLabelPaint);

            float awakeSleepHeight = mGraphRegionHeight - mGraphExtent * 0.0f + mSideLabelPaint.getFontMetrics().descent - mSideLabelPadding;
            canvas.drawText("AWAKE", mSideLabelPadding, awakeSleepHeight, mSideLabelPaint);

            if (mLegends != null) {

                canvas.clipPath(mAreaPath);

                for (Legend legend : mLegends) {

                    float legendWidth = mLabelPaint.measureText(legend.value);

                    canvas.drawText(legend.value, legend.center.x - legendWidth / 2f, legend.center.y - mLabelPaint.getFontMetrics().ascent / 2, mLabelPaint);
                    // canvas.drawLine(legend.center.x, 0, legend.center.x, legend.center.y - mLabelPaint.getTextSize() / 2, mLabelLinePaint);
                    // canvas.drawLine(legend.center.x,legend.center.y+mLabelPaint.getFontMetrics().descent+mLabelPaint.getTextSize()/2,legend.center.x,canvas.getHeight(),mSideLabelPaint);
                }
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

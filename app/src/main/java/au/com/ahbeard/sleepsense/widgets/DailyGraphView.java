package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is a graph divided into 2 areas. One is a scaled representation of the data from minimum value to maximum value
 * the other is an area for the graph labels (see the designs). The graph labels sit centered in that area and lined up
 * with the data point on the x axis.
 */
public class DailyGraphView extends ViewGroup {

    public static final float GRAPH_LEGEND_SPLIT = 0.9f;

    private Paint mLinePaint;
    private Paint mLineShadowPaint;
    private Paint mDividerPaint;
    private Paint mAreaPaint;
    private Paint mLabelPaint;
    private Paint mSideLabelPaint;
    private float mTopPadding;

    private List<PointF> mDividerPoints;
    private List<Path> mAreaPaths;
    private Path mAreaPathsCombined;
    private List<RectF> mAreaPathBounds;
    private List<Boolean> mAreaPathStates;
    private List<Object> mAreaPathIndexes;

    private PointF mEventPoint = new PointF();
    private PointF mDownPoint = new PointF();

    private Path mPath;

    private LinearGradient mAreaGradient;

    // Yes, I know I would probably have been better using sparse arrays here ;-)
    private Float[] mValues;
    private Float[] mRawPoints;

    private PointF[] mPoints;

    private boolean mGraphNeedsRelayout = true;
    private float mColumnWidth;
    private float mGraphExtent;
    private float mGraphRegionHeight;
    private float mLegendHeight;

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
        setClickable(true);

        mDividerPoints = new ArrayList<>();

        mAreaPaths = new ArrayList<>();
        mAreaPathBounds = new ArrayList<>();
        mAreaPathStates = new ArrayList<>();
        mAreaPathIndexes = new ArrayList<>();

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
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        mLinePaint.setStyle(Paint.Style.STROKE);

        mLineShadowPaint = new Paint(mLinePaint);
        mLineShadowPaint.setColor(Color.GRAY);
        mLineShadowPaint.setAlpha(64);

        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mLabelPaint = new Paint();
        mLabelPaint.setColor(Color.WHITE);
        mLabelPaint.setStyle(Paint.Style.FILL);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        mLabelPaint.setTypeface(TypefaceService.instance().getTypeface("OpenSansRegular"));

        mSideLabelPaint = new Paint(mLabelPaint);
        mSideLabelPaint.setAlpha(140);

        mTopPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

        mPath = new Path();

    }

    public void setValues(float[] values, float minimum, float maximum) {

        Float[] _values = new Float[values.length];

        for (int i = 0; i < values.length; i++) {
            _values[i] = values[i] >= 0 ? values[i] : null;
        }

        setValues(_values, minimum, maximum);

    }

    // We actually need to think of this as a window on some data not
    public void setValues(Float[] values, float minimum, float maximum) {

        mValues = values;

        float range = maximum - minimum;

        // These represent an abstract 0.0 - 1.0 from the maximum to the minimum.
        mRawPoints = new Float[mValues.length];

        // Set up the existing point.
        for (int i = 0; i < mValues.length; i++) {
            if (mValues[i] != null) {
                mRawPoints[i] = (mValues[i] - minimum) / range;
            } else {
                // Set it to just under 0... hacky, but will be fixed.
                mRawPoints[i] = null;
            }
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
        mAreaPaths.clear();
        mAreaPathsCombined = new Path();
        mDividerPoints.clear();
        mAreaPathBounds.clear();
        mAreaPathStates.clear();
        mAreaPathIndexes.clear();


        mAreaPaint.setShader(new LinearGradient(
                0, 0, 0, getHeight() * GRAPH_LEGEND_SPLIT,
                getResources().getColor(R.color.graphAreaGradientStart),
                getResources().getColor(R.color.graphAreaGradientEnd),
                Shader.TileMode.MIRROR));

        if (mRawPoints == null) {
            mPoints = new PointF[0];
            return;
        }

        mColumnWidth = (float) width / (mRawPoints.length - 2);

        mGraphRegionHeight = height * GRAPH_LEGEND_SPLIT;
        mLegendHeight = height - mGraphRegionHeight;
        mGraphExtent = height * GRAPH_LEGEND_SPLIT - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        mPoints = new PointF[mRawPoints.length];

        mPath.reset();

        // Lay out the raw points.
        for (int i = 0; i < mRawPoints.length; i++) {
            if (mRawPoints[i] != null) {
                mPoints[i] = new PointF();
                mPoints[i].x = mColumnWidth * i - mColumnWidth / 2;
                mPoints[i].y = mGraphRegionHeight - mGraphExtent * mRawPoints[i];
            }
        }

        for (int i = 1; i < mPoints.length - 1; i++) {

            if (mPoints[i] == null)
                continue;

            boolean isFirstPoint = i == 1;
            boolean isLastPoint = i == mPoints.length - 2;

            // Find left neighbour index...
            Integer nearestLeftNeighbourIndex = null;

            for (int j = i - 1; j >= 0; j--) {
                if (mPoints[j] != null) {
                    nearestLeftNeighbourIndex = j;
                    break;
                }
            }

            Integer nearestRightNeighbourIndex = null;

            // Find right neighbour...
            for (int j = i + 1; j < mPoints.length; j++) {
                if (mPoints[j] != null) {
                    nearestRightNeighbourIndex = j;
                    break;
                }
            }

            // Build the left half of the path.
            PointF leftStartPoint = new PointF();

            leftStartPoint.x = mPoints[i].x - mColumnWidth / 2;

            if (nearestLeftNeighbourIndex == null) {
                leftStartPoint.y = mPoints[i].y;
            } else {
                float differenceInPixels = mPoints[nearestLeftNeighbourIndex].y - mPoints[i].y;
                int differenceInIndexes = i - nearestLeftNeighbourIndex;
                leftStartPoint.y = mPoints[i].y + (differenceInPixels / 2) / differenceInIndexes;
            }

            // Build the right half of the path
            PointF rightStartPoint = new PointF();

            // Round this, don't just convert it into an integer, otherwise we might lose a chunk on some devices.
            rightStartPoint.x = mPoints[i].x + mColumnWidth / 2;

            if (nearestRightNeighbourIndex == null) {
                rightStartPoint.y = mPoints[i].y;
            } else {
                float differenceInPixels = mPoints[nearestRightNeighbourIndex].y - mPoints[i].y;
                float differenceInIndexes = nearestRightNeighbourIndex - i;
                rightStartPoint.y = mPoints[i].y + (differenceInPixels / 2) / differenceInIndexes;
                if (differenceInIndexes < 1.5f) {
                    mDividerPoints.add(rightStartPoint);
                }

            }

            // Create the line segment.
            if (nearestLeftNeighbourIndex != null && nearestLeftNeighbourIndex == i - 1) {
                mPath.moveTo(leftStartPoint.x, leftStartPoint.y);
                mPath.lineTo(mPoints[i].x, mPoints[i].y);
            } else {
                mPath.moveTo(mPoints[i].x, mPoints[i].y);
            }

            if (nearestRightNeighbourIndex != null && nearestRightNeighbourIndex == i + 1) {
                mPath.lineTo(rightStartPoint.x, rightStartPoint.y);
            }

            // Create the area under the line segment.
            Path path = new Path();

            path.moveTo(leftStartPoint.x, leftStartPoint.y);
            path.lineTo(mPoints[i].x, mPoints[i].y);
            path.lineTo(rightStartPoint.x, rightStartPoint.y);
            path.lineTo(rightStartPoint.x, getHeight());
            path.lineTo(leftStartPoint.x, getHeight());

            path.close();

            mAreaPaths.add(path);
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            mAreaPathBounds.add(bounds);
            mAreaPathStates.add(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAreaPathsCombined.op(path, Path.Op.UNION);
            }

        }

        mGraphNeedsRelayout = false;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mGraphNeedsRelayout) {
            layout(canvas.getWidth(), canvas.getHeight());
        }

        if ( mAreaPathsCombined.isEmpty() ) {
            for (int i = 0; i < mAreaPaths.size(); i++) {
                canvas.drawPath(mAreaPaths.get(i), mAreaPaint);
                if (mAreaPathStates.get(i)) {
                    canvas.drawPath(mAreaPaths.get(i), mAreaPaint);
                }
            }

        } else {
            canvas.drawPath(mAreaPathsCombined,mAreaPaint);
            for (int i = 0; i < mAreaPaths.size(); i++) {
                if (mAreaPathStates.get(i)) {
                    canvas.drawPath(mAreaPaths.get(i), mAreaPaint);
                }
            }
        }

        super.onDraw(canvas);

        canvas.drawPath(mPath, mLinePaint);

        if (mPoints.length <= 14) {
            for (int i = 0; i < mPoints.length; i++) {
                if (mPoints[i] != null) {
                    canvas.drawCircle(mPoints[i].x, mPoints[i].y, 4, mLinePaint);
                }

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            for (int i = 0; i < mAreaPathBounds.size(); i++) {
                if (mAreaPathBounds.get(i).contains(event.getX(), event.getY())) {
                    mAreaPathStates.set(i, true);
                    mDownPoint.set(event.getX(), event.getY());
                    invalidate();
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            for (int i = 0; i < mAreaPathBounds.size(); i++) {
                if (mAreaPathStates.get(i)) {
                    mAreaPathStates.set(i, false);
                    // TODO: Fire event!
                    invalidate();
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            for (int i = 0; i < mAreaPathBounds.size(); i++) {
                if (mAreaPathStates.get(i)) {
                    mAreaPathStates.set(i, false);
                    invalidate();
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < mAreaPathBounds.size(); i++) {
                if (mAreaPathStates.get(i)) {
                    mEventPoint.set(event.getX(), event.getY());
                    // Check if we've either moved out of the bounds, or we've moved too much.
                    if (!mAreaPathBounds.get(i).contains(event.getX(), event.getY())) {
                        mAreaPathStates.set(i, false);
                        invalidate();
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     *
     */
    class LegendViewHolder {

        View mRoot;

        @Bind(R.id.graph_legend_text_view_value)
        TextView mValueTextView;

        @Bind(R.id.graph_legend_text_view_text)
        TextView mTextTextView;

        @Bind(R.id.graph_legend_image_view_link)
        ImageView mLinkImageView;

    }
}

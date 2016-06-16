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
import android.support.annotation.NonNull;
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
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is a graph divided into 2 areas. One is a scaled representation of the data from minimum value to maximum value
 * the other is an area for the graph labels (see the designs). The graph labels sit centered in that area and lined up
 * with the data point on the x axis.
 */
public class WeeklyGraphView extends ViewGroup {

    private Object[] mIndexes;
    private float mDataPointRadius;

    public interface OnClickListener {
        void onValueClicked(Object identifier);
    }

    public static final float GRAPH_LEGEND_SPLIT = 0.65f;

    private OnClickListener mOnClickListener;

    private Paint mLinePaint;
    private Paint mLineShadowPaint;
    private Paint mDividerPaint;
    private Paint mAreaPaint;

    private List<PointF> mDividerPoints;
    private List<Path> mAreaPaths;
    private List<RectF> mAreaPathBounds;
    private List<Boolean> mAreaPathStates;
    private List<Object> mAreaPathIndexes;

    private PointF mEventPoint = new PointF();
    private PointF mDownPoint = new PointF();


    private Path mPath;

    private LinearGradient mAreaGradient;

    // Yes, I know I would probably have been better using sparse arrays here ;-)
    private LegendViewHolder[] mLegendViewHolders;
    private Float[] mValues;
    private Float[] mRawPoints;

    private PointF[] mPoints;

    private boolean mGraphNeedsRelayout = true;
    private float mColumnWidth;
    private float mGraphExtent;
    private float mGraphRegionHeight;
    private float mLegendHeight;

    public WeeklyGraphView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WeeklyGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WeeklyGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mAreaPaint.setAlpha(255);
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

        mLineShadowPaint = new Paint(mLinePaint);
        mLineShadowPaint.setColor(Color.GRAY);
        mLineShadowPaint.setAlpha(64);

        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mDataPointRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.5f, getResources().getDisplayMetrics());

        mPath = new Path();


        // eef5fa
    }

    // We actually need to think of this as a window on some data not
    public void setValues(Float[] values, String[] names, Object[] indexes, float minimum, float maximum) {

        mValues = values;
        mIndexes = indexes;

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

        mLegendViewHolders = new LegendViewHolder[mValues.length];

        if (names != null) {
            for (int i = 1; i < mValues.length - 1; i++) {
                if (mValues[i] != null) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.graph_legend_weekly, this, false);
                    LegendViewHolder legendViewHolder = new LegendViewHolder();
                    ButterKnife.bind(legendViewHolder, view);
                    this.addView(view);
                    legendViewHolder.mRoot = view;
                    legendViewHolder.mTextTextView.setText(names[i]);
                    legendViewHolder.mValueTextView.setText(Integer.toString(mValues[i].intValue()));
                    mLegendViewHolders[i] = legendViewHolder;
                }
            }
        }

        mGraphNeedsRelayout = true;

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    private void layout(int width, int height) {

        // Have to allocate here, since we don't know the size. Optimally we could do this when the graph canvas
        // changes size... actually if the values don't change, none of this changes, so we can set a "dirty" flag.
        mAreaPaths.clear();
        mDividerPoints.clear();
        mAreaPathBounds.clear();
        mAreaPathStates.clear();
        mAreaPathIndexes.clear();

        if (mRawPoints == null) {
            return;
        }

        mAreaPaint.setShader(new LinearGradient(
                0, 0, 0, getHeight() * GRAPH_LEGEND_SPLIT,
                new int[]{
                        getResources().getColor(R.color.graphAreaGradientStart),
                        getResources().getColor(R.color.graphAreaGradientEnd)},
                new float[]{
                        0.0f,
                        1.0f},
                Shader.TileMode.MIRROR));

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

        // Line up the legend view holders.
        for (int i = 0; i < mLegendViewHolders.length; i++) {

            if (mLegendViewHolders[i] == null)
                continue;
            View child = mLegendViewHolders[i].mRoot;

            child.measure(
                    MeasureSpec.makeMeasureSpec((int) mColumnWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec((int) mGraphRegionHeight, MeasureSpec.AT_MOST));

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            child.layout(
                    (int) (mPoints[i].x - childWidth / 2),
                    (int) (mGraphRegionHeight + mLegendHeight / 2 - childHeight / 2),
                    (int) (mPoints[i].x + childWidth / 2),
                    (int) (mGraphRegionHeight + mLegendHeight / 2 + childHeight / 2));

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
            mAreaPathBounds.add(computeBounds(path));
            mAreaPathStates.add(false);
            mAreaPathIndexes.add(mIndexes[i]);

        }

        mGraphNeedsRelayout = false;

    }

    @NonNull
    private RectF computeBounds(Path path) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        return bounds;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mGraphNeedsRelayout) {
            layout(canvas.getWidth(), canvas.getHeight());
        }

        if (mRawPoints == null) {
            return;
        }

        for (int i = 0; i < mAreaPaths.size(); i++) {
            canvas.drawPath(mAreaPaths.get(i), mAreaPaint);
            if (mAreaPathStates.get(i)) {
                canvas.drawPath(mAreaPaths.get(i), mAreaPaint);
            }
        }

        super.onDraw(canvas);

        canvas.drawPath(mPath, mLinePaint);

        if (mPoints.length <= 14) {
            for (int i = 0; i < mPoints.length; i++) {
                if (mPoints[i] != null) {
                    canvas.drawCircle(mPoints[i].x, mPoints[i].y, mDataPointRadius, mLinePaint);
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
                    if (mOnClickListener != null) {
                        mOnClickListener.onValueClicked(mAreaPathIndexes.get(i));
                    }
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

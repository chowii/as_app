package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Super view which automatically scale its contents according to target width and height.
 *
 * @author kikyoung@ustwo.com
 * @see <a href="http://www.quadra-tec.net/~floppie/blag/2013/01/scalinglinearlayout-auto-scaling-layouts-in-android/">ScalingLinearLayout: Auto-Scaling Layouts in Android</a>
 */
public class ModeView extends FrameLayout {

    private static final String TAG = "ModeView";

    protected float mTargetWidth;
    protected float mTargetHeight;

    private float mScale = 1.0f;

    private boolean mLayoutComplete;

    public boolean requiresLayout() {
        return !mLayoutComplete;
    }

    private List<View> mPostponedAdds;

    public ModeView(Context context, AttributeSet attributes) {
        super(context, attributes);

        // Basically this display is a preview of something meant to fill the screen.
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        mTargetWidth = 480 * 2;// metrics.widthPixels;
        mTargetHeight = 860 * 2;//metrics.heightPixels;

        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {

                if (mLayoutComplete) {
                    // Don't scale if we haven't been laid out, otherwise the scaling factor will be 0.0f
                    scaleViewAndChildren(child, mScale, true);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        //Log.d(TAG, String.format("onLayoutCalled: %s, %d, %d, %d, %d", Boolean.toString(changed), left, top, right,
        //        bottom));

        if (changed) {

            // Calculate new scale (it is initially set at 1.0f, i.e. not scaled).
            int width = getWidth();
            int height = getHeight();

            // Figure out if we need to scale the layout, we may need to scale if we haven't scaled it before.
            if (width > 0 && height > 0) {
                // Figure out the x-scaling.
                float xScale = (float) width / mTargetWidth;
                // Figure out the y-scaling.
                float yScale = (float) height / mTargetHeight;


                // Scale the layout.
                float oldScale = mScale;
                mScale = Math.max(xScale, yScale);
                mScale = 1.0f;
//                Log.d("DEBUG","mScale: "+mScale);
                scaleViewAndChildren(this, mScale / oldScale, false);
            }

        }

        super.onLayout(changed, left, top, right, bottom);

        this.setVisibility(VISIBLE);

        mLayoutComplete = true;

    }

    public void scaleViewAndChildren(View root) {
        scaleViewAndChildren(root, mScale, true);
    }

    /**
     * Scale the given view, its contents and all of its children by the given scale factor.
     *
     * @param root             View to scale.
     * @param scale            Scale factor to apply
     * @param isResizeRootSize Represent whether need to scale root width and height or not.
     */
    public void scaleViewAndChildren(View root, float scale, boolean isResizeRootSize) {

        // Log.d(TAG, "scaleAndViewChildren called: " + root + "   " + root.getTag() + " scale: " + scale);

        // Retrieve the view's layout information.
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();

        if (!"scaled".equals(root.getTag()) && isResizeRootSize) {
            // Scale the view itself
            // If view width and height for MATCH_PARENT and WRAP_CONTENT are not resized.
            if (layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.width *= scale;
            }
            if (layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height *= scale;
            }

        }

        // Only scale all this once.

        // If the View has margins, scale those too.
        if (!"scaled".equals(root.getTag()) && layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginParams.leftMargin *= scale;
            marginParams.topMargin *= scale;
            marginParams.rightMargin *= scale;
            marginParams.bottomMargin *= scale;
        }

        root.setLayoutParams(layoutParams);

        // Same treatment for padding.
        if (!"scaled".equals(root.getTag())) {
            root.setPadding(
                    (int) (root.getPaddingLeft() * scale),
                    (int) (root.getPaddingTop() * scale),
                    (int) (root.getPaddingRight() * scale),
                    (int) (root.getPaddingBottom() * scale));
        }

        // If TextView (Button, EditText and etc), scale the font size.
        if (root instanceof TextView) {
            TextView tv = (TextView) root;
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() * scale);
        }

//        if (root instanceof GridView) {
//            GridView gv = (GridView) root;
//            int scaledSpacing = (int) (scale * getResources().getDimensionPixelSize(R.dimen.feedback_grid_spacing));
//            gv.setHorizontalSpacing(scaledSpacing);
//            gv.setVerticalSpacing(scaledSpacing);
//
//        }

        root.setTag("scaled");

        // If the root is a view group, scale its children.
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scaleViewAndChildren(vg.getChildAt(i), scale, true);
            }
        }
    }


}

package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by neal on 30/03/15.
 */
public class ScaleToWidthImageView extends ImageView {

    public ScaleToWidthImageView(Context context) {
        super(context);
    }

    public ScaleToWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleToWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
        setScaleType(ScaleType.FIT_XY);
    }

}
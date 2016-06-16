package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * Created by neal on 13/05/2016.
 */
public class LabelThingy extends View implements ViewPager.OnPageChangeListener {


    public interface LabelProvider {
        public String getLabel(int position);
    }

    private LabelProvider mLabelProvider;

    private Paint mLabelPaint;
    private Paint mSideLabelPaint;

    private float mPositionOffset;

    private String mLeftHeading;
    private String mCentreHeading;
    private String mRightHeading;

    public LabelThingy(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LabelThingy(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LabelThingy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        setWillNotDraw(false);
        setClickable(true);

        mLabelPaint = new Paint();
        mLabelPaint.setColor(Color.WHITE);
        mLabelPaint.setStyle(Paint.Style.FILL);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        mLabelPaint.setTypeface(TypefaceService.instance().getTypeface("OpenSansRegular"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLabelPaint.setLetterSpacing(0.08f);
        }

        mSideLabelPaint = new Paint(mLabelPaint);
        mSideLabelPaint.setAlpha(140);

    }

    public void setLabelProvider(LabelProvider labelProvider) {
        mLabelProvider = labelProvider;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if ( mLabelProvider!=null) {
            for ( int i=-2; i <= 2; i++ ) {

                String label =mLabelProvider.getLabel(mPosition+i);
                float labelWidth = mLabelPaint.measureText(label);

                if ( i == 0 ) {
                    mLabelPaint.setAlpha((int)(255-128*mPositionOffset));
                } else if ( i== 1 ) {
                    mLabelPaint.setAlpha((int)(128+128*mPositionOffset));
                } else {
                    mLabelPaint.setAlpha(128);
                }

                int x = getWidth()/2*(i+1);
                canvas.drawText( label, x -labelWidth / 2f - mPositionOffset/2f*getWidth(),
                        getHeight()/2 - mLabelPaint.getFontMetrics().ascent, mLabelPaint);

            }
        }


    }

    private int mPosition;


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        mPosition = position;
        mPositionOffset = positionOffset;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        invalidate();
    }
}

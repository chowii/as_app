package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by neal on 10/03/2016.
 */
public class SleepSenseGraphView extends View {

    public SleepSenseGraphView(Context context) {
        super(context);
    }

    public SleepSenseGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SleepSenseGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        for ( int i=0; i < 10; i++ ) {

        }

    }
}

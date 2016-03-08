package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.TypefaceService;

/**
 * Created by neal on 3/03/2016.
 */
public class StyledTextView extends TextView {

    public StyledTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StyledTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StyledTextView, defStyleAttr, 0);

        String typefaceName = a.getString(R.styleable.StyledTextView_typeface);

        if (typefaceName != null) {
            Typeface typeface;

            if (isInEditMode()) {
                typeface = TypefaceService.instance(getContext()).getTypeface(typefaceName);
            } else {
                typeface = TypefaceService.instance().getTypeface(typefaceName);
            }

            if (typeface != null) {
                setTypeface(typeface);
            }
        }

        a.recycle();
    }


}

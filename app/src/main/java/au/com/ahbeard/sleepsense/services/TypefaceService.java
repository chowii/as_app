package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.ahbeard.sleepsense.widgets.TypefaceSpan;

/**
 * Created by neal on 16/01/2014.
 */
public class TypefaceService {

    private static final String TAG = "TypefaceUtils";

    private static TypefaceService sTypefaceService;

    private Context mContext;
    private Map<String, Typeface> mTypefaces;

    private android.graphics.Typeface mDefaultTypeface;

    private TypefaceService(Context context) {

        mContext = context.getApplicationContext();

        mTypefaces = new HashMap<>();

        try {


            AssetManager assets = mContext.getAssets();

            if (assets != null) {
                String[] typefaceFiles = assets.list("fonts");

                if (typefaceFiles != null) {

                    for (String typefaceFile : typefaceFiles) {
                        Typeface typeface = Typeface.createFromAsset(assets,
                                "fonts/" + typefaceFile);
                        mTypefaces.put(
                                typefaceFile.replaceAll("[\\s\\-]", "").replaceAll("\\.otf", "").replaceAll("\\.ttf",
                                        "").toUpperCase(), typeface);
                    }

                    mDefaultTypeface = mTypefaces.get("OpenSansRegular".toUpperCase());
                }
            }

            if (mDefaultTypeface == null) {
                mDefaultTypeface = Typeface.DEFAULT;
            }


        } catch (IOException e) {
            Log.e(TAG, "Exception initialising list of typefaces...", e);
        }


    }

    public static void initialize(Context context) {
        if (sTypefaceService == null) {
            sTypefaceService = new TypefaceService(context);
        }
    }

    public static TypefaceService instance() {
        return sTypefaceService;
    }

    public static TypefaceService instance(Context context) {
        if (sTypefaceService == null) {
            sTypefaceService = new TypefaceService(context);
        }
        return sTypefaceService;
    }

    public Typeface getTypeface(String typeface) {
        return mTypefaces.get(typeface);
    }

    public SpannableString createSpannableStringWithTypeface(String string, String typefaceId) {
        Typeface typeface = mTypefaces.get(typefaceId);

        SpannableString spannableString = new SpannableString(string);

        if (typeface != null) {
            spannableString.setSpan(new TypefaceSpan(mContext, typeface),
                    0, spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }


    public SpannableString createSpannableStringWithTypeface(int resourceId, String typefaceId) {
        return createSpannableStringWithTypeface(mContext.getString(resourceId), typefaceId);
    }

}

package by.vshkl.translate2.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DimensionUtils {

    public static int dp2px(DisplayMetrics displayMetrics, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}

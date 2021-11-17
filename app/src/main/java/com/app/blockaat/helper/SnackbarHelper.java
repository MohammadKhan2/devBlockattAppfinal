package com.app.blockaat.helper;

import android.content.Context;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.androidadvance.topsnackbar.TSnackbar;
import com.app.blockaat.R;

public class SnackbarHelper {

    public static void configSnackbar(Context context, TSnackbar snack) {
        addMargins(snack);
        setRoundBordersBg(context, snack);
        ViewCompat.setElevation(snack.getView(), 6f);
    }

    private static void addMargins(TSnackbar snack) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snack.getView().getLayoutParams();
        snack.getView().setLayoutParams(params);
    }

    private static void setRoundBordersBg(Context context, TSnackbar snackbar) {
        snackbar.getView().setBackground(ContextCompat.getDrawable(context, R.drawable.snackbar_bg));

    }

}

package com.app.blockaat.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.LinearLayout;


public class SimpleViewAnimator extends LinearLayout {
    private Animation inAnimation;
    private Animation outAnimation;

    public SimpleViewAnimator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    @Override
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            if (visibility == VISIBLE) {
                if (inAnimation != null) startAnimation(inAnimation);
            } else if ((visibility == INVISIBLE) || (visibility == GONE)) {
                if (outAnimation != null) startAnimation(outAnimation);
            }
        }

        super.setVisibility(visibility);
    }
}

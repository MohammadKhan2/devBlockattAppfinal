package com.app.saveco.helper

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout


class SimpleViewAnimator(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var inAnimation: Animation? = null
    private var outAnimation: Animation? = null

    fun setInAnimation(inAnimation: Animation) {
        this.inAnimation = inAnimation
    }

    fun setOutAnimation(outAnimation: Animation) {
        this.outAnimation = outAnimation
    }

    override fun setVisibility(visibility: Int) {
        if (getVisibility() != visibility) {
            if (visibility == View.VISIBLE) {
                if (inAnimation != null) startAnimation(inAnimation)
            } else if (visibility == View.INVISIBLE || visibility == View.GONE) {
                if (outAnimation != null) startAnimation(outAnimation)
            }
        }

        super.setVisibility(visibility)
    }
}

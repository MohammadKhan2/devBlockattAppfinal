package com.app.blockaat.helper

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.BaselineLayout
import com.app.blockaat.R

class ExtendedBottomNavigationView(context: Context, attrs: AttributeSet) :
    BottomNavigationView(context, attrs) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val bottomMenu = getChildAt(0) as ViewGroup
        val bottomMenuChildCount = bottomMenu.childCount
        var item: BottomNavigationItemView
        var itemTitle: View

        for (i in 0 until bottomMenuChildCount) {
            item = bottomMenu.getChildAt(i) as BottomNavigationItemView
            //every BottomNavigationItemView has two children, first is an itemIcon and second is an itemTitle
            itemTitle = item.getChildAt(1)
            //every itemTitle has two children, first is a smallLabel and second is a largeLabel. these two are type of AppCompatTextView

            val unSelectedText = ((itemTitle as BaselineLayout).getChildAt(0) as TextView)
            val selectedText = (itemTitle.getChildAt(1) as TextView)
            unSelectedText.typeface = Global.fontRegular
            selectedText.typeface =
                if (Global.isEnglishLanguage(context as Activity)) Global.fontRegular else Global.fontMedium
            selectedText.setPadding(0, 2, 0, 2)
            unSelectedText.setPadding(0, 2, 0, 2)
//            unSelectedText.setPadding(0, 0, 0, 0)
        }
    }
}
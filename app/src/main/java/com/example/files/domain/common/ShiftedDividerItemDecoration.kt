package com.example.files.domain.common

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt


class ShiftedDividerItemDecoration(context: Context, orientation: Int, private val offset: Int) :
    DividerItemDecoration(context, orientation) {
    private val mBounds = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()

        val leftWithMargin = (offset * Resources.getSystem().displayMetrics.density).toInt()
        val right = parent.width
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + ViewCompat.getTranslationY(child).roundToInt()
            val top = bottom - drawable?.intrinsicHeight!!
            drawable?.setBounds(leftWithMargin, top, right, bottom)
            drawable?.draw(canvas)
        }
        canvas.restore()
    }
}
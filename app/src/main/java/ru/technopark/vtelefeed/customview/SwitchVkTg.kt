package ru.technopark.vtelefeed.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import android.widget.EditText
import ru.technopark.vtelefeed.R


class SwitchVkTg(context: Context, attrs: AttributeSet?) : SwitchCompat (context, attrs) {
    init {
        this.setThumbResource(R.id.switch_thumb)
    }
}
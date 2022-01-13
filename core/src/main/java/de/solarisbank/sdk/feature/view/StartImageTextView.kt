package de.solarisbank.sdk.feature.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.annotation.DrawableRes
import de.solarisbank.sdk.core.R

import androidx.core.view.updateLayoutParams


class StartImageTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val imageView: ImageView
    val textView: TextView

    init {
        imageView = ImageView(context)
        textView = TextView(context, null, R.style.IdentHubTextView_Regular)
        addView(imageView)
        addView(textView)
        imageView.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        textView.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            marginStart = resources.getDimensionPixelSize(R.dimen.identhub_margin_8)
        }

        gravity = Gravity.TOP

        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        if (attrs == null) return

        val style = context.obtainStyledAttributes(
            attrs,
            R.styleable.IdentHubStartImageTextView,
            R.attr.IdentHubStartImageTextViewStyle,
            R.style.IdentHubDefaultStartImageTextViewStyle
        )

        val text = style.getString(R.styleable.IdentHubStartImageTextView_android_text) ?: ""
        textView.text = text
        imageView.setImageDrawable(style.getDrawable(R.styleable.IdentHubStartImageTextView_android_drawable))
        textView.updateLayoutParams<LayoutParams> {
            topMargin = style.getDimensionPixelSize(R.styleable.IdentHubStartImageTextView_textTopMargin, 0)
        }
        if (style.getBoolean(R.styleable.IdentHubStartImageTextView_hideIfTextIsEmpty, false) && text.isEmpty()) {
            visibility = GONE
        }
        style.recycle()
    }

    fun setText(charSequence: CharSequence?) {
        textView.text = charSequence
    }

    fun setImage(@DrawableRes drawableRes: Int) {
        imageView.setImageResource(drawableRes)
    }
}
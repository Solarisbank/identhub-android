package de.solarisbank.sdk.feature.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import de.solarisbank.sdk.core.R

class BulletListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
    }

    fun updateItems(title: CharSequence? = null,
                    items: List<CharSequence>,
                    titleStyle: TitleStyle = TitleStyle.SimpleBold) {
        removeAllViews()
        title?.let {
            addTitle(it, titleStyle)

        }
        items.forEach {
            addBulletItem(it)
        }
    }

    private fun addTitle(title: CharSequence, style: TitleStyle) {
        val titleView: TextView = when (style) {
            is TitleStyle.SimpleBold -> TextView(context, null, 0, R.style.IdentHubTextView_HeadLine3)
            is TitleStyle.Notice -> {
                TextView(context, null, 0, R.style.IdentHubTextView_Small_13).apply {
                    setCompoundDrawablesWithIntrinsicBounds(style.drawable, null, null, null)
                    compoundDrawablePadding = (8f * resources.displayMetrics.density).toInt()
                }
            }
        }

        titleView.text = title

        addView(titleView)
        titleView.updateLayoutParams<LayoutParams> {
            bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_12)
        }
    }

    private fun addBulletItem(title: CharSequence) {
        val layout = LinearLayout(context)
        layout.orientation = HORIZONTAL

        val bulletView = TextView(context, null, 0, R.style.IdentHubTextView_Regular)
        bulletView.text = "\u2022"
        bulletView.textAlignment = TEXT_ALIGNMENT_CENTER
        layout.addView(bulletView)
        bulletView.updateLayoutParams<LayoutParams> {
            val margin = resources.getDimensionPixelSize(R.dimen.margin_8)
            marginStart = margin
            marginEnd = margin
        }

        val titleView = TextView(context, null, 0, R.style.IdentHubTextView_Regular)
        titleView.text = title
        layout.addView(titleView)

        addView(layout)
    }

    sealed class TitleStyle {
        object SimpleBold: TitleStyle()
        class Notice(val drawable: Drawable?): TitleStyle()
    }
}
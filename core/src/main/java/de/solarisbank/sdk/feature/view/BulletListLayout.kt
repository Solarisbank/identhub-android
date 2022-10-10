package de.solarisbank.sdk.feature.view

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.feature.customization.customizeLinks

class BulletListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
        if (isInEditMode) {
            updateItems("Notice", listOf("Test bullet item"))
        }
    }

    fun updateItems(title: CharSequence? = null,
                    items: List<CharSequence>,
                    titleStyle: TitleStyle = TitleStyle.SimpleBold,
                    customization: Customization? = null) {
        removeAllViews()
        title?.let {
            addTitle(it, titleStyle)
        }
        items.forEach {
            addBulletItem(it, customization)
        }
    }

    private fun addTitle(title: CharSequence, style: TitleStyle) {
        val titleView: TextView = when (style) {
            is TitleStyle.SimpleBold -> TextView(context, null, 0, R.style.IdentHubTextView_HeadLine3)
            is TitleStyle.Notice -> {
                TextView(context, null, 0, R.style.IdentHubTextView_Small_13).apply {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.identhub_ic_info)
                    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    compoundDrawablePadding = (8f * resources.displayMetrics.density).toInt()
                }
            }
        }

        titleView.text = title

        addView(titleView)
        titleView.updateLayoutParams<LayoutParams> {
            bottomMargin = resources.getDimensionPixelSize(R.dimen.identhub_margin_12)
        }
    }

    private fun addBulletItem(title: CharSequence, customization: Customization?) {
        val layout = LinearLayout(context)
        layout.orientation = HORIZONTAL

        val bulletView = TextView(context, null, 0, R.style.IdentHubTextView_Regular)
        bulletView.text = "\u2022"
        bulletView.textAlignment = TEXT_ALIGNMENT_CENTER
        layout.addView(bulletView)
        bulletView.updateLayoutParams<LayoutParams> {
            val margin = resources.getDimensionPixelSize(R.dimen.identhub_margin_8)
            marginStart = margin
            marginEnd = margin
        }

        val titleView = TextView(context, null, 0, R.style.IdentHubTextView_Regular)
        titleView.text = title
        titleView.movementMethod = LinkMovementMethod.getInstance()
        customization?.let {
            titleView.customizeLinks(it)
        }
        layout.addView(titleView)

        addView(layout)
    }

    sealed class TitleStyle {
        object SimpleBold: TitleStyle()
        object Notice: TitleStyle()
    }
}
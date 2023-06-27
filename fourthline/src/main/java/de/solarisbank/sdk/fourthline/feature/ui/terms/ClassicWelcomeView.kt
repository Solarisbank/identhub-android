package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import de.solarisbank.sdk.fourthline.R

class ClassicWelcomeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
        inflate(context, R.layout.identhub_view_classic_welcome, this)
    }

}
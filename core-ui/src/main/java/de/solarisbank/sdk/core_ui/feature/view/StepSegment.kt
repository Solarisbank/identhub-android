package de.solarisbank.sdk.core_ui.feature.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

abstract class StepSegment @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    abstract fun setStepNumber(number: Int)

    abstract fun isPasssed(): Boolean

    abstract fun setPassed(isPassed: Boolean)

    /**
     * Makes constrainted by width segment line Gone
     * Used for StepIndicator parent ViewGroup positioning
     */
    abstract fun isFirstStep(isLastStep: Boolean)

    /**
     * Indicates in case of width wrap_content will not it be 0
     * Used for StepIndicator parent ViewGroup positioning
     */
    abstract fun isFirstStepWrappable(): Boolean

    /**
     * Returns required minimal segment width in dp.
     * Used for counting measurement in StepIndicator parent ViewGroup
     */
    abstract fun getConstaintInnerWidth(): Int

    /**
     * returns required minimal segment height in dp
     * Used for counting measurement in StepIndicator parent ViewGroup
     */
    abstract fun getConstaintInnerHeight(): Int
}
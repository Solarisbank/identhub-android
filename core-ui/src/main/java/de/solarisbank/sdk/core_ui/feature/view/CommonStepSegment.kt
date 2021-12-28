package de.solarisbank.sdk.core_ui.feature.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import de.solarisbank.sdk.core_ui.R
import timber.log.Timber


class CommonStepSegment @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : StepSegment(context, attrs) {

    init {
        initView()
    }

    private var step1Image: View? = null

    override fun setPassed(isPassed: Boolean) {
        step1Image!!.isEnabled = isPassed
    }

    override fun isPasssed(): Boolean {
        return step1Image!!.isEnabled
    }

    override fun isFirstStep(isLastStep: Boolean) {
        /* for common style number is not required*/
    }

    override fun isFirstStepWrappable(): Boolean {
        return false
    }

    override fun getConstaintInnerWidth(): Int {
        return 0
    }

    override fun getConstaintInnerHeight(): Int {
        return 4
    }

    override fun setBackgroundColor(color: Int) {
        Timber.d("aChub setBackgroundColor")
        step1Image!!.setBackgroundColor(color)
    }

    private fun initView() {
        id = View.generateViewId()
        val wishedChildHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
        step1Image = ImageView(context).apply {
            setId(View.generateViewId())
            layoutParams = LayoutParams(0, wishedChildHeight.toInt())
            background = context.resources.getDrawable(R.drawable.common_step_segment_background_selector)
        }
        addView(step1Image, LayoutParams(0, wishedChildHeight.toInt()))
        layoutParams = LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_PARENT)
        deepForEach { isEnabled = false }
        defineViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = MeasureSpec.makeMeasureSpec(
            (getConstaintInnerHeight().dpToPixels(context)),
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, heightSpec)

    }

    override fun setStepNumber(number: Int) {
        /* for common style number is not required*/
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        defineViews()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun defineViews() {
        var set = ConstraintSet()
        set.clone(this)
        set.connect(step1Image!!.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(step1Image!!.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(step1Image!!.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.applyTo(this)
    }

}
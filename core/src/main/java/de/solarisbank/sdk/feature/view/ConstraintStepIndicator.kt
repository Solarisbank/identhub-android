package de.solarisbank.sdk.feature.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.feature.view.customization.isDarkMode
import timber.log.Timber

class ConstraintStepIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs) {


    private var stepsAmount: Int = 0
    private lateinit var segmentList: MutableList<StepSegment>
    private var outerBetweenSegmentMarginDp: Int = 0
    private var labelBottomMarginDp: Int = 0
    private var areLabelsVisible = false
    private lateinit var currentStepLabel: TextView
    private lateinit var nextStepLabel: TextView
    private var currentStepLabelTextSize: Float = 0f
    private var nextStepLabelTextSize: Float = 0f
    private var startEndSegmentParentMargin: Int = 0
    private var customStepPassedColor: Int? = null
    private var stepUnpassedColor: Int =
        if (!context.isDarkMode()) resources.getColor(R.color.identhub_color_black10)
        else resources.getColor(R.color.identhub_color_base75)

    private val labelsConstraintWrapper = ConstraintLayout(context)
        .apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            tag = "labelsWrapper"
            id = ViewCompat.generateViewId()
        }

    var currentStep = 0
        get() = field
        private set(value) { field = value }
    //todo refactor setter

    init {
        initInnerViews(attrs!!, defStyleAttr)
    }

    fun customize(customization: Customization?) {
        customStepPassedColor = customization?.colors?.themeSecondary(context)
        customStepPassedColor?.let { customStepPassedColor ->
            segmentList[0].setBackgroundColor(customStepPassedColor)
            segmentList.forEach() {
                if (it.isPasssed()) {
                    it.setBackgroundColor(customStepPassedColor)
                } else {
                    it.setBackgroundColor(stepUnpassedColor)
                }
            }
        }
    }

    //todo check if separate context required
    private fun initInnerViews(attrs: AttributeSet, defStyleAttr: Int) {
        //todo fix obtaining common style
        context
            .obtainStyledAttributes(
                attrs, R.styleable.IdentHubStepIndicator, defStyleAttr, R.style.IdentHubStepIndicatorStyleCommon
            )
            .also { style1 ->
                stepsAmount = style1.getString(R.styleable.IdentHubStepIndicator_stepsAmount)!!.toInt()
                outerBetweenSegmentMarginDp =
                    style1.getDimensionPixelSize(R.styleable.IdentHubStepIndicator_innerSegmentMargin, 0)

                labelBottomMarginDp = style1.getDimensionPixelSize(R.styleable.IdentHubStepIndicator_labelMargin,0)
                startEndSegmentParentMargin = style1.getDimensionPixelSize(R.styleable.IdentHubStepIndicator_startEndSegmentParentMargin,0)
                areLabelsVisible = style1.getBoolean(R.styleable.IdentHubStepIndicator_areLabelsVisible, false)

                val ta = context.obtainStyledAttributes(
                    style1.getResourceId(R.styleable.IdentHubStepIndicator_currentStepLabelStyle, 0),
                    intArrayOf(android.R.attr.textSize)
                )
                currentStepLabelTextSize =
                    ta.getDimension(R.styleable.TextAppearance_android_textSize, 0f)
                Timber.d("currentStepLabelTextSize : $currentStepLabelTextSize")
                ta.recycle()

                context.obtainStyledAttributes(
                    style1.getResourceId(R.styleable.IdentHubStepIndicator_nextStepLabelStyle, 0),
                    intArrayOf(android.R.attr.textSize)
                ).apply {
                    nextStepLabelTextSize = getDimension(R.styleable.TextAppearance_android_textSize, 0f)
                    Timber.d("nextStepLabelTextSize : $nextStepLabelTextSize")
                    recycle()
                }

                currentStepLabel = TextView(
                    context,
                    null,
                    style1.getResourceId(R.styleable.IdentHubStepIndicator_currentStepLabelStyle, 0),
                    R.attr.currentStepLabelStyle
                ).apply {
                    tag = "currentStepLabel"
                    id = ViewCompat.generateViewId()
                    setTypeface(typeface, Typeface.BOLD);
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, currentStepLabelTextSize)
                }

                nextStepLabel = TextView(
                    context
                ).apply {
                    tag = "nextStepLabel"
                    id = ViewCompat.generateViewId()
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, nextStepLabelTextSize)
                }

                Timber.d("initInnerViews, stepsAmount : $stepsAmount")
                style1.recycle()
            }
        addInnerViews()
    }

    fun setPassedStep(step: Int) {
        if (step <= segmentList.size && step > 0) {
            currentStep = step
            IntRange(0, step - 1).forEach { number ->
                segmentList[number].also { segment ->
                    segment.setPassed(true)
                    customStepPassedColor?.let { color -> segment.setBackgroundColor(color) }
                }

            }
            IntRange(step, segmentList.size - 1).forEach {
                segmentList[it].also { segment ->
                    segment.setPassed(false)
                    //todo check
                    customStepPassedColor?.let { segment.setBackgroundColor(stepUnpassedColor) }
                }

            }
        } else {
            Timber.w("step is not valid, step : $step, stepAmount: $stepsAmount")
        }
    }

    fun setCurrentStepLabel(text: String) {
        currentStepLabel.text = text
    }

    fun setCurrentStepLabelRes(@StringRes res: Int) {
        context?.getString(res)?.let(currentStepLabel::setText)
            ?: Timber.e("No context attached")
    }

    fun setNextStepLabel(text: String) {
        nextStepLabel.text = text
    }

    fun setNextStepLabelRes(@StringRes res: Int) {
        context?.getString(res)?.let(nextStepLabel::setText)
            ?: Timber.e("No context attached")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.d("onMeasure")
        val heightSpec = MeasureSpec.makeMeasureSpec(
            (segmentList[0].getConstaintInnerHeight()
                .dpToPixels(context) + labelBottomMarginDp + currentStepLabelTextSize.spToPx(context)),
            MeasureSpec.EXACTLY
        )

        makeConstraints(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        segmentsSet.applyTo(this)
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Timber.d("onLayout")
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun addInnerViews() {
        labelsConstraintWrapper.addView(currentStepLabel)
        labelsConstraintWrapper.addView(nextStepLabel)
        addView(labelsConstraintWrapper)
        if (!areLabelsVisible) {
            currentStepLabel.visibility = View.GONE
            nextStepLabel.visibility = View.GONE
        }
        segmentList = ArrayList(stepsAmount)
        IntRange(0, stepsAmount - 1).forEach { i ->
            CommonStepSegment(context).also {
                it.tag = "segment" + i
                it.id = ViewCompat.generateViewId()
                addView(it)
                segmentList.add(it)
            }
        }
    }

    private fun Number.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            (this).toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    val labelsConstraintSet = ConstraintSet()
    var segmentsSet = ConstraintSet()


    private fun makeLabelConstraints() {
        labelsConstraintSet.clone(labelsConstraintWrapper)
        labelsConstraintSet.connect(currentStepLabel.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        labelsConstraintSet.connect(currentStepLabel.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        labelsConstraintSet.constrainHeight(currentStepLabel.id, ConstraintSet.WRAP_CONTENT)
        labelsConstraintSet.constrainWidth(currentStepLabel.id, ConstraintSet.WRAP_CONTENT)
        labelsConstraintSet.connect(nextStepLabel.id, ConstraintSet.START, currentStepLabel.id, ConstraintSet.END)
        labelsConstraintSet.connect(nextStepLabel.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        labelsConstraintSet.connect(nextStepLabel.id, ConstraintSet.BASELINE, ConstraintSet.PARENT_ID, ConstraintSet.BASELINE)
        labelsConstraintSet.setHorizontalBias(nextStepLabel.id, 1f)
        labelsConstraintSet.constrainHeight(nextStepLabel.id, ConstraintSet.WRAP_CONTENT)
        labelsConstraintSet.constrainWidth(nextStepLabel.id, ConstraintSet.WRAP_CONTENT)
        labelsConstraintSet.applyTo(labelsConstraintWrapper)
    }

    private fun makeConstraints(parentWidth: Int, parentHeight: Int) {
        val halfInnerSegmentMarginDp = outerBetweenSegmentMarginDp / 2
        segmentsSet = ConstraintSet()
        segmentsSet.clone(this)
        segmentsSet.connect(labelsConstraintWrapper.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        makeLabelConstraints()

        for (i in 0 until stepsAmount) {
            val segment = segmentList[i]
            segment.setStepNumber(i+1)
            when (i) {
                0 -> {
                    segmentsSet.setMargin(segment.id, ConstraintSet.END, halfInnerSegmentMarginDp)
                    segmentsSet.connect(segment.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startEndSegmentParentMargin)
                    segmentsSet.connect(segment.id, ConstraintSet.END, segmentList[i+1].id, ConstraintSet.START)
                    segmentsSet.connect(segment.id, ConstraintSet.TOP, labelsConstraintWrapper.id, ConstraintSet.BOTTOM, labelBottomMarginDp.dpToPx())
                    segmentsSet.connect(segment.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

                    segment.isFirstStep(true)
                    segmentsSet.constrainHeight(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                    if (segment.isFirstStepWrappable()) {
                        segmentsSet.constrainWidth(segment.id, ConstraintSet.WRAP_CONTENT)
                    } else {
                        val wishedInnerSegmentMargin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            outerBetweenSegmentMarginDp.toFloat(),
                            resources.displayMetrics
                        )
                        val wishedSegmentWidth =
                            (parentWidth - ((stepsAmount - 1) * wishedInnerSegmentMargin)) / stepsAmount
                        segmentsSet.constrainWidth(segment.id, wishedSegmentWidth.toInt())
                    }
                }

                /* Defines last step */
                stepsAmount - 1 -> {
                    segmentsSet.setMargin(segment.id, ConstraintSet.START, halfInnerSegmentMarginDp)
                    segmentsSet.connect(segment.id, ConstraintSet.START, segmentList[i - 1].id, ConstraintSet.END)
                    segmentsSet.connect(segment.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, startEndSegmentParentMargin)
                    segmentsSet.connect(segment.id, ConstraintSet.TOP, labelsConstraintWrapper.id, ConstraintSet.BOTTOM, labelBottomMarginDp.dpToPx())
                    segmentsSet.connect(segment.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                    segmentsSet.constrainHeight(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                    segmentsSet.constrainWidth(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                }
                else -> {
                    segmentsSet.setMargin(segment.id, ConstraintSet.START, halfInnerSegmentMarginDp)
                    segmentsSet.setMargin(segment.id, ConstraintSet.END, halfInnerSegmentMarginDp)
                    segmentsSet.connect(segment.id, ConstraintSet.START, segmentList[i-1].id, ConstraintSet.END)
                    segmentsSet.connect(segment.id, ConstraintSet.END, segmentList[i+1].id, ConstraintSet.START)
                    segmentsSet.connect(segment.id, ConstraintSet.TOP, labelsConstraintWrapper.id, ConstraintSet.BOTTOM, labelBottomMarginDp.dpToPx())
                    segmentsSet.connect(segment.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                    segmentsSet.constrainHeight(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                    segmentsSet.constrainWidth(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                }
            }
        }
    }
}
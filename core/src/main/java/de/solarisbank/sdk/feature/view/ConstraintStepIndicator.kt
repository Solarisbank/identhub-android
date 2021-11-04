package de.solarisbank.sdk.feature.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.core_ui.feature.view.CommonStepSegment
import de.solarisbank.sdk.core_ui.feature.view.StepSegment
import de.solarisbank.sdk.core_ui.feature.view.dpToPixels
import de.solarisbank.sdk.core_ui.feature.view.spToPx
import timber.log.Timber
import kotlin.reflect.KClass

class ConstraintStepIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs) {


    private var stepsAmount: Int = 0
    private lateinit var segmentList: MutableList<StepSegment>
    private lateinit var propClass: KClass<out StepSegment>
    private var outerBetweenSegmentMarginDp: Int = 0
    private var labelBottomMarginDp: Int = 0
    private var areLabelsVisible = false
    private lateinit var currentStepLabel: TextView
    private lateinit var nextStepLabel: TextView
    private var currentStepLabelTextSize: Float = 0f
    private var nextStepLabelTextSize: Float = 0f
    private var startEndSegmentParentMargin: Int = 0

    private val barrier = Barrier(context).apply {
        tag = "barrier"
        id = ViewCompat.generateViewId()
    }

    var currentStep = 0
        get() = field
        private set(value) { field = value }
    //todo refactor setter

    init {
        initInnerViews(attrs!!, defStyleAttr)
    }

    //todo check if separate context required
    private fun initInnerViews(attrs: AttributeSet, defStyleAttr: Int) {
        //todo fix obtaining common style
        context
            .obtainStyledAttributes(
                attrs, R.styleable.StepIndicator, defStyleAttr, R.style.StepIndicatorStyleCommon
            )
            .also { style1 ->
                stepsAmount = style1.getString(R.styleable.StepIndicator_stepsAmount)!!.toInt()
                outerBetweenSegmentMarginDp =
                    style1.getDimensionPixelSize(R.styleable.StepIndicator_innerSegmentMargin, 0)

                labelBottomMarginDp = style1.getDimensionPixelSize(R.styleable.StepIndicator_labelMargin,0)
                startEndSegmentParentMargin = style1.getDimensionPixelSize(R.styleable.StepIndicator_startEndSegmentParentMargin,0)
                areLabelsVisible = style1.getBoolean(R.styleable.StepIndicator_areLabelsVisible, false)

                val ta = context.obtainStyledAttributes(
                    style1.getResourceId(R.styleable.StepIndicator_currentStepLabelStyle, 0),
                    intArrayOf(android.R.attr.textSize)
                )
                currentStepLabelTextSize =
                    ta.getDimension(R.styleable.TextAppearance_android_textSize, 0f)
                Timber.d("currentStepLabelTextSize : $currentStepLabelTextSize")
                ta.recycle()

                context.obtainStyledAttributes(
                    style1.getResourceId(R.styleable.StepIndicator_nextStepLabelStyle, 0),
                    intArrayOf(android.R.attr.textSize)
                ).apply {
                    nextStepLabelTextSize = getDimension(R.styleable.TextAppearance_android_textSize, 0f)
                    Timber.d("nextStepLabelTextSize : $nextStepLabelTextSize")
                    recycle()
                }

                currentStepLabel = TextView(
                    context,
                    null,
                    style1.getResourceId(R.styleable.StepIndicator_currentStepLabelStyle, 0),
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
            IntRange(0, step - 1).forEach { segmentList[it].setPassed(true) }
            IntRange(step, segmentList.size - 1).forEach { segmentList[it].setPassed(false) }
        } else {
            Timber.w("step is not valid, step : $step, stepAmount: $stepsAmount")
        }
    }

    fun setCurrentStepLabel(text: String) {
        currentStepLabel.text = text
    }

    fun setNextStepLabel(text: String) {
        nextStepLabel.text = text
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = MeasureSpec.makeMeasureSpec(
            (segmentList[0].getConstaintInnerHeight()
                .dpToPixels(context) + labelBottomMarginDp + currentStepLabelTextSize.spToPx(context)),
            MeasureSpec.EXACTLY
        )

        super.onMeasure(widthMeasureSpec, heightSpec)
        makeConstraints(MeasureSpec.getSize(widthMeasureSpec))
        set.applyTo(this)
    }

    private fun addInnerViews() {
        addView(currentStepLabel)
        addView(nextStepLabel)
        addView(barrier)
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

    var set = ConstraintSet()

    private fun makeConstraints(parentWidth: Int) {

        val halfInnerSegmentMarginDp = outerBetweenSegmentMarginDp / 2
        set = ConstraintSet()
        set.clone(this)

        //todo think about layout
        set.connect(currentStepLabel.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(currentStepLabel.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.constrainHeight(currentStepLabel.id, ConstraintSet.WRAP_CONTENT)
        set.constrainWidth(currentStepLabel.id, ConstraintSet.WRAP_CONTENT)
        set.connect(nextStepLabel.id, ConstraintSet.START, currentStepLabel.id, ConstraintSet.END)
        set.connect(nextStepLabel.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(nextStepLabel.id, ConstraintSet.BASELINE, ConstraintSet.PARENT_ID, ConstraintSet.BASELINE)
        set.setHorizontalBias(nextStepLabel.id, 1f)
        set.constrainHeight(nextStepLabel.id, ConstraintSet.WRAP_CONTENT)
        set.constrainWidth(nextStepLabel.id, ConstraintSet.WRAP_CONTENT)
        set.createBarrier(barrier.id, Barrier.BOTTOM, nextStepLabel.id, nextStepLabel.id)

        for (i in 0 until stepsAmount) {
            val segment = segmentList[i]
            segment.setStepNumber(i+1)
            when (i) {
                0 -> {
                    Timber.d("makeConstraints 1")
                    set.setMargin(segment.id, ConstraintSet.END, halfInnerSegmentMarginDp)
                    set.connect(segment.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startEndSegmentParentMargin)
                    set.connect(segment.id, ConstraintSet.END, segmentList[i+1].id, ConstraintSet.START)
                    set.connect(segment.id, ConstraintSet.TOP, barrier.id, ConstraintSet.BOTTOM, labelBottomMarginDp.dpToPx())
                    set.connect(segment.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

                    segment.isFirstStep(true)
                    set.constrainHeight(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                    if (segment.isFirstStepWrappable()) {
                        Timber.d("makeConstraints 2")
                        set.constrainWidth(segment.id, ConstraintSet.WRAP_CONTENT)
                    } else {
                        Timber.d("makeConstraints 3")
                        val wishedInnerSegmentMargin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            outerBetweenSegmentMarginDp.toFloat(),
                            resources.displayMetrics
                        )
                        val wishedSegmentWidth =
                            (parentWidth - ((stepsAmount - 1) * wishedInnerSegmentMargin)) / stepsAmount
                        set.constrainWidth(segment.id, wishedSegmentWidth.toInt())
                    }
                }

                /* Defines last step */
                stepsAmount - 1 -> {
                    Timber.d("makeConstraints 4")
                    set.setMargin(segment.id, ConstraintSet.START, halfInnerSegmentMarginDp)
                    set.connect(segment.id, ConstraintSet.START, segmentList[i - 1].id, ConstraintSet.END)
                    set.connect(segment.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, startEndSegmentParentMargin)
                    set.connect(segment.id, ConstraintSet.TOP, barrier.id, ConstraintSet.BOTTOM, labelBottomMarginDp.dpToPx())
                    set.connect(segment.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                    set.constrainHeight(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                    set.constrainWidth(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                }
                else -> {
                    Timber.d("makeConstraints 5")
                    set.setMargin(segment.id, ConstraintSet.START, halfInnerSegmentMarginDp)
                    set.setMargin(segment.id, ConstraintSet.END, halfInnerSegmentMarginDp)
                    set.connect(segment.id, ConstraintSet.START, segmentList[i-1].id, ConstraintSet.END)
                    set.connect(segment.id, ConstraintSet.END, segmentList[i+1].id, ConstraintSet.START)
                    set.connect(segment.id, ConstraintSet.TOP, barrier.id, ConstraintSet.BOTTOM, labelBottomMarginDp.dpToPx())
                    set.connect(segment.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                    set.constrainHeight(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                    set.constrainWidth(segment.id, ConstraintSet.MATCH_CONSTRAINT)
                }
            }
        }

    }
}
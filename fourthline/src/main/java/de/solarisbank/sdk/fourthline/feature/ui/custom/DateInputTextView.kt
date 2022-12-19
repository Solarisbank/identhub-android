package de.solarisbank.sdk.fourthline.feature.ui.custom

import android.app.DatePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import de.solarisbank.sdk.fourthline.R
import timber.log.Timber
import java.util.*

class DateInputTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setOnClickListener {
            showDialog()
        }
    }

    private var calendar: Calendar? = null

    private val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        calendar?.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        updateLabel()
    }

    fun setDate(date: Date) {
        calendar = createCalendar().apply {
            time = date
            resetToMidnight()
        }
        updateLabel()
    }

    fun getDate(): Date? {
        return calendar?.time
    }

    fun countIssueDate(expireDate: Date, subtractYears: Int = -10) {
        calendar = createCalendar().apply {
            time = expireDate
            resetToMidnight()
            add(Calendar.YEAR, subtractYears)
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    fun setSuggestedDateWithOffset(date: Date, offsetYears: Int) {
        // If text is not blank, user already selected a date and updated the calendar
        if (text.isNotBlank()) {
            return
        }

        calendar = createCalendar().apply {
            time = date
            resetToMidnight()
            add(Calendar.YEAR, offsetYears)
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun showDialog() {
        if (calendar == null) {
            calendar = createCalendar().apply { resetToMidnight() }
        }

        calendar?.let {
            val datePickerDialog = DatePickerDialog(
                context,
                R.style.IdentHubDatePickerStyle,
                dateListener,
                it.get(Calendar.YEAR),
                it.get(Calendar.MONTH),
                it.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setOnCancelListener {
                clearFocus()
            }
            datePickerDialog.setCanceledOnTouchOutside(false)
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.identhub_color_secondary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.identhub_color_secondary))
        }
    }

    private fun updateLabel() {
        Timber.d("updateLabel(): ${DateFormat.getDateFormat(context)}")
        calendar?.let { text = DateFormat.getDateFormat(context).format(it.time) }
                ?:run { text = "" }
    }

    private fun createCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    }
}

fun Calendar.resetToMidnight() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
}
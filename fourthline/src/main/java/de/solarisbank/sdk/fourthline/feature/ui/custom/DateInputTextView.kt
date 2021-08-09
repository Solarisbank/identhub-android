package de.solarisbank.sdk.fourthline.feature.ui.custom

import android.app.DatePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.parseDateFromString
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

    private val dateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        calendar?.set(Calendar.YEAR, year)
        calendar?.set(Calendar.MONTH, month)
        calendar?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
        clearFocus()
    }

    fun setDate(date: Date) {
        calendar = Calendar.getInstance().apply { time = date }
        updateLabel()
    }

    fun getDate(): Date? {
        return calendar?.time
    }

    fun countIssueDate(expireDate: Date, subtractYears: Int = -10) {
        calendar = Calendar.getInstance().apply {
            time = expireDate
            add(Calendar.YEAR, subtractYears)
        }
    }

    private fun showDialog() {
        if (text.toString().isNotBlank()) {
            Timber.d("showDialog(): ${DateFormat.getDateFormat(context)}")

            try {
                Timber.d("showDialog() 1")
                calendar = Calendar.getInstance()
                        .apply { time = DateFormat.getDateFormat(context).parse(text.toString()) }
                calendar?:let{
                    Timber.d("showDialog() 2")
                }


            } catch (e: Exception) {
                Timber.d("showDialog() 3")
                text.toString().parseDateFromString()?.let {
                    calendar = Calendar.getInstance().apply { time = it }
                    Timber.d("showDialog() 4")
                } ?: let {
                    Timber.d("showDialog() 5")
                    calendar = null
                }
            }

        } else if (text.toString().isBlank() && calendar == null) {
            calendar = Calendar.getInstance()
        }

        calendar?.let {
            val datePickerDialog = DatePickerDialog(
                context,
                R.style.IdentHubDatePickerStyle,
                dateListener,
                calendar!!.get(Calendar.YEAR),
                calendar!!.get(Calendar.MONTH),
                calendar!!.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setOnCancelListener {
                clearFocus()
            }
            datePickerDialog.setCanceledOnTouchOutside(false)
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.ident_hub_color_secondary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.ident_hub_color_secondary))
        }
    }

    private fun updateLabel() {
        Timber.d("updateLabel(): ${DateFormat.getDateFormat(context).toString()}")
        calendar?.let { text = DateFormat.getDateFormat(context).format(it.time) }
                ?:run { text = "" }
    }

}
package de.solarisbank.sdk.fourthline.feature.ui.custom

import android.app.DatePickerDialog
import android.content.Context
import android.text.InputType
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.appcompat.widget.AppCompatEditText
import de.solarisbank.sdk.fourthline.parseDateFromString
import timber.log.Timber
import java.util.*

class DateInputEditText : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        Timber.d("init 1")
        setOnTouchListener()
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus -> if (hasFocus) { showDialog() } }
        Timber.d("init 2")
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
//        updateLabel()
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

        } else if (text.toString().isNullOrBlank() && calendar == null) {
            calendar = Calendar.getInstance()
        }

        calendar?.let {
            DatePickerDialog(
                    context,
                    dateListener,
                    calendar!!.get(Calendar.YEAR),
                    calendar!!.get(Calendar.MONTH),
                    calendar!!.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setOnTouchListener() {

        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val inType = inputType; // backup the input type
                inputType = InputType.TYPE_NULL; // disable soft input
                onTouchEvent(event); // call native handler
                inputType = inType; // restore input type
                return true
            }
        })

    }

    private fun updateLabel() {
        Timber.d("updateLabel(): ${DateFormat.getDateFormat(context).toString()}")
        calendar?.let { setText(DateFormat.getDateFormat(context).format(it.time)) }
                ?:run { setText("") }
    }

}
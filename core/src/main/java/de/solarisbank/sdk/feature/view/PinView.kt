package de.solarisbank.sdk.feature.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import de.solarisbank.identhub.R
import kotlin.math.max

class PinView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs),
    TextWatcher, View.OnFocusChangeListener, View.OnKeyListener {

    private var mPinLength = 6
    private val editTextList: MutableList<EditText> = ArrayList()
    private var mPinWidth:Float = 32F
    private var mTextSize = 16
    private var mPinHeight:Float = 48F
    private var mSplitWidth = 15
    private var mCursorVisible = false
    private var mDelPressed = false

    private var pinBackground = R.drawable.identhub_pinview_background
    private var finalNumberPin = false
    private var mListener: PinViewEventListener? = null
    private var fromSetValue = false

    interface PinViewEventListener {
        fun onDataEntered(pinView: PinView?, fromUser: Boolean)
    }

    private var mClickListener: OnClickListener? = null
    private var currentFocus: View? = null
    private var filters = arrayOfNulls<InputFilter>(1)
    private var params: LayoutParams? = null

    private val Number.toPx get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)

    private val indexOfCurrentFocus: Int
        get() = editTextList.indexOf(currentFocus)

    init {
        removeAllViews()
        setWillNotDraw(false)
        params = LayoutParams(mPinWidth.toPx.toInt(), mPinHeight.toPx.toInt())
        orientation = HORIZONTAL
        createEditTexts()
        super.setOnClickListener {
            var focused = false
            for (editText in editTextList) {
                if (editText.length() == 0) {
                    editText.requestFocus()
                    focused = true
                    break
                }
            }
            if (!focused && editTextList.size > 0) {
                editTextList[editTextList.size - 1].requestFocus()
            }
            if (mClickListener != null) {
                mClickListener!!.onClick(this@PinView)
            }
        }
    }

    private fun createEditTexts() {
        removeAllViews()
        editTextList.clear()
        var editText: EditText

        for (i in 0 until mPinLength) {
            editText = EditText(context)
            editText.textSize = mTextSize.toFloat()
            editTextList.add(i, editText)
            this.addView(editText)
            generateOneEditText(editText, "" + i)
        }

        editTextList[mPinLength - 1].filters = arrayOf(filters[0], LastInputFilter())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun generateOneEditText(styleEditText: EditText, tag: String) {
        params!!.setMargins(mSplitWidth, mSplitWidth, mSplitWidth, mSplitWidth)

        filters[0] = InputFilter.LengthFilter(mPinLength)

        styleEditText.filters = filters
        styleEditText.layoutParams = params
        styleEditText.gravity = Gravity.CENTER
        styleEditText.isCursorVisible = true

        if (!mCursorVisible) {
            styleEditText.isClickable = false
            styleEditText.setOnTouchListener { _, _ ->
                mDelPressed = false
                false
            }
        }
        styleEditText.apply {
            setBackgroundResource(pinBackground)
            setPadding(0, 0, 0, 0)
            this.tag = tag
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            addTextChangedListener(this@PinView)
            onFocusChangeListener = this@PinView
            setOnKeyListener(this@PinView)
        }
    }

    var value: String
        get() {
            val sb = StringBuilder()
            for (et in editTextList) {
                sb.append(et.text.toString())
            }
            return sb.toString()
        }
        set(value) {
            val regex = Regex("[0-9]*")
            fromSetValue = true

            if (!value.matches(regex) || editTextList.isEmpty()) {
                return
            }

            var lastTagHavingValue = -1
            for (i in editTextList.indices) {
                if (value.length > i) {
                    lastTagHavingValue = i
                    editTextList[i].setText(value[i].toString())
                } else {
                    editTextList[i].setText("")
                }
            }
            if (mPinLength > 0) {
                currentFocus = editTextList[mPinLength - 1]
                if (lastTagHavingValue == mPinLength - 1) {
                    currentFocus = editTextList[mPinLength - 1]
                    this.finalNumberPin = true
                    this.mListener?.onDataEntered(this, false)
                }
                currentFocus?.requestFocus()
            }
            fromSetValue = false
            updateEnabledState()
        }

    override fun onFocusChange(view: View, isFocused: Boolean) {
        if (isFocused && !mCursorVisible) {
            if (mDelPressed) {
                currentFocus = view
                mDelPressed = false
                return
            }
            for (editText in editTextList) {
                if (editText.length() == 0) {
                    if (editText !== view) {
                        editText.requestFocus()
                    } else {
                        currentFocus = view
                    }
                    return
                }
            }
            if (editTextList[editTextList.size - 1] !== view) {
                editTextList[editTextList.size - 1].requestFocus()
            } else {
                currentFocus = view
            }
        } else if (isFocused) {
            currentFocus = view
        } else {
            view.clearFocus()
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, start: Int, i1: Int, count: Int) {
        if (charSequence.length == 6 && !fromSetValue) {
            value = charSequence.toString()
            this.mListener?.onDataEntered(this, true)
            return
        }

        if (charSequence.length == 1 && currentFocus != null) {
            val currentTag = indexOfCurrentFocus
            if (currentTag < mPinLength - 1) {
                val delay: Long = 1
                postDelayed({
                    val nextEditText = editTextList[currentTag + 1]
                    nextEditText.isEnabled = true
                    nextEditText.requestFocus()
                }, delay)
            }

            if (currentTag == mPinLength - 1) {
                finalNumberPin = true
            }
        } else if (charSequence.isEmpty()) {
            if(indexOfCurrentFocus < 0){
                return
            }
            val currentTag = indexOfCurrentFocus
            this.mDelPressed = true

            if (!this.editTextList[currentTag].text.isNullOrEmpty()) {
                this.editTextList[currentTag].setText("")
            }
        }

        this.editTextList.forEach { item ->
            if (item.text.isNotEmpty()) {
                val index = this.editTextList.indexOf(item) + 1
                if (!this.fromSetValue && index == mPinLength) {
                    this.mListener?.onDataEntered(this, true)
                }
            }
        }

        updateEnabledState()
    }

    private fun updateEnabledState() {
        val currentTag = max(0, indexOfCurrentFocus)

        for (index in editTextList.indices) {
            val editText = editTextList[index]
            editText.isEnabled = index <= currentTag
        }
    }

    override fun afterTextChanged(editable: Editable) {}

    override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
        if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_DEL) {
            val currentTag = indexOfCurrentFocus
            val currentEditText = editTextList[currentTag].text
            if (currentTag == mPinLength - 1 && finalNumberPin) {
                if (!currentEditText.isNullOrEmpty()) {
                    this.editTextList[currentTag].setText("")
                }
                finalNumberPin = false
            } else if (currentTag > 0) {
                mDelPressed = true
                if (currentEditText.isNullOrEmpty()) {
                    this.editTextList[currentTag - 1].requestFocus()
                }
                this.editTextList[currentTag].setText("")
            } else {
                if (!currentEditText.isNullOrEmpty()) {
                    editTextList[currentTag].setText("")
                }
            }
            return true
        }
        return false
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    fun setPinViewEventListener(listener: PinViewEventListener?) {
        mListener = listener
    }

    inner class LastInputFilter: InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int,
        ): CharSequence? {
            return if (finalNumberPin && editTextList[mPinLength - 1].text.isNotEmpty()) {
                ""
            } else {
                null
            }
        }
    }

}
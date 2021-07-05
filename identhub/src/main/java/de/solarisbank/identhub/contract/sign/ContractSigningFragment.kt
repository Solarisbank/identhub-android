package de.solarisbank.identhub.contract.sign

import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.contract.ContractViewModel
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.verfication.phone.CountDownTime
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel
import de.solarisbank.identhub.verfication.phone.format
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.core.viewModels
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.*

class ContractSigningFragment : IdentHubFragment() {
    private var currentFocusedEditText: View? = null
    private val digitsEditTexts: MutableList<EditText> = ArrayList()
    private var disposable = Disposables.disposed()
    private val listLevelDrawables: MutableList<LevelListDrawable> = ArrayList()
    private val sharedViewModel: ContractViewModel by lazy<ContractViewModel> { activityViewModels() }
    private val viewModel: ContractSigningViewModel by lazy<ContractSigningViewModel> { viewModels() }

    private lateinit var codeInput: EditText
    private lateinit var description: TextView
    private lateinit var sendNewCode: TextView
    private lateinit var newCodeCounter: TextView
    private lateinit var transactionDescription: TextView
    private lateinit var errorMessage: TextView
    private lateinit var submitButton: TextView

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contract_signing, container, false)
                .also {
                    codeInput = it.findViewById(R.id.codeInput)
                    description = it.findViewById(R.id.description)
                    sendNewCode = it.findViewById(R.id.sendNewCode)
                    newCodeCounter = it.findViewById(R.id.newCodeCounter)
                    transactionDescription = it.findViewById(R.id.transactionDescription)
                    errorMessage = it.findViewById(R.id.errorMessage)
                    submitButton = it.findViewById(R.id.submitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeAuthorizeResult()
        observeIdentificationResult()
        observableInputs()
        observeCountDownTimeEvent()
    }

    private fun initViews() {
        initFocusListener()
        initStateOfDigitInputField()
        viewModel.startTimer()
        sendNewCode.setOnClickListener { viewModel.onSendNewCodeClicked() }
        codeInput.addTextChangedListener(codeInputValidator)
    }

    private fun initFocusListener() {
        val onFocusChangeListener = OnFocusChangeListener { view: View?, hasFocus: Boolean ->
            if (hasFocus) {
                currentFocusedEditText = view
            }
        }
        codeInput.requestFocus()
    }

    private fun initStateOfDigitInputField() {
        digitsEditTexts.add(codeInput)
    }

    private fun observeAuthorizeResult() {
        viewModel.getAuthorizeResultLiveData().observe(viewLifecycleOwner, Observer { onAuthorizeResultChanged(it) })
    }

    private fun onAuthorizeResultChanged(result: Result<Any>) {
        when (result) {
            is Result.Success -> {
                sendNewCode.visibility = View.VISIBLE
                submitButton.visibility = View.GONE
            }
            is Result.Error -> {
                sendNewCode.visibility = View.GONE
                submitButton.visibility = View.VISIBLE
            }
            else -> {
                defaultState()
            }
        }
    }

    private fun observeIdentificationResult() {
        viewModel.getIdentificationResultLiveData().observe(viewLifecycleOwner, Observer { onIdentificationResultChanged(it) })
    }

    private fun onIdentificationResultChanged(result: Result<IdentificationUiModel>) {
        Timber.d("onIdentificationResultChanged, result: ${result.data}")
        if (result.succeeded && Status.getEnum(result.data?.status) == Status.SUCCESSFUL) {
            transactionDescription.text = String.format(getString(R.string.contract_signing_preview_transaction_info), result.data?.id)
            onStateOfDigitInputChanged(SUCCESS_STATE)
            sharedViewModel.navigateToSummary()
        } else if (Status.getEnum(result.data?.status) == Status.FAILED || Status.getEnum(result.data?.status) == Status.CONFIRMED) {
            onStateOfDigitInputChanged(ERROR_STATE)
            sharedViewModel.callOnFailureResult()
        }
    }

    private fun observeCountDownTimeEvent() {
        viewModel.getCountDownTimeEventLiveData().observe(viewLifecycleOwner, Observer { onCountDownTimeState(it) })
    }

    private fun onCountDownTimeState(event: Event<CountDownTime>) {
        val countDownTime = event.content
        if (countDownTime != null) {
            sendNewCode.visibility = if (countDownTime.isFinish) View.VISIBLE else View.GONE
            errorMessage.visibility = if (countDownTime.isFinish) View.VISIBLE else View.GONE
            codeInput.isEnabled = !countDownTime.isFinish
            submitButton.visibility = if (!countDownTime.isFinish) View.VISIBLE else View.GONE
            newCodeCounter.visibility = if (!countDownTime.isFinish) View.VISIBLE else View.INVISIBLE
            newCodeCounter.text = String.format(getString(R.string.contract_signing_code_expires), countDownTime.format())

        }
    }

    private fun observableInputs() {
        Timber.d("observableInputs, Samsung")
        disposable = RxView.clicks(submitButton)
                .flatMapSingle {
                    onStateOfDigitInputChanged(LOADING_STATE)
                    Observable.fromIterable(digitsEditTexts)
                            .map { editText: EditText -> editText.text.toString() }
                            .toList()
                }
                .map { it.joinToString("") }
                .filter { token: String -> token.length == VerificationPhoneViewModel.MIN_CODE_LENGTH }
                .subscribe(
                        { token: String -> viewModel.onSubmitButtonClicked(token) },
                        { Timber.e(it, "Something went wrong") }
                )
    }

    private fun onStateOfDigitInputChanged(state: Int) {
        errorMessage.visibility = if (state == ERROR_STATE) View.VISIBLE else View.GONE
        sendNewCode.visibility = if (state == ERROR_STATE) View.VISIBLE else View.GONE
        submitButton.visibility = if (state != ERROR_STATE) View.VISIBLE else View.GONE
        newCodeCounter.visibility = if (state != ERROR_STATE) View.VISIBLE else View.GONE

        submitButton.isEnabled = state != LOADING_STATE
        submitButton.setText(if (state == LOADING_STATE) R.string.verification_phone_status_verifying else R.string.contract_signing_preview_sign_action)
        codeInput.isEnabled = state != LOADING_STATE && state != ERROR_STATE
        listLevelDrawables.forEach { it.level = state }
    }

    private fun defaultState() {
        listLevelDrawables.forEach { it.level = 0 }
        currentFocusedEditText?.clearFocus()
        codeInput.requestFocus()
        newCodeCounter.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        codeInput.text = null
    }

    private val codeInputValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            submitButton.isEnabled = !codeInput.text.isNullOrEmpty() && codeInput.text.length == CODE_LENGTH
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onDestroyView() {
        digitsEditTexts.clear()
        disposable.dispose()
        currentFocusedEditText = null
        super.onDestroyView()
    }

    companion object {

        const val CODE_LENGTH = 6

        const val DEFAULT_STATE = 0
        const val SUCCESS_STATE = 1
        const val ERROR_STATE = 2
        const val LOADING_STATE = 3

        fun newInstance(): Fragment {
            return ContractSigningFragment()
        }
    }
}
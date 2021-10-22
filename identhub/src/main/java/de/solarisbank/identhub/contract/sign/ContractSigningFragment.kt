package de.solarisbank.identhub.contract.sign

import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.contract.ContractViewModel
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModel
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.data.entity.CountDownTime
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.data.entity.format
import de.solarisbank.sdk.domain.model.result.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.*

class ContractSigningFragment : IdentHubFragment() {
    private val digitsEditTexts: MutableList<EditText> = ArrayList()
    private var disposable = Disposables.disposed()
    private val listLevelDrawables: MutableList<LevelListDrawable> = ArrayList()
    private val sharedViewModel: ContractViewModel by lazy<ContractViewModel> { activityViewModels() }
    private val viewModel: ContractSigningViewModel by lazy<ContractSigningViewModel> { viewModels() }

    private var codeInput: EditText? = null
    private var currentFocusedEditText: View? = null
    private var description: TextView? = null
    private var sendNewCode: Button? = null
    private var newCodeCounter: TextView? = null
    private var transactionDescription: TextView? = null
    private var errorMessage: TextView? = null
    private var submitButton: Button? = null
    private var progress: ProgressBar? = null
    private var codeProgress: ProgressBar? = null
    private var imageView: ImageView? = null

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
                    progress = it.findViewById(R.id.progress)
                    codeProgress = it.findViewById(R.id.codeProgress)
                    imageView = it.findViewById(R.id.scratch)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        submitButton?.customize(customization)
        sendNewCode?.customize(customization)
        imageView?.isVisible = customization.customFlags.shouldShowLargeImages
        progress?.customize(customization)
        codeProgress?.customize(customization)
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
        observePhoneNumberResult()
        sendNewCode!!.setOnClickListener {
            sendNewCodeClickListener()
        }
        codeInput!!.addTextChangedListener(codeInputValidator)
        onStateOfDigitInputChanged(DEFAULT_STATE)
    }

    private fun sendNewCodeClickListener() {
        viewModel.onSendNewCodeClicked()
        viewModel.startTimer()
        codeInput!!.text.clear()
        codeInput!!.addTextChangedListener(codeInputValidator)
    }

    private fun initFocusListener() {
        val onFocusChangeListener = OnFocusChangeListener { view: View?, hasFocus: Boolean ->
            if (hasFocus) {
                currentFocusedEditText = view
            }
        }
        codeInput!!.requestFocus()
    }

    private fun initStateOfDigitInputField() {
        digitsEditTexts.add(codeInput!!)
    }

    private fun observePhoneNumberResult() {
        viewModel.getPhoneNumberResultLiveData().observe(viewLifecycleOwner, Observer { onPhoneNumberResult(it) })
    }

    private fun onPhoneNumberResult(result: Result<MobileNumberDto>) {
        if (result.succeeded && result.data != null) {
            var number = result.data!!.number
            if (number != null) {
                number = number.replace("\\d(?=\\d{4})".toRegex(), "*")
            }
            description!!.text = String.format(requireContext().resources.getString(R.string.contract_signing_description), number)
            description!!.visibility = View.VISIBLE
            progress!!.visibility = View.INVISIBLE
        }
    }

    private fun observeAuthorizeResult() {
        viewModel.getAuthorizeResultLiveData().observe(viewLifecycleOwner, Observer { onAuthorizeResultChanged(it) })
    }

    private fun onAuthorizeResultChanged(result: Result<Any>) {
        when (result) {
            is Result.Success -> {
                sendNewCode!!.visibility = View.VISIBLE
                submitButton!!.visibility = View.GONE
            }
            is Result.Error -> {
                sendNewCode!!.visibility = View.GONE
                submitButton!!.visibility = View.VISIBLE
            }
            else -> {
                onStateOfDigitInputChanged(DEFAULT_STATE)
            }
        }
    }

    private fun observeIdentificationResult() {
        viewModel.getIdentificationResultLiveData().observe(viewLifecycleOwner, Observer { onIdentificationResultChanged(it) })
    }

    private fun onIdentificationResultChanged(result: Result<IdentificationDto>) {
        //todo move to use case and replace with states
        Timber.d("onIdentificationResultChanged, result: ${result.data}")
        if (result.succeeded && Status.getEnum(result.data?.status) == Status.SUCCESSFUL) {
            transactionDescription!!.text = String.format(getString(R.string.contract_signing_preview_transaction_info), result.data?.id)
            onStateOfDigitInputChanged(SUCCESS_STATE)
            sharedViewModel.callOnSuccessResult()
        } else if (Status.getEnum(result.data?.status) == Status.FAILED || Status.getEnum(result.data?.status) == Status.CONFIRMED) {
            onStateOfDigitInputChanged(ERROR_STATE)
        } else if (result.throwable?.message?.contains("422") == true || result.throwable?.message?.contains("409") == true) {
            onStateOfDigitInputChanged(ERROR_STATE)
        }
    }

    private fun observeCountDownTimeEvent() {
        viewModel.getCountDownTimeEventLiveData().observe(viewLifecycleOwner, Observer { onCountDownTimeState(it) })
    }

    private fun onCountDownTimeState(event: Event<CountDownTime>) {
        val countDownTime = event.content
        if (countDownTime != null) {
            sendNewCode!!.visibility = if (countDownTime.isFinish) View.VISIBLE else View.GONE
            errorMessage!!.visibility = if (countDownTime.isFinish) View.VISIBLE else View.GONE
            codeInput!!.isEnabled = !countDownTime.isFinish
            submitButton!!.visibility = if (!countDownTime.isFinish) View.VISIBLE else View.GONE
            newCodeCounter!!.visibility = if (!countDownTime.isFinish) View.VISIBLE else View.INVISIBLE
            newCodeCounter!!.text = String.format(getString(R.string.contract_signing_code_expires), countDownTime.format())

        }
    }

    private fun observableInputs() {
        Timber.d("observableInputs")
        disposable = RxView.clicks(submitButton!!)
                .flatMapSingle {
                    onStateOfDigitInputChanged(LOADING_STATE)
                    Observable.fromIterable(digitsEditTexts)
                            .map { editText: EditText -> editText.text.toString() }
                            .toList()
                }
                .map { it.joinToString("") }
                .filter { token: String -> token.length == PhoneVerificationViewModel.MIN_CODE_LENGTH }
                .subscribe(
                        { token: String -> viewModel.onSubmitButtonClicked(token) },
                        { Timber.e(it, "Something went wrong") }
                )
    }

    private fun onStateOfDigitInputChanged(state: Int) {
        val error = state == ERROR_STATE || viewModel.getCounterFinished()
        submitButton!!.setText(if (state == LOADING_STATE) R.string.verification_phone_status_verifying else R.string.contract_signing_preview_sign_action)
        submitButton!!.isEnabled = state != LOADING_STATE && isSubmitButtonEnabled()

        errorMessage!!.visibility = if (error) View.VISIBLE else View.GONE
        sendNewCode!!.visibility = if (error) View.VISIBLE else View.GONE
        submitButton!!.visibility = if (!error) View.VISIBLE else View.GONE
        newCodeCounter!!.visibility = if (!error) View.VISIBLE else View.GONE

        codeProgress!!.visibility = if (state == LOADING_STATE) View.VISIBLE else View.GONE
        codeInput!!.isEnabled = state != LOADING_STATE && !error
        listLevelDrawables.forEach { it.level = state }
    }

    private val codeInputValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            submitButton!!.isEnabled = isSubmitButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {
       }

    }

    private fun isSubmitButtonEnabled(): Boolean {
        return !codeInput!!.text.isNullOrEmpty() && codeInput!!.text.length == CODE_LENGTH

    }
    override fun onDestroyView() {
        disposable.dispose()
        digitsEditTexts.clear()
        currentFocusedEditText = null
        codeInput = null
        description = null
        sendNewCode = null
        newCodeCounter = null
        transactionDescription = null
        errorMessage = null
        submitButton = null
        progress = null
        codeProgress = null
        imageView = null
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
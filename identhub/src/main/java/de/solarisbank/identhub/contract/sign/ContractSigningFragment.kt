package de.solarisbank.identhub.contract.sign

import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.text.TextWatcher
import android.view.FocusFinder
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.BaseFragment
import de.solarisbank.identhub.base.activityViewModels
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.base.viewModels
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.databinding.FragmentContractSigningBinding
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.ui.DefaultTextWatcher
import de.solarisbank.identhub.verfication.phone.CountDownTime
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel
import de.solarisbank.identhub.verfication.phone.format
import de.solarisbank.shared.result.Event
import de.solarisbank.shared.result.Result
import de.solarisbank.shared.result.data
import de.solarisbank.shared.result.succeeded
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.*

class ContractSigningFragment : BaseFragment() {
    private val binding: FragmentContractSigningBinding by viewBinding(FragmentContractSigningBinding::inflate)
    private var currentFocusedEditText: View? = null
    private val digitsEditTexts: MutableList<EditText> = ArrayList()
    private var disposable = Disposables.disposed()
    private val listLevelDrawables: MutableList<LevelListDrawable> = ArrayList()
    private val sharedViewModel: IdentityActivityViewModel by lazy { activityViewModels() }
    private val viewModel: ContractSigningViewModel by lazy { viewModels() }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeAuthorizeResult()
        observeConfirmationResult()
        observeIdentificationResult()
        observableInputs()
        observeCountDownTimeEvent()
    }

    private fun initViews() {
        initFocusListener()
        initTextWatcher()
        initStateOfDigitInputField()
        binding.run {
            description.text = String.format(getString(R.string.verification_phone_description), "")
            sendNewCode.setOnClickListener { viewModel.onSendNewCodeClicked() }
        }
    }

    private fun initFocusListener() {
        val onFocusChangeListener = OnFocusChangeListener { view: View?, hasFocus: Boolean ->
            if (hasFocus) {
                currentFocusedEditText = view
            }
        }
        binding.run {
            firstDigit.onFocusChangeListener = onFocusChangeListener
            secondDigit.onFocusChangeListener = onFocusChangeListener
            thirdDigit.onFocusChangeListener = onFocusChangeListener
            fourthDigit.onFocusChangeListener = onFocusChangeListener
            fifthDigit.onFocusChangeListener = onFocusChangeListener
            sixthDigit.onFocusChangeListener = onFocusChangeListener
            firstDigit.requestFocus()
        }
    }

    private fun initTextWatcher() {
        val textWatcher: TextWatcher = object : DefaultTextWatcher() {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (after > count && after == 1) {
                    changeFocusToNextInput()
                }
            }
        }
        binding.run {
            firstDigit.addTextChangedListener(textWatcher)
            secondDigit.addTextChangedListener(textWatcher)
            thirdDigit.addTextChangedListener(textWatcher)
            fourthDigit.addTextChangedListener(textWatcher)
            fifthDigit.addTextChangedListener(textWatcher)
        }
    }

    private fun changeFocusToNextInput() {
        val view = FocusFinder.getInstance().findNextFocus(binding.root, currentFocusedEditText, View.FOCUS_RIGHT)
        view.requestFocus()
    }

    private fun initStateOfDigitInputField() {
        binding.run {
            digitsEditTexts.add(firstDigit)
            digitsEditTexts.add(secondDigit)
            digitsEditTexts.add(thirdDigit)
            digitsEditTexts.add(fourthDigit)
            digitsEditTexts.add(fifthDigit)
            digitsEditTexts.add(sixthDigit)
            listLevelDrawables.add(firstDigit.background as LevelListDrawable)
            listLevelDrawables.add(secondDigit.background as LevelListDrawable)
            listLevelDrawables.add(thirdDigit.background as LevelListDrawable)
            listLevelDrawables.add(fourthDigit.background as LevelListDrawable)
            listLevelDrawables.add(fifthDigit.background as LevelListDrawable)
            listLevelDrawables.add(sixthDigit.background as LevelListDrawable)
        }
    }

    private fun observeAuthorizeResult() {
        viewModel.getAuthorizeResultLiveData().observe(viewLifecycleOwner, { onAuthorizeResultChanged(it) })
    }

    private fun onAuthorizeResultChanged(result: Result<Any>) {
        when (result) {
            is Result.Success -> {
                binding.run {
                    sendNewCode.visibility = View.VISIBLE
                    newCodeCounter.visibility = View.GONE
                }
            }
            is Result.Error -> {
                binding.run {
                    sendNewCode.visibility = View.GONE
                    newCodeCounter.visibility = View.VISIBLE
                }
            }
            else -> {
                defaultState()
            }
        }
    }

    private fun observeIdentificationResult() {
        viewModel.getIdentificationResultLiveData().observe(viewLifecycleOwner, { onIdentificationResultChanged(it) })
    }

    private fun onIdentificationResultChanged(result: Result<Identification>) {
        if (result.succeeded) {
            binding.transactionDescription.text = String.format(getString(R.string.contract_signing_preview_transaction_info), result.data?.id)
        }
    }

    private fun observeConfirmationResult() {
        viewModel.getConfirmResultLiveData().observe(viewLifecycleOwner, { onConfirmationResultChanged(it) })
    }

    private fun onConfirmationResultChanged(result: Result<Any>) {
        when (result) {
            is Result.Success<*> -> {
                onStateOfDigitInputChanged(SUCCESS_STATE)
                sharedViewModel.navigateToSummary()
            }
            is Result.Error -> {
                onStateOfDigitInputChanged(ERROR_STATE)
            }
            else -> {
                onStateOfDigitInputChanged(LOADING_STATE)
            }
        }
    }

    private fun observeCountDownTimeEvent() {
        viewModel.getCountDownTimeEventLiveData().observe(viewLifecycleOwner, { event: Event<CountDownTime> -> onCountDownTimeState(event) })
    }

    private fun onCountDownTimeState(event: Event<CountDownTime>) {
        val countDownTime = event.content
        if (countDownTime != null) {
            binding.run {
                newCodeCounter.visibility = if (countDownTime.isFinish) View.GONE else View.VISIBLE
                sendNewCode.visibility = if (countDownTime.isFinish) View.VISIBLE else View.GONE
                newCodeCounter.text = String.format(getString(R.string.verification_phone_request_code), countDownTime.format())
            }
        }
    }

    private fun observableInputs() {
        disposable = RxView.clicks(binding.submitButton)
                .flatMapSingle {
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
        binding.run {
            newCodeCounter.visibility = if (state == DEFAULT_STATE) View.VISIBLE else View.GONE
            errorMessage.visibility = if (state == ERROR_STATE) View.VISIBLE else View.GONE
            sendNewCode.visibility = if (state == ERROR_STATE) View.VISIBLE else View.GONE
            sendNewCode.isEnabled = state != LOADING_STATE
            submitButton.isEnabled = state != LOADING_STATE
            submitButton.setText(if (state == LOADING_STATE) R.string.verification_phone_status_verifying else R.string.contract_signing_preview_sign_action)
            firstDigit.isEnabled = state != LOADING_STATE
            secondDigit.isEnabled = state != LOADING_STATE
            thirdDigit.isEnabled = state != LOADING_STATE
            fourthDigit.isEnabled = state != LOADING_STATE
            fifthDigit.isEnabled = state != LOADING_STATE
            sixthDigit.isEnabled = state != LOADING_STATE
        }

        listLevelDrawables.forEach { it.level = state }
    }

    private fun defaultState() {
        listLevelDrawables.forEach { it.level = 0 }
        currentFocusedEditText?.clearFocus()

        binding.run {
            firstDigit.requestFocus()
            errorMessage.visibility = View.GONE
            firstDigit.text = null
            secondDigit.text = null
            thirdDigit.text = null
            fourthDigit.text = null
            fifthDigit.text = null
            sixthDigit.text = null
            sixthDigit.invalidate()
        }
    }

    override fun onDestroyView() {
        digitsEditTexts.clear()
        disposable.dispose()
        currentFocusedEditText = null
        super.onDestroyView()
    }

    companion object {
        const val DEFAULT_STATE = 0
        const val SUCCESS_STATE = 1
        const val ERROR_STATE = 2
        const val LOADING_STATE = 3

        fun newInstance(): Fragment {
            return ContractSigningFragment()
        }
    }
}
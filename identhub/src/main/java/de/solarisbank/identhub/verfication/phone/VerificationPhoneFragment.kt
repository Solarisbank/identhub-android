package de.solarisbank.identhub.verfication.phone

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
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse
import de.solarisbank.identhub.databinding.FragmentVerificationPhoneBinding
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.ui.DefaultTextWatcher
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.Result.Loading
import de.solarisbank.sdk.core.result.Type
import de.solarisbank.sdk.core.result.Type.ResourceNotFound
import de.solarisbank.sdk.core.view.viewBinding
import de.solarisbank.sdk.core.viewModels
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import timber.log.Timber

class VerificationPhoneFragment : IdentHubFragment() {
    private val binding: FragmentVerificationPhoneBinding by viewBinding { FragmentVerificationPhoneBinding.inflate(layoutInflater) }
    private var currentFocusedEditText: View? = null
    private var digitsEditTexts: MutableList<EditText>? = null
    private var disposable = Disposables.disposed()
    private var listLevelDrawables: MutableList<LevelListDrawable>? = null
    private val sharedViewModel: IdentityActivityViewModel by lazy<IdentityActivityViewModel> { activityViewModels() }
    private val viewModel: VerificationPhoneViewModel by lazy<VerificationPhoneViewModel> { viewModels() }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeAuthorizeState()
        observeConfirmationState()
        observeCountDownTimeState()
        observeInputs()
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

    private fun observeAuthorizeState() {
        viewModel.getAuthorizeResultLiveData().observe(viewLifecycleOwner, Observer { result: Result<VerificationPhoneResponse> -> onAuthorizeStateChanged(result) })
    }

    private fun observeConfirmationState() {
        viewModel.getConfirmResultLiveData().observe(viewLifecycleOwner, Observer { result: Result<VerificationPhoneResponse> -> onConfirmationResultChanged(result) })
    }

    private fun observeCountDownTimeState() {
        viewModel.getCountDownTimeEventLiveData().observe(viewLifecycleOwner, Observer { event: Event<CountDownTime> -> onCountDownTimeState(event) })
    }

    private fun observeInputs() {
        disposable = RxView.clicks(binding.submitButton)
                .flatMapSingle {
                    Observable.fromIterable(digitsEditTexts)
                            .map { it.text.toString() }
                            .toList()
                }
                .map { it.joinToString("") }
                .filter { token -> token.length == VerificationPhoneViewModel.MIN_CODE_LENGTH }
                .subscribe(
                        { viewModel.onSubmitButtonClicked(it) },
                        { Timber.e(it, "Something went wrong") }
                )
    }

    private fun onAuthorizeStateChanged(result: Result<VerificationPhoneResponse>) {
        when (result) {
            is Result.Error -> {
                binding.run {
                    sendNewCode.visibility = View.VISIBLE
                    newCodeCounter.visibility = View.GONE
                }
            }
            is Result.Success<*> -> {
                binding.run {
                    sendNewCode.visibility = View.GONE
                    newCodeCounter.visibility = View.VISIBLE
                }
            }
            is Loading -> {
                defaultState()
            }
        }
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

    private fun initStateOfDigitInputField() {
        binding.run {
            digitsEditTexts = mutableListOf(firstDigit, secondDigit, thirdDigit, fourthDigit, fifthDigit, sixthDigit)
            listLevelDrawables = mutableListOf(firstDigit.background as LevelListDrawable,
                    secondDigit.background as LevelListDrawable,
                    thirdDigit.background as LevelListDrawable,
                    fourthDigit.background as LevelListDrawable,
                    fifthDigit.background as LevelListDrawable,
                    sixthDigit.background as LevelListDrawable)
        }

    }

    private fun onConfirmationResultChanged(result: Result<VerificationPhoneResponse>) {
        var state = DEFAULT_STATE
        if (result is Result.Success<*>) {
            state = SUCCESS_STATE
        } else if (result is Result.Error) {
            state = ERROR_STATE
        } else if (result is Loading) {
            state = LOADING_STATE
        }
        binding.run {
            newCodeCounter.visibility = if (state == DEFAULT_STATE) View.VISIBLE else View.GONE
            errorMessage.visibility = if (state == ERROR_STATE) View.VISIBLE else View.GONE
            sendNewCode.visibility = if (state == ERROR_STATE) View.VISIBLE else View.GONE
            sendNewCode.isEnabled = state != LOADING_STATE
            submitButton.isEnabled = state != LOADING_STATE
            submitButton.setText(if (state == LOADING_STATE) R.string.verification_phone_status_verifying else R.string.verification_phone_action_submit)
            firstDigit.isEnabled = state != LOADING_STATE
            secondDigit.isEnabled = state != LOADING_STATE
            thirdDigit.isEnabled = state != LOADING_STATE
            fourthDigit.isEnabled = state != LOADING_STATE
            fifthDigit.isEnabled = state != LOADING_STATE
            sixthDigit.isEnabled = state != LOADING_STATE
        }

        listLevelDrawables?.forEach { it.level = state }

        if (state == SUCCESS_STATE) {
            sharedViewModel.navigateToVerificationPhoneSuccess()
        } else if (state == ERROR_STATE) {
            val (typ) = result as Result.Error
            if (typ is ResourceNotFound || typ is Type.ServerError) {
                sharedViewModel.navigateToVerificationPhoneError()
            }
        }
    }

    private fun defaultState() {
        listLevelDrawables?.forEach { it.level = 0 }
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
        digitsEditTexts?.clear()
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
            return VerificationPhoneFragment()
        }
    }
}
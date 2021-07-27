package de.solarisbank.identhub.verfication.bank

import android.graphics.Rect
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.feature.model.ErrorState
import de.solarisbank.identhub.ui.CustomClickableSpan
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankIbanFragment : IdentHubFragment() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val sharedViewModel: VerificationBankViewModel by lazy<VerificationBankViewModel> { activityViewModels() }
    private val ibanViewModel: VerificationBankIbanViewModel by lazy<VerificationBankIbanViewModel> { viewModels() }

    private var ibanNumber: EditText? = null
    private var ibanInputErrorLabel: TextView? = null
    private var progressBar: ProgressBar? = null
    private var submitButton: TextView? = null
    private var secondDescription: TextView? = null

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verification_bank_iban, container, false)
                .also {
                    ibanNumber = it.findViewById(R.id.ibanNumber)
                    ibanInputErrorLabel = it.findViewById(R.id.errorMessage)
                    progressBar = it.findViewById(R.id.progressBar)
                    submitButton = it.findViewById(R.id.submitButton)
                    secondDescription = it.findViewById(R.id.secondDescription)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeVerifyResult()
    }

    private fun observeVerifyResult() {
        ibanViewModel.getVerificationStateLiveData().observe(viewLifecycleOwner, Observer { setState(it) })
    }

    private fun setState(state: VerificationState) {
        Timber.d("setState: ${state}")
        if (state is SealedVerificationState.IbanVerificationSuccessful) {
            Timber.d("setState 1")
            sharedViewModel.moveToEstablishSecureConnection(bankIdentificationUrl = state.bankIdentificationUrl, nextStep = state.nextStep)
        } else {
            Timber.d("setState 2")
            ibanNumber!!.isEnabled = state.isIbanNumberEnabled
            (ibanNumber!!.background as StateListDrawable).level = state.ibanBackgroundItem
            progressBar!!.isVisible = state.isProgressBarShown
            ibanInputErrorLabel!!.visibility = state.ibanInputErrorLabelVisibility
            submitButton!!.isEnabled = state.isSubmitButtonEnabled

            if (state is ErrorState) {
                Timber.d("setState 3")
                showAlert(state)
            }
        }
    }

    private fun showAlert(state: ErrorState) {
        when (state) {
            is SealedVerificationState.InvalidBankIdError -> {
                Timber.d("showAlert 1, state.retryAllowed ${state.retryAllowed}")
                showAlertFragment(
                        title = state.dialogTitle.getStringRes(),
                        message = state.dialogMessage.getStringRes(),
                        positiveLabel = state.dialogPositiveLabel.getStringRes(),
                        positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) },
                        negativeLabel = if (state.retryAllowed) state.dialogNegativeLabel.getStringRes() else null,
                        negativeAction = if (state.retryAllowed) ({ retryInputIBan() }) else null,
                        cancelAction =
                        if (state.retryAllowed) ({ retryInputIBan() })
                        else ({ sharedViewModel.postDynamicNavigationNextStep(state.nextStep) })
                )
            }
            is SealedVerificationState.AlreadyIdentifiedSuccessfullyError,
            is SealedVerificationState.ExceedMaximumAttemptsError,
            is SealedVerificationState.GenericError -> {
                Timber.d("showAlert 2")
                showAlertFragment(
                        title = state.dialogTitle.getStringRes(),
                        message = state.dialogMessage.getStringRes(),
                        positiveLabel = state.dialogPositiveLabel.getStringRes(),
                        positiveAction = { sharedViewModel.callOnFailure() },
                        cancelAction = { sharedViewModel.callOnFailure() }
                )
            }

        }
    }

    private fun retryInputIBan() {
        Timber.d("retryInputIBan()")
        setState(SealedVerificationState.IbanIput())
    }

    private fun initViews() {
        Timber.d("initViews()")
        setState(SealedVerificationState.IbanIput())
        ibanNumber!!.addTextChangedListener(ibanTextValidator)
        compositeDisposable.add(RxView.clicks(submitButton!!)
                .map { ibanNumber!!.text.toString().filter { !it.isWhitespace() } }
                .subscribe(
                        { iBan: String ->
                            Timber.d("submitButton success")
                            sharedViewModel.iban = iBan
                            ibanViewModel.onSubmitButtonClicked(iBan)
                        },
                        { throwable: Throwable? -> Timber.e(throwable, "Cannot valid IBAN") })
        )
        addInfoToDescription()
    }

    private fun addInfoToDescription() {
        val descriptionText = getString(R.string.verification_bank_second_description)
        val desc = SpannableString("$descriptionText ")
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_question)!!
        drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val imageSpan = ImageSpan(drawable)
        val clickSpan = CustomClickableSpan {
            showAlertFragment(
                getString(R.string.about_identity_verification_title),
                getString(R.string.about_identity_verification_message),
                getString(R.string.ok_button),
                positiveAction = {}
            )
        }
        val length = descriptionText.length
        desc.setSpan(imageSpan, length, length + 1, SPAN_INCLUSIVE_EXCLUSIVE)
        desc.setSpan(clickSpan, length, length + 1, SPAN_INCLUSIVE_EXCLUSIVE)
        secondDescription!!.text = desc
        secondDescription!!.movementMethod = LinkMovementMethod.getInstance()
    }

    private val ibanTextValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            submitButton!!.isEnabled = !ibanNumber!!.text.isNullOrEmpty() && ibanNumber!!.text.length >= MIN_IBAN_LENGTH
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        ibanNumber!!.removeTextChangedListener(ibanTextValidator)
        ibanNumber = null
        ibanInputErrorLabel = null
        progressBar = null
        submitButton = null
        super.onDestroyView()
    }

    companion object {
        private const val MIN_IBAN_LENGTH = 15
    }
}

interface VerificationState {
    val isIbanNumberEnabled: Boolean
    val ibanBackgroundItem: Int
    val isProgressBarShown: Boolean
    val ibanInputErrorLabelVisibility: Int
    val isSubmitButtonEnabled: Boolean
}

sealed class SealedVerificationState : VerificationState {

    class IbanIput : VerificationState {
        override val isIbanNumberEnabled = true
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
    }

    class Loading : VerificationState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = true
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
    }

    class IbanVerificationSuccessful(
            val bankIdentificationUrl: String?,
            val nextStep: String?
            ) : VerificationState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
    }

    class GenericError : SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitle = "generic_error_title"
        override val dialogMessage = "generic_error_message"
        override val dialogPositiveLabel = "ok_button"
        override val dialogNegativeLabel = null
    }

    class InvalidBankIdError(val nextStep: String, val retryAllowed: Boolean) :
            SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 2
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.VISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitle = "invalid_iban_title"
        override val dialogMessage = "invalid_iban_message"
        override val dialogPositiveLabel = "ok_button"
        override val dialogNegativeLabel = "retry_button"
    }

    class AlreadyIdentifiedSuccessfullyError: SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitle = "already_identified_title"
        override val dialogMessage = "already_identified_title"
        override val dialogPositiveLabel = "ok_button"
        override val dialogNegativeLabel = null
    }

    class ExceedMaximumAttemptsError: SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitle = "excess_attempts_title"
        override val dialogMessage = "excess_attempts_message"
        override val dialogPositiveLabel = "ok_button"
        override val dialogNegativeLabel = null
    }
}
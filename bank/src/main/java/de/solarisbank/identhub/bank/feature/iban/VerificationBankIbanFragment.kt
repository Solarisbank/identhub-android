package de.solarisbank.identhub.bank.feature.iban

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.bank.BankModule
import de.solarisbank.identhub.bank.R
import de.solarisbank.identhub.bank.data.ErrorState
import de.solarisbank.identhub.bank.feature.VerificationBankViewModel
import de.solarisbank.sdk.data.FIRST_STEP_KEY
import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.ButtonStyle
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.customization.customizeLinks
import de.solarisbank.sdk.feature.extension.linkOccurrenceOf
import de.solarisbank.sdk.feature.view.BulletListLayout
import de.solarisbank.sdk.feature.view.hideKeyboard
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class VerificationBankIbanFragment : NewBaseFragment() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val sharedViewModel: VerificationBankViewModel by koinNavGraphViewModel(BankModule.navigationId)
    private val ibanViewModel: VerificationBankIbanViewModel by viewModel()

    private var ibanNumber: EditText? = null
    private var ibanInputErrorLabel: TextView? = null
    private var progressBar: ProgressBar? = null
    private var submitButton: Button? = null
    private var imageView: ImageView? = null
    private var termsCheckBox: CheckBox? = null
    private var termsDisclaimer: TextView? = null
    private var termsLayout: View? = null
    private var noticeBulletList: BulletListLayout? = null

    private var defaultToBankId: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initDefaultToBankId()
        return inflater.inflate(R.layout.identhub_fragment_verification_bank_iban, container, false)
                .also {
                    ibanNumber = it.findViewById(R.id.ibanNumber)
                    ibanInputErrorLabel = it.findViewById(R.id.errorMessage)
                    progressBar = it.findViewById(R.id.progressBar)
                    submitButton = it.findViewById(R.id.submitButton)
                    imageView = it.findViewById(R.id.image)
                    termsCheckBox = it.findViewById(R.id.termsCheckBox)
                    termsCheckBox?.setOnCheckedChangeListener { _, _ -> updateSubmitButtonState() }
                    termsDisclaimer = it.findViewById(R.id.termsDisclaimer)
                    termsLayout = it.findViewById(R.id.termsLayout)
                    noticeBulletList = it.findViewById(R.id.noticeBulletList)
                    updateBulletList()
                    customizeUI()
                }
    }

    private fun customizeUI() {
        submitButton?.customize(customization, ButtonStyle.Primary)
        imageView?.isVisible = customization.customFlags.shouldShowLargeImages
        termsCheckBox?.customize(customization)
        termsDisclaimer?.customizeLinks(customization)
    }

    private fun updateBulletList() {
        val notice = getString(R.string.identhub_verification_bank_notice_value)
        if (notice.isNotBlank()) {
            noticeBulletList?.isVisible = true
            noticeBulletList?.updateItems(
                title = getString(R.string.identhub_verification_bank_notice_label),
                titleStyle = BulletListLayout.TitleStyle.Notice,
                items = listOf(notice)
            )
        } else {
            noticeBulletList?.isVisible = false
        }
    }

    private fun initDefaultToBankId() {
        requireActivity().intent.getStringExtra(FIRST_STEP_KEY)?.let {
            if (it == IdentificationStep.BANK_ID_IBAN.destination) {
                defaultToBankId = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeVerifyResult()
    }

    private fun observeVerifyResult() {
        ibanViewModel.getVerificationStateLiveData().observe(viewLifecycleOwner) { setState(it) }
        ibanViewModel.getTermsAgreedLiveData().observe(viewLifecycleOwner) { agreed ->
            if (agreed) {
                termsCheckBox?.isChecked = true
                termsLayout?.isVisible = false
            } else {
                termsCheckBox?.isVisible = true
            }
        }
        sharedViewModel.navigator = navigator
    }

    private fun setState(state: VerificationState) {
        Timber.d("setState: ${state}")
        if (state is SealedVerificationState.IbanVerificationSuccessful) {
            Timber.d("setState 1")
            sharedViewModel.moveToEstablishSecureConnection(bankIdentificationUrl = state.bankIdentificationUrl, nextStep = state.nextStep)
        } else {
            Timber.d("setState 2")
            ibanNumber!!.isEnabled = state.isIbanNumberEnabled
            termsCheckBox!!.isEnabled = state.isIbanNumberEnabled
            progressBar!!.isVisible = state.isProgressBarShown
            ibanInputErrorLabel!!.visibility = state.ibanInputErrorLabelVisibility
            submitButton!!.isEnabled = state.isSubmitButtonEnabled && termsCheckBox!!.isChecked

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
                        title = getString(state.dialogTitleId),
                        message = getString(state.dialogTitleId),
                        positiveLabel = getString(R.string.identhub_iban_verification_invalid_iban_ok_button),
                        positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) },
                        negativeLabel = if (state.retryAllowed) getString(state.dialogNegativeLabelId) else null,
                        negativeAction = if (state.retryAllowed) ({ retryInputIBan() }) else null,
                        cancelAction =
                        if (state.retryAllowed) ({ retryInputIBan() })
                        else ({ sharedViewModel.postDynamicNavigationNextStep(state.nextStep) })
                )
            }
            is SealedVerificationState.AlreadyIdentifiedSuccessfullyError,
            is SealedVerificationState.ExceedMaximumAttemptsError,
            is SealedVerificationState.GenericError
            -> {
                Timber.d("showAlert 2")
                showAlertFragment(
                        title = getString(state.dialogTitleId),
                        message = getString(state.dialogMessageId),
                        positiveLabel = getString(state.dialogPositiveLabelId),
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

    private fun updateSubmitButtonState() {
        submitButton!!.isEnabled =
            ibanNumber!!.text.toString().replace(" ", "").length >= MIN_IBAN_LENGTH
                    && termsCheckBox!!.isChecked
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
                            hideKeyboard()
                            ibanViewModel.onSubmitButtonClicked(iBan, defaultToBankId)
                        },
                        { throwable: Throwable? -> Timber.e(throwable, "Cannot valid IBAN") })
        )
        setTermsText()
    }

    private fun setTermsText() {
        val termsPartText = getString(R.string.identhub_iban_verification_terms_agreement_terms)
        val privacyPartText = getString(R.string.identhub_iban_verification_terms_agreement_privacy)
        val termsText = getString(R.string.identhub_iban_verification_terms_agreement, termsPartText, privacyPartText)
        val spanned = termsText.linkOccurrenceOf(termsPartText, termsLink)
        spanned.linkOccurrenceOf(privacyPartText, privacyLink)
        termsDisclaimer?.text = spanned
        termsDisclaimer?.movementMethod = LinkMovementMethod.getInstance()
    }

    private val ibanTextValidator = object : TextWatcher {
        private var lastChangedText: String = ""
        private var locked: Boolean = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateSubmitButtonState()
        }

        override fun afterTextChanged(s: Editable?) {
            if (locked || s == null) {
                return
            }
            locked = true
            if (lastChangedText.length > s.length) {
                var differIndex = -1
                for (index in lastChangedText.indices) {
                    if (index >= s.length || s[index] != lastChangedText[index]) {
                        differIndex = index
                        break
                    }
                }
                if (lastChangedText[differIndex] == ' ') {
                    s.delete(differIndex - 1, differIndex)
                }
            }
            var i = 0
            while (i < s.length) {
                if ((i + 1) % 5 != 0 && s[i] == ' ')  {
                    s.delete(i, i + 1)
                } else if ((i + 1) % 5 == 0 && s[i] != ' ') {
                    s.insert(i, " ")
                }
                i++
            }
            lastChangedText = s.toString()
            locked = false
        }

    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        ibanNumber!!.removeTextChangedListener(ibanTextValidator)
        ibanNumber = null
        ibanInputErrorLabel = null
        progressBar = null
        submitButton = null
        imageView = null
        termsCheckBox = null
        termsDisclaimer = null
        termsLayout = null
        noticeBulletList = null
        super.onDestroyView()
    }

    companion object {
        private const val MIN_IBAN_LENGTH = 15
        private const val privacyLink = "https://www.solarisbank.com/en/privacy-policy/"
        private const val termsLink = "https://www.solarisbank.com/en/customer-information/"
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
        override val dialogTitleId = R.string.identhub_generic_error_title
        override val dialogMessageId = R.string.identhub_generic_error_message
        override val dialogPositiveLabelId = R.string.identhub_ok_button
        override val dialogNegativeLabelId = null
    }

    class InvalidBankIdError(val nextStep: String, val retryAllowed: Boolean) :
            SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 2
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.VISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitleId = R.string.identhub_iban_verification_invalid_iban_title
        override val dialogMessageId = R.string.identhub_iban_verification_invalid_iban_message
        override val dialogPositiveLabelId = R.string.identhub_ok_button
        override val dialogNegativeLabelId = R.string.identhub_iban_verification_retry_button
    }

    class AlreadyIdentifiedSuccessfullyError: SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitleId = R.string.identhub_iban_verification_already_identified_title
        override val dialogMessageId = R.string.identhub_iban_verification_already_identified_title
        override val dialogPositiveLabelId = R.string.identhub_ok_button
        override val dialogNegativeLabelId = null
    }

    class ExceedMaximumAttemptsError: SealedVerificationState(), ErrorState {
        override val isIbanNumberEnabled = false
        override val ibanBackgroundItem = 1
        override val isProgressBarShown = false
        override val ibanInputErrorLabelVisibility = View.INVISIBLE
        override val isSubmitButtonEnabled = false
        override val dialogTitleId = R.string.identhub_iban_verification_excess_attempts_title
        override val dialogMessageId = R.string.identhub_iban_verification_excess_attempts_message
        override val dialogPositiveLabelId = R.string.identhub_ok_button
        override val dialogNegativeLabelId = null
    }
}
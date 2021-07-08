package de.solarisbank.identhub.verfication.bank

import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankIbanFragment : IdentHubFragment() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val sharedViewModel: VerificationBankViewModel by lazy<VerificationBankViewModel> { activityViewModels() }
    private val ibanViewModel: VerificationBankIbanViewModel by lazy<VerificationBankIbanViewModel> { viewModels() }

    private lateinit var ibanNumber: EditText
    private lateinit var errorMessage: TextView
    private lateinit var submitButton: TextView

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verification_bank_iban, container, false)
                .also {
                    ibanNumber = it.findViewById(R.id.ibanNumber)
                    errorMessage = it.findViewById(R.id.errorMessage)
                    submitButton = it.findViewById(R.id.submitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeVerifyResult()
    }

    private fun observeVerifyResult() {
        ibanViewModel.getVerifyResultLiveData().observe(viewLifecycleOwner, Observer { onResultVerifyIBanChanged(it) })
    }

    private fun onResultVerifyIBanChanged(state: IbanVerificationState) {
        when (state) {
            is IbanVerificationState.Loading -> {
                ibanNumber.isEnabled = false
                submitButton.isEnabled = false
            }
            is IbanVerificationState.BankSuccessful -> {
                sharedViewModel.moveToEstablishSecureConnection(state.bankIdentificationUrl, state.nextStep)
            }
            is IbanVerificationState.BankIdDialogRetry -> {
                showIbanErrorLabel()
                (requireActivity() as VerificationBankActivity)
                        .showDialog(
                                title = "Wrong IBAN",
                                message = "Unfortunately the IBAN you entered is invalid or not supported",
                                positiveLabel = "Alternative identifying",
                                negativeLabel = "Retry",
                                positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) },
                                negativeAction = { retryInputIBan() }
                        )
            }
            is IbanVerificationState.BankIdDialogAlterOnly -> {
                showIbanErrorLabel()
                (requireActivity() as VerificationBankActivity)
                        .showDialog(
                                title = "Wrong IBAN",
                                message = "Unfortunately the IBAN you entered is invalid or not supported",
                                positiveLabel = "Alternative identifying",
                                positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
                        )
            }
            is IbanVerificationState.Error -> {

            }
        }
    }

    private fun retryInputIBan() {
        Timber.d("retryInputIBan()")
        (ibanNumber.background as StateListDrawable).level = 1
        errorMessage.visibility = View.INVISIBLE
        ibanNumber.setText("")
        ibanNumber.isEnabled = true
        submitButton.isEnabled = true
    }

    private fun showIbanErrorLabel() {
        ibanNumber.isEnabled = false
        (ibanNumber.background as StateListDrawable).level = 2
        errorMessage.visibility = View.VISIBLE
    }

    private fun initViews() {
        submitButton.isEnabled = false
        ibanNumber.addTextChangedListener(ibanTextValidator)

        compositeDisposable.add(RxView.clicks(submitButton)
                .map { ibanNumber.text.toString().filter { !it.isWhitespace() } }
                .subscribe(
                        { iBan: String ->
                            (activity as VerificationBankActivity).iban = iBan
                            ibanViewModel.onSubmitButtonClicked(iBan)
                        },
                        { throwable: Throwable? -> Timber.e(throwable, "Cannot valid IBAN") })
        )
    }

    private val ibanTextValidator = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            submitButton.isEnabled = !ibanNumber.text.isNullOrEmpty() && ibanNumber.text.length >= MIN_IBAN_LENGTH
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    companion object {
        private const val MIN_IBAN_LENGTH = 15
    }
}

sealed class IbanVerificationState {
    class Loading : IbanVerificationState()
    class BankSuccessful(val bankIdentificationUrl: String?, val nextStep: String?) : IbanVerificationState()
    class Error : IbanVerificationState()
    class BankIdDialogRetry(val nextStep: String) : IbanVerificationState()
    class BankIdDialogAlterOnly(val nextStep: String): IbanVerificationState()
}
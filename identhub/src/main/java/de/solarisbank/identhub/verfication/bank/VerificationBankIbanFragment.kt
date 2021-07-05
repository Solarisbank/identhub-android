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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.router.NEXT_STEP_DIRECTION
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel.IBanState
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.network.utils.parseErrorResponseDto
import de.solarisbank.sdk.core.result.*
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
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
        observeInputsState()
        observeVerifyResult()
    }

    private fun observeVerifyResult() {
        ibanViewModel.getVerifyResultLiveData().observe(viewLifecycleOwner, Observer { onResultVerifyIBanChanged(it) })
    }

    private fun onResultVerifyIBanChanged(result: Result<String>) {
        //todo move out
        if (result.succeeded) {
            sharedViewModel.moveToEstablishSecureConnection(result.data, result.nextStep)
        } else if (result is Result.Error) {
            val type = result.type
            var errorDtoCode: String? = null
            if (result.throwable is HttpException) {
                try {
                    errorDtoCode = (result.throwable as HttpException).parseErrorResponseDto()?.errors?.get(0)?.code.toString()
                    Timber.d("errorDto: $errorDtoCode")
                } catch (e: Exception) {
                    Timber.d("Error during errorDto parsing")
                }
            }

            if (type is Type.PreconditionFailed && result.nextStep != null) {
                sharedViewModel.postDynamicNavigationNextStep(result.nextStep)
            } else if (type is Type.BadRequest && (errorDtoCode == INVALID_IBAN)) {
                sharedViewModel.postDynamicNavigationNextStep(NEXT_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination)
            } else {
                updateIBanInputState(IBanState.INVALID)
            }
        }
    }

    private fun updateIBanInputState(iBanState: IBanState) {
        (ibanNumber.background as StateListDrawable).level = iBanState.value
        errorMessage.visibility = if (iBanState === IBanState.INVALID) View.VISIBLE else View.GONE
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

    private fun observeInputsState() {
        ibanViewModel.iBanState.observe(viewLifecycleOwner, Observer { event: Event<IBanState> -> onIBanInputValidationStateChanged(event) })
    }

    private fun onIBanInputValidationStateChanged(event: Event<IBanState>) {
        val iBanState = event.content
        iBanState?.let { updateIBanInputState(it) }
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
        private const val INVALID_IBAN = "invalid_iban"

        fun newInstance(): Fragment {
            return VerificationBankIbanFragment()
        }
    }
}
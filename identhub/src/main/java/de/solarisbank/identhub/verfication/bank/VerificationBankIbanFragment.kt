package de.solarisbank.identhub.verfication.bank

import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel.IBanState
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.result.*
import de.solarisbank.sdk.core.result.Type.ResourceNotFound
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankIbanFragment : IdentHubFragment() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val sharedViewModel: VerificationBankViewModel by lazy<VerificationBankViewModel> { activityViewModels() }
    private val ibanViewModel: VerificationBankIbanViewModel by lazy<VerificationBankIbanViewModel> { viewModels() }

    private lateinit var ibanNumber: EditText
    private lateinit var errorMessage: TextView
    private lateinit var submitButton: Button

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
        if (result.succeeded) {
            sharedViewModel.moveToEstablishSecureConnection(result.data, result.nextStep)
        } else if (result is Result.Error) {
            val type = result.typ
            if (type is Type.ServerError || type is ResourceNotFound) {
                sharedViewModel.navigateToVerificationBankError()
            } else {
                updateIBanInputState(IBanState.INVALID)
            }
        }
    }

    private fun updateIBanInputState(iBanState: IBanState) {
        (ibanNumber.background as LevelListDrawable).level = iBanState.value
        errorMessage.visibility = if (iBanState === IBanState.INVALID) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        compositeDisposable.add(RxView.clicks(submitButton)
                .map { ibanNumber.text.toString().trim { it <= ' ' } }
                .subscribe(
                        { iBan: String -> ibanViewModel.onSubmitButtonClicked(iBan) },
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

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): Fragment {
            return VerificationBankIbanFragment()
        }
    }
}
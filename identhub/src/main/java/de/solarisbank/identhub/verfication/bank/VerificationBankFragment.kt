package de.solarisbank.identhub.verfication.bank

import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.databinding.FragmentVerificationBankBinding
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel.IBanState
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.result.*
import de.solarisbank.sdk.core.result.Type.ResourceNotFound
import de.solarisbank.sdk.core.view.viewBinding
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankFragment : IdentHubFragment() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val binding: FragmentVerificationBankBinding by viewBinding { FragmentVerificationBankBinding.inflate(layoutInflater) }
    private val sharedViewModel: IdentityActivityViewModel by lazy<IdentityActivityViewModel> { activityViewModels() }
    private val viewModel: VerificationBankViewModel by lazy<VerificationBankViewModel> { viewModels() }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeInputsState()
        observeVerifyResult()
    }

    private fun observeVerifyResult() {
        viewModel.getVerifyResultLiveData().observe(viewLifecycleOwner, Observer { onResultVerifyIBanChanged(it) })
    }

    private fun onResultVerifyIBanChanged(result: Result<String>) {
        if (result.succeeded) {
            val resultData = result.data
            sharedViewModel.moveToEstablishSecureConnection(resultData)
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
        (binding.ibanNumber.background as LevelListDrawable).level = iBanState.value
        binding.errorMessage.visibility = if (iBanState === IBanState.INVALID) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        compositeDisposable.add(RxView.clicks(binding.submitButton)
                .map { binding.ibanNumber.text.toString().trim { it <= ' ' } }
                .filter { viewModel.validationIBan(it) }
                .subscribe(
                        { iBan: String -> viewModel.onSubmitButtonClicked(iBan) },
                        { throwable: Throwable? -> Timber.e(throwable, "Cannot valid IBAN") })
        )
        compositeDisposable.add(RxTextView.textChangeEvents(binding.ibanNumber)
                .subscribe { textViewTextChangeEvent: TextViewTextChangeEvent -> viewModel.onIBanInputChanged(textViewTextChangeEvent.text().toString()) })
    }

    private fun observeInputsState() {
        viewModel.iBanState.observe(viewLifecycleOwner, Observer { event: Event<IBanState> -> onIBanInputValidationStateChanged(event) })
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
            return VerificationBankFragment()
        }
    }
}
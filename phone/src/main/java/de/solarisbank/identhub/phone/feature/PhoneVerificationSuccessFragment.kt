package de.solarisbank.identhub.phone.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.phone.PhoneModule
import de.solarisbank.identhub.phone.R
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import org.koin.androidx.navigation.koinNavGraphViewModel

class PhoneVerificationSuccessFragment : BaseFragment()  {

    private var submitButton: Button? = null
    private val sharedViewModel: PhoneViewModel by koinNavGraphViewModel(PhoneModule.navigationId)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_phone_verification_success, container, false)
            .also {
                submitButton = it.findViewById(R.id.submitButton)
                submitButton?.setOnClickListener {
                    sharedViewModel.onSuccessResult()
                }
            }
    }

    override fun customizeView(view: View) {
        submitButton?.customize(customization)
    }
}
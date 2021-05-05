package de.solarisbank.sdk.fourthline.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent

class SelfieResultFragment : FourthlineFragment() {

    private var imageResult: ImageView? = null
    private var submitButton: Button? = null
    private var retryButton: Button? = null

    private val viewModel: SelfieSharedViewModel by lazy<SelfieSharedViewModel> { activityViewModels() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_selfie_result, container, false)
        imageResult = view.findViewById(R.id.imageResult)
        submitButton = view.findViewById(R.id.submitButton)
        retryButton = view.findViewById(R.id.retryButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (viewModel.selfieResultBitmap != null) {
            imageResult!!.setImageBitmap(viewModel.selfieResultBitmap)
        }

        retryButton!!.setOnClickListener { viewModel.onRetakeButtonClicked() }
        submitButton!!.setOnClickListener { viewModel.onConfirmButtonClicked() }
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        imageResult = null
        submitButton = null
        retryButton = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return SelfieResultFragment()
        }
    }
}
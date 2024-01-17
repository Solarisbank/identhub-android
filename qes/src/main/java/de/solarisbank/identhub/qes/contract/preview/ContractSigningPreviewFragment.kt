package de.solarisbank.identhub.qes.contract.preview

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.qes.contract.ContractViewModel
import de.solarisbank.identhub.contract.adapter.DocumentAdapter
import de.solarisbank.identhub.qes.QESFlow
import de.solarisbank.identhub.qes.R
import de.solarisbank.sdk.R as CoreR
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.domain.model.result.*
import de.solarisbank.sdk.feature.PdfIntent
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.customization.customizeLinks
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.feature.extension.linkOccurrenceOf
import io.reactivex.disposables.Disposables
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File


class ContractSigningPreviewFragment : BaseFragment() {
    private val adapter = DocumentAdapter()
    private var clickDisposable = Disposables.disposed()
    private val sharedViewModel: ContractViewModel by koinNavGraphViewModel(QESFlow.navigationId)
    private val viewModel: ContractSigningPreviewViewModel by viewModel()

    private var titleView: TextView? = null
    private var subtitleView: TextView? = null
    private var documentsList: RecyclerView? = null
    private var submitButton: Button? = null
    private var termsLayout: View? = null
    private var termsCheckBox: CheckBox? = null
    private var termsDescription: TextView? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_contract_signing_preview, container, false)
                .also {
                    titleView = it.findViewById(R.id.title)
                    subtitleView = it.findViewById(R.id.subtitle)
                    documentsList = it.findViewById(R.id.documentsList)
                    submitButton = it.findViewById(R.id.submitButton)
                    termsLayout = it.findViewById(R.id.termsLayout)
                    termsCheckBox = it.findViewById(R.id.termsCheckBox)
                    termsDescription = it.findViewById(R.id.termsDescription)
                }
    }

    override fun customizeView(view: View) {
        submitButton?.customize(customization)
        termsDescription?.customizeLinks(customization)
        termsCheckBox?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewModels()
        observeDownloadingPdfFiles()
    }

    private fun initViews() {
        clickDisposable = adapter.actionOnClickObservable.subscribe(
                { onDocumentActionClicked(it) },
                { onDocumentActionError(it) }
        )
        documentsList!!.layoutManager = LinearLayoutManager(context)
        documentsList!!.setHasFixedSize(true)
        documentsList!!.adapter = adapter
        val isPreview = arguments?.getBoolean("isPreview") ?: true
        if (isPreview) {
            titleView?.text = getString(R.string.identhub_qes_doc_title)
            subtitleView?.text = getString(R.string.identhub_qes_doc_description)
            submitButton?.text = getString(CoreR.string.identhub_next)
        } else {
            titleView?.text = getString(R.string.identhub_contract_signing_finish_title)
            subtitleView?.text = getString(R.string.identhub_contract_signing_finish_subtitle)
            submitButton?.text = getString(R.string.identhub_contract_signing_finish_button)
        }
        submitButton!!.setOnClickListener {
            if (isPreview)
                viewModel.onAction(ContractSigningPreviewAction.Next)
            else {
                /* TODO We should somehow store what was going to happen (Success/Confirm)
                *   Before showing this screen and resume that here */
            }
        }
        termsCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                submitButton?.buttonDisabled(false)
            } else {
                submitButton?.buttonDisabled(true)
            }
        }
    }

    private fun initViewModels() {
        viewModel.state().observe(viewLifecycleOwner, ::stateUpdated)
        viewModel.events().observe(viewLifecycleOwner, ::onEvent)
        sharedViewModel.navigator = navigator
    }

    @SuppressLint("StringFormatMatches", "StringFormatInvalid")
    private fun setTermsConditionSpan() {
        val termsPartText = getString(R.string.identhub_contract_signing_preview_terms_condition_part)
        val termsText = getString(R.string.identhub_contract_signing_preview_terms_condition, termsPartText)
        val spanned = termsText.linkOccurrenceOf(termsPartText, termsLink)
        termsDescription?.text = spanned
        termsDescription?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun observeDownloadingPdfFiles() {
        viewModel.getFetchPdfFilesResultLiveData().observe(viewLifecycleOwner) {
            onDownloadedPdfFiles(it)
        }
    }

    private fun stateUpdated(state: ContractSigningPreviewState) {
        if (state.shouldShowTerms) {
            termsLayout?.visibility = View.VISIBLE
            setTermsConditionSpan()
            submitButton?.buttonDisabled(true)
        } else {
            termsLayout?.visibility = View.GONE
        }
        onDocumentResultChanged(state.documents)
    }

    private fun onEvent(event: Event<ContractSigningPreviewEvent>) {
        val content = event.content ?: return

        when (content) {
            is ContractSigningPreviewEvent.DocumentDownloaded -> {
                onPdfFileFetched(content.result)
            }
            is ContractSigningPreviewEvent.Done -> {
                sharedViewModel.onContractSigningPreviewResult()
            }
        }
    }

    private fun onDownloadedPdfFiles(result: Result<List<File>>) {
        if (result.succeeded) {
            val files = result.data!!
            val count = files.size
            val downloadedFileMessage = resources.getQuantityString(R.plurals.identhub_contract_signing_preview_downloaded_message, count, count)
            Toast.makeText(context, downloadedFileMessage, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.identhub_contract_signing_preview_download_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPdfFileFetched(result: Result<File?>) {
        if (result.succeeded) {
            PdfIntent.openFile(context, result.data)
        } else {
            Toast.makeText(context, R.string.identhub_contract_signing_preview_render_pdf_error, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onDocumentResultChanged(result: Result<List<DocumentDto>>) {
        if (result.succeeded) {
            adapter.clear()
            adapter.add(result.data)
            adapter.notifyDataSetChanged()
        } else {
            Timber.e(result.throwable, "Could not get documents data")
        }
    }

    private fun onDocumentActionError(throwable: Throwable) {
        Timber.e(throwable, "Could not open pdf file")
    }

    private fun onDocumentActionClicked(document: DocumentDto) {
        viewModel.onAction(ContractSigningPreviewAction.DownloadDocument(document))
    }

    override fun onDestroyView() {
        clickDisposable.dispose()
        documentsList = null
        submitButton = null
        titleView = null
        subtitleView= null
        super.onDestroyView()
    }

    companion object {
        const val termsLink = "https://www.solarisbank.com/en/customer-information/"
    }
}
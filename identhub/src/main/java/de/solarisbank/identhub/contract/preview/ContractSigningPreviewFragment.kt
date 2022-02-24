package de.solarisbank.identhub.contract.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.contract.ContractViewModel
import de.solarisbank.identhub.contract.adapter.DocumentAdapter
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.model.result.throwable
import de.solarisbank.sdk.feature.PdfIntent
import de.solarisbank.sdk.feature.customization.customize
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.io.File

class ContractSigningPreviewFragment : IdentHubFragment() {
    private val adapter = DocumentAdapter()
    private var clickDisposable = Disposables.disposed()
    private val sharedViewModel: ContractViewModel by lazy { activityViewModels() }
    private val viewModel: ContractSigningPreviewViewModel by lazy { viewModels() }

    private var titleView: TextView? = null
    private var subtitleView: TextView? = null
    private var documentsList: RecyclerView? = null
    private var submitButton: Button? = null

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_contract_signing_preview, container, false)
                .also {
                    titleView = it.findViewById(R.id.title)
                    subtitleView = it.findViewById(R.id.subtitle)
                    documentsList = it.findViewById(R.id.documentsList)
                    submitButton = it.findViewById(R.id.submitButton)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        submitButton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewModel.refreshIdentificationData()
        observeContracts()
        observeDownloadPdfFile()
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
            titleView?.text = getString(R.string.identhub_contract_signing_preview_title)
            subtitleView?.text = getString(R.string.identhub_contract_signing_preview_subtitle)
            submitButton?.text = getString(R.string.identhub_contract_signing_preview_send_code_action)
        } else {
            titleView?.text = getString(R.string.identhub_contract_signing_finish_title)
            subtitleView?.text = getString(R.string.identhub_contract_signing_finish_subtitle)
            submitButton?.text = getString(R.string.identhub_contract_signing_finish_button)
        }
        submitButton!!.setOnClickListener {
            if (isPreview)
                sharedViewModel.navigateToContractSigningProcess()
            else
                sharedViewModel.callOnSuccessResult()
        }
    }

    private fun observeDownloadingPdfFiles() {
        viewModel.getFetchPdfFilesResultLiveData().observe(viewLifecycleOwner) {
            onDownloadedPdfFiles(it)
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

    private fun observeDownloadPdfFile() {
        viewModel.getFetchPdfResultLiveData().observe(viewLifecycleOwner) { onPdfFileFetched(it) }
    }

    private fun onPdfFileFetched(result: Result<File?>) {
        if (result.succeeded) {
            PdfIntent.openFile(context, result.data)
        } else {
            Toast.makeText(context, R.string.identhub_contract_signing_preview_render_pdf_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeContracts() {
        viewModel.getDocumentsResultLiveData().observe(viewLifecycleOwner) { onDocumentResultChanged(it) }
    }

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
        viewModel.onDocumentActionClicked(document)
    }

    override fun onDestroyView() {
        clickDisposable.dispose()
        documentsList = null
        submitButton = null
        super.onDestroyView()
    }
}
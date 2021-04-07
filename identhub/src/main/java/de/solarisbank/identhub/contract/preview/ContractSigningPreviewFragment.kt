package de.solarisbank.identhub.contract.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.contract.adapter.DocumentAdapter
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.core.result.throwable
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.io.File

class ContractSigningPreviewFragment : IdentHubFragment() {
    private val adapter = DocumentAdapter()
    private var clickDisposable = Disposables.disposed()
    private val sharedViewModel: IdentityActivityViewModel by lazy<IdentityActivityViewModel> { activityViewModels() }
    private val viewModel: ContractSigningPreviewViewModel by lazy<ContractSigningPreviewViewModel> { viewModels() }

    private lateinit var documentsList: RecyclerView
    private lateinit var submitButton: Button
    private lateinit var downloadButton: Button

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contract_signing_preview, container, false)
                .also {
                    documentsList = it.findViewById(R.id.documentsList)
                    submitButton = it.findViewById(R.id.submitButton)
                    downloadButton = it.findViewById(R.id.downloadButton)
                }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeContracts()
        observeDownloadPdfFile()
        observeDownloadingPdfFiles()
    }

    private fun initViews() {
        clickDisposable = adapter.actionOnClickObservable.subscribe(
                { onDocumentActionClicked(it) },
                { onDocumentActionError(it) }
        )
        documentsList.layoutManager = LinearLayoutManager(context)
        documentsList.setHasFixedSize(true)
        documentsList.adapter = adapter
        submitButton.setOnClickListener { sharedViewModel.navigateToContractSigningProcess() }
        downloadButton.setOnClickListener { viewModel.onDownloadAllDocumentClicked(adapter.items) }
    }

    private fun observeDownloadingPdfFiles() {
        viewModel.getFetchPdfFilesResultLiveData().observe(viewLifecycleOwner, Observer { onDownloadedPdfFiles(it) })
    }

    private fun onDownloadedPdfFiles(result: Result<List<File>>) {
        if (result.succeeded) {
            val files = result.data!!
            val count = files.size
            val downloadedFileMessage = resources.getQuantityString(R.plurals.contract_signing_preview_downloaded_message, count, count)
            Toast.makeText(context, downloadedFileMessage, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.contract_signing_preview_download_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeDownloadPdfFile() {
        viewModel.getFetchPdfResultLiveData().observe(viewLifecycleOwner, Observer { onPdfFileFetched(it) })
    }

    private fun onPdfFileFetched(result: Result<de.solarisbank.sdk.core.Optional<File>>) {
        if (result.succeeded) {
            val file: de.solarisbank.sdk.core.Optional<File> = result.data!!
            de.solarisbank.sdk.core.PdfIntent.openFile(context, file.get())
        } else {
            Toast.makeText(context, R.string.contract_signing_preview_render_pdf_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeContracts() {
        viewModel.getDocumentsResultLiveData().observe(viewLifecycleOwner, Observer { onDocumentResultChanged(it) })
    }

    private fun onDocumentResultChanged(result: Result<List<Document>>) {
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

    private fun onDocumentActionClicked(document: Document) {
        viewModel.onDocumentActionClicked(document)
    }

    override fun onDestroyView() {
        clickDisposable.dispose()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): Fragment {
            return ContractSigningPreviewFragment()
        }
    }
}
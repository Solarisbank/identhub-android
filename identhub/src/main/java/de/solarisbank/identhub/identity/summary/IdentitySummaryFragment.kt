package de.solarisbank.identhub.identity.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.contract.ContractViewModel
import de.solarisbank.identhub.contract.adapter.SignedDocumentAdapter
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.data.model.StateUiModel
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.core.viewModels
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.io.File

class IdentitySummaryFragment : IdentHubFragment() {
    private var clickDisposable = Disposables.disposed()
    private val adapter = SignedDocumentAdapter()
    private val viewModel: IdentitySummaryFragmentViewModel by lazy<IdentitySummaryFragmentViewModel> { viewModels() }
    private val sharedViewModel: ContractViewModel by lazy<ContractViewModel> { activityViewModels() }

    private lateinit var documentsList: RecyclerView
    private lateinit var downloadButton: Button
    private lateinit var submitButton: TextView

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_identity_summary, container, false)
                .also {
                    documentsList = it.findViewById(R.id.documentsList)
                    downloadButton = it.findViewById(R.id.downloadButton)
                    submitButton = it.findViewById(R.id.submitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeFetchingPdfFile()
        observeFetchingPdfFiles()
        observeDocumentsResult()
    }

    private fun initViews() {
        clickDisposable = adapter.actionOnClickObservable.subscribe({ document: Document -> onDocumentActionClicked(document) }) { throwable: Throwable -> onDocumentActionError(throwable) }
        documentsList.layoutManager = LinearLayoutManager(context)
        documentsList.setHasFixedSize(true)
        documentsList.adapter = adapter
        viewModel.getUiState().observe(viewLifecycleOwner, { onIdentificationStatusChanged(it) })
        downloadButton.setOnClickListener { viewModel.onDownloadAllDocumentClicked(adapter.items) }
        submitButton.setOnClickListener { viewModel.onSubmitButtonClicked() }
        //todo too many observer; should be refactored with state pattern
    }

    private fun onIdentificationStatusChanged(model: StateUiModel<Bundle?>) {
        Timber.d("onIdentificationStatusChanged, model : ${model.data}")
        sharedViewModel.sendResult(model.data)
    }

    private fun onDocumentActionError(throwable: Throwable) {}

    private fun onDocumentActionClicked(document: Document) {
        viewModel.onDocumentActionClicked(document)
    }

    private fun observeDocumentsResult() {
        viewModel.getDocumentsResultLiveData().observe(viewLifecycleOwner, Observer { result: Result<List<Document>> -> onSummaryResultChanged(result) })
    }

    private fun onSummaryResultChanged(result: Result<List<Document>>) {
        if (result.succeeded) {
            adapter.clear()
            adapter.add(result.data)
            adapter.notifyDataSetChanged()
        } else {

        }
    }

    private fun observeFetchingPdfFile() {
        viewModel.getFetchPdfResultLiveData().observe(viewLifecycleOwner, Observer { onPdfFileFetched(it) })
    }

    private fun observeFetchingPdfFiles() {
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

    private fun onPdfFileFetched(result: Result<de.solarisbank.sdk.core.Optional<File>>) {
        if (result.succeeded) {
            val file: de.solarisbank.sdk.core.Optional<File> = result.data!!
            de.solarisbank.sdk.core.PdfIntent.openFile(context, file.get())
        } else {
            Toast.makeText(context, R.string.contract_signing_preview_render_pdf_error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        clickDisposable.dispose()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): Fragment {
            return IdentitySummaryFragment()
        }
    }
}
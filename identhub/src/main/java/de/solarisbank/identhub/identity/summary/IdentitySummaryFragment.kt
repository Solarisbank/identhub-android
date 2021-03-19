package de.solarisbank.identhub.identity.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.*
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.contract.adapter.SignedDocumentAdapter
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.databinding.FragmentIdentitySummaryBinding
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.shared.result.Result
import de.solarisbank.shared.result.data
import de.solarisbank.shared.result.succeeded
import io.reactivex.disposables.Disposables
import java.io.File

class IdentitySummaryFragment : BaseFragment() {
    private var clickDisposable = Disposables.disposed()
    private val adapter = SignedDocumentAdapter()
    private val binding: FragmentIdentitySummaryBinding by viewBinding(FragmentIdentitySummaryBinding::inflate)
    private val viewModel: IdentitySummaryFragmentViewModel by lazy { viewModels() }
    private val sharedViewModel: IdentitySummaryViewModel by lazy { activityViewModels() }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
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
        binding.run {
            documentsList.layoutManager = LinearLayoutManager(context)
            documentsList.setHasFixedSize(true)
            documentsList.adapter = adapter
            downloadButton.setOnClickListener { viewModel.onDownloadAllDocumentClicked(adapter.items) }
            submitButton.setOnClickListener { sharedViewModel.onSubmitButtonClicked() }
        }
    }

    private fun onDocumentActionError(throwable: Throwable) {}

    private fun onDocumentActionClicked(document: Document) {
        viewModel.onDocumentActionClicked(document)
    }

    private fun observeDocumentsResult() {
        viewModel.getDocumentsResultLiveData().observe(viewLifecycleOwner, { result: Result<List<Document>> -> onSummaryResultChanged(result) })
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
        viewModel.getFetchPdfResultLiveData().observe(viewLifecycleOwner, { onPdfFileFetched(it) })
    }

    private fun observeFetchingPdfFiles() {
        viewModel.getFetchPdfFilesResultLiveData().observe(viewLifecycleOwner, { onDownloadedPdfFiles(it) })
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

    private fun onPdfFileFetched(result: Result<Optional<File>>) {
        if (result.succeeded) {
            val file: Optional<File> = result.data!!
            PdfIntent.openFile(context, file.get())
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
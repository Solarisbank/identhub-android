package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.fourthline.R
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.data.dto.AppliedDocument
import de.solarisbank.sdk.fourthline.data.dto.asString

class DocTypeAdapter(private val customization: Customization,
                     private val isDocTypeDelectedCallback: ((AppliedDocument?) -> Unit)) : RecyclerView.Adapter<DocTypeAdapter.DocTypeViewHolder>() {

    private val documentTypes: MutableList<AppliedDocument> = mutableListOf()
    private var selectedDocumentTypeIndex = -1

    fun getSelectedDocType(): AppliedDocument {
        return documentTypes[selectedDocumentTypeIndex]
    }

    inner class DocTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val typeRadioButton: RadioButton = view.findViewById(R.id.typeRadiobutton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocTypeViewHolder {
        return DocTypeViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.identhub_item_doc_type, parent, false))
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: DocTypeViewHolder, position: Int) {
        val documentType: AppliedDocument = documentTypes[position]
        holder.textView.text = documentType.asString(holder.textView.context)
        holder.typeRadioButton.customize(customization)
        holder.typeRadioButton.isChecked = position == selectedDocumentTypeIndex
        holder.typeRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isDocTypeDelectedCallback(documentType)
                if (selectedDocumentTypeIndex != -1) {
                    notifyItemChanged(selectedDocumentTypeIndex)
                }
                selectedDocumentTypeIndex = position
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return documentTypes.size
    }

    fun add(docTypes: Collection<AppliedDocument>) {
        documentTypes.clear()
        documentTypes.addAll(docTypes)
    }
}



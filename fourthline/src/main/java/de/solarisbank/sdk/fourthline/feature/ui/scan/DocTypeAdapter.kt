package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.data.entity.asString

class DocTypeAdapter(private val isDocTypeDelectedCallback: ((AppliedDocument?) -> Unit)) : RecyclerView.Adapter<DocTypeAdapter.DocTypeViewHolder>() {

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
        return DocTypeViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.view_id_doc_type_holder, parent, false))
    }

    override fun onBindViewHolder(holder: DocTypeViewHolder, position: Int) {
        val documentType: AppliedDocument = documentTypes[position]
        holder.textView.text = documentType.asString(holder.textView.context)
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



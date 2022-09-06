package de.solarisbank.identhub.contract.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.qes.R
import de.solarisbank.sdk.data.dto.DocumentDto

class DocumentViewHolder(holderView: View) : RecyclerView.ViewHolder(holderView) {

    private val documentLayout: View = holderView.findViewById(R.id.viewDocumentLayout)
    private val downloadLayout: View = holderView.findViewById(R.id.downloadLayout)
    private val documentNameText: TextView = holderView.findViewById(R.id.documentNameText)

    fun bind(document: DocumentDto, clickListener: View.OnClickListener?) {
        documentNameText.text = document.name
        documentLayout.setOnClickListener(clickListener)
        downloadLayout.setOnClickListener(clickListener)
    }
}
package de.solarisbank.identhub.contract.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.R

class DocumentViewHolder(holderView: View) : RecyclerView.ViewHolder(holderView) {

    private val documentLayout: View = holderView.findViewById(R.id.viewDocumentLayout)
    private val downloadLayout: View = holderView.findViewById(R.id.downloadLayout)

    fun bindAction(clickListener: View.OnClickListener?) {
        documentLayout.setOnClickListener(clickListener)
        downloadLayout.setOnClickListener(clickListener)
    }
}
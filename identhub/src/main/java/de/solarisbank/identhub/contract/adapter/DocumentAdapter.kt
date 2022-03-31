package de.solarisbank.identhub.contract.adapter

import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.sdk.data.dto.DocumentDto
import com.jakewharton.rxrelay2.PublishRelay
import android.view.ViewGroup
import android.view.LayoutInflater
import de.solarisbank.identhub.R
import io.reactivex.Observable
import java.util.ArrayList

class DocumentAdapter : RecyclerView.Adapter<DocumentViewHolder>() {
    private val documents: MutableList<DocumentDto> = ArrayList()
    private val actionClickRelay = PublishRelay.create<DocumentDto>()
    val actionOnClickObservable: Observable<DocumentDto>
        get() = actionClickRelay.hide()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val holderView = LayoutInflater.from(parent.context)
            .inflate(R.layout.identhub_item_document, parent, false)
        return DocumentViewHolder(holderView)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document) { actionClickRelay.accept(document) }
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    fun clear() {
        documents.clear()
    }

    fun add(documents: List<DocumentDto>?) {
        this.documents.addAll(documents!!)
    }
}
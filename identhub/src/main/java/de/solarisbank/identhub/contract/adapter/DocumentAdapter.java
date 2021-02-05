package de.solarisbank.identhub.contract.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.solarisbank.identhub.databinding.ItemDocumentBinding;
import de.solarisbank.identhub.contract.model.Document;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> {
    private List<Document> documents = new ArrayList<>();

    public DocumentAdapter() {
        super();
        documents.add(new Document("doc1.pdf", "Doc 1", ""));
        documents.add(new Document("doc2.pdf", "Doc 2", ""));
        documents.add(new Document("doc3.pdf", "Doc 3", ""));
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        holder.bind(documents.get(position));
        holder.bindAction(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }
}

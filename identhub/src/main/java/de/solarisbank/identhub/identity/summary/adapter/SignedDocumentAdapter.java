package de.solarisbank.identhub.identity.summary.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.solarisbank.identhub.databinding.ItemDocumentBinding;
import de.solarisbank.identhub.identity.summary.model.SignedDocument;

public class SignedDocumentAdapter extends RecyclerView.Adapter<SignedDocumentViewHolder> {
    private final List<SignedDocument> signedDocuments = new ArrayList<>();

    public SignedDocumentAdapter() {
        super();
        signedDocuments.add(new SignedDocument("SIGNED_doc1.pdf", "Doc 1", ""));
        signedDocuments.add(new SignedDocument("SIGNED_doc2.pdf", "Doc 2", ""));
        signedDocuments.add(new SignedDocument("SIGNED_doc3.pdf", "Doc 3", ""));
    }

    @NonNull
    @Override
    public SignedDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SignedDocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SignedDocumentViewHolder holder, int position) {
        holder.bind(signedDocuments.get(position));
        holder.bindAction(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return signedDocuments.size();
    }
}

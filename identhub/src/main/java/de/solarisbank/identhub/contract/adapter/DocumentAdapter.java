package de.solarisbank.identhub.contract.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.List;

import de.solarisbank.identhub.data.entity.Document;
import de.solarisbank.identhub.databinding.ItemDocumentBinding;
import io.reactivex.Observable;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> {
    private List<Document> documents = new ArrayList<>();

    private PublishRelay<Document> actionClickRelay = PublishRelay.create();

    public Observable<Document> getActionOnClickObservable() {
        return actionClickRelay.hide();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.bind(document);
        holder.bindAction(view -> {
            actionClickRelay.accept(document);
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void clear() {
        this.documents.clear();
    }

    public void add(List<Document> documents) {
        this.documents.addAll(documents);
    }

    public List<Document> getItems() {
        return documents;
    }
}

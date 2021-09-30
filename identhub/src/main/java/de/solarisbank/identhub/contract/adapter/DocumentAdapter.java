package de.solarisbank.identhub.contract.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.List;

import de.solarisbank.identhub.R;
import de.solarisbank.sdk.data.dto.DocumentDto;
import io.reactivex.Observable;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> {
    private List<DocumentDto> documents = new ArrayList<>();

    private PublishRelay<DocumentDto> actionClickRelay = PublishRelay.create();

    public Observable<DocumentDto> getActionOnClickObservable() {
        return actionClickRelay.hide();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentDto document = documents.get(position);
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

    public void add(List<DocumentDto> documents) {
        this.documents.addAll(documents);
    }

    public List<DocumentDto> getItems() {
        return documents;
    }
}

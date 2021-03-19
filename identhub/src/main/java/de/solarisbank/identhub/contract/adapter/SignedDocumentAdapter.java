package de.solarisbank.identhub.contract.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import de.solarisbank.identhub.databinding.ItemDocumentBinding;

public class SignedDocumentAdapter extends DocumentAdapter {

    @NonNull
    @Override
    public SignedDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SignedDocumentViewHolder(binding);
    }
}

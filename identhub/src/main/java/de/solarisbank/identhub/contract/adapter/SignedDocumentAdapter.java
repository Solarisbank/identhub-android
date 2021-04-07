package de.solarisbank.identhub.contract.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import de.solarisbank.identhub.R;

public class SignedDocumentAdapter extends DocumentAdapter {

    @NonNull
    @Override
    public SignedDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new SignedDocumentViewHolder(holderView);
    }
}

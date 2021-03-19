package de.solarisbank.identhub.contract.adapter;

import android.content.res.ColorStateList;

import androidx.core.content.ContextCompat;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.data.entity.Document;
import de.solarisbank.identhub.databinding.ItemDocumentBinding;

public class SignedDocumentViewHolder extends DocumentViewHolder {
    public SignedDocumentViewHolder(ItemDocumentBinding binding) {
        super(binding);
    }

    @Override
    public void bind(Document signedDocument) {
        binding.imageView.setImageLevel(1);
        binding.imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.success)));
        binding.title.setText(signedDocument.getDocumentType());
        binding.subtitle.setText(signedDocument.getName());
    }
}

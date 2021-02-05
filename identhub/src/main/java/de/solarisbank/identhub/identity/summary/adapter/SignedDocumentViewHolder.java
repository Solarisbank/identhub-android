package de.solarisbank.identhub.identity.summary.adapter;

import android.content.res.ColorStateList;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.ItemDocumentBinding;
import de.solarisbank.identhub.identity.summary.model.SignedDocument;

public class SignedDocumentViewHolder extends RecyclerView.ViewHolder {
    private ItemDocumentBinding binding;

    public SignedDocumentViewHolder(ItemDocumentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(SignedDocument signedDocument) {
        binding.imageView.setImageLevel(1);
        binding.imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.success)));
        binding.title.setText(signedDocument.getLabel());
        binding.subtitle.setText(signedDocument.getFileName());
    }

    public void bindAction(View.OnClickListener clickListener) {
        binding.previewButton.setOnClickListener(clickListener);
    }
}

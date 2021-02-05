package de.solarisbank.identhub.contract.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import de.solarisbank.identhub.contract.model.Document;
import de.solarisbank.identhub.databinding.ItemDocumentBinding;

public class DocumentViewHolder extends RecyclerView.ViewHolder {
    private ItemDocumentBinding binding;

    public DocumentViewHolder(ItemDocumentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Document document) {
        binding.imageView.setImageLevel(0);
        binding.title.setText(document.getLabel());
        binding.subtitle.setText(document.getFileName());
    }

    public void bindAction(View.OnClickListener clickListener) {
        binding.previewButton.setOnClickListener(clickListener);
    }
}

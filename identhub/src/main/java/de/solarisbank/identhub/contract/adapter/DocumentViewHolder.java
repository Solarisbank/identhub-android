package de.solarisbank.identhub.contract.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import de.solarisbank.identhub.data.entity.Document;
import de.solarisbank.identhub.databinding.ItemDocumentBinding;

public class DocumentViewHolder extends RecyclerView.ViewHolder {
    protected ItemDocumentBinding binding;

    public DocumentViewHolder(ItemDocumentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Document document) {
        binding.imageView.setImageLevel(0);
        binding.title.setText(document.getDocumentType());
        binding.subtitle.setText(document.getName());
    }

    public void bindAction(View.OnClickListener clickListener) {
        binding.previewButton.setOnClickListener(clickListener);
    }
}

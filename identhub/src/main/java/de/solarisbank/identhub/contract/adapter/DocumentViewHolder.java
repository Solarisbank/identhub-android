package de.solarisbank.identhub.contract.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.data.entity.Document;

public class DocumentViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final Button previewButton;

    public DocumentViewHolder(View holderView) {
        super(holderView);
        imageView = holderView.findViewById(R.id.imageView);
        title = holderView.findViewById(R.id.title);
        subtitle = holderView.findViewById(R.id.subtitle);
        previewButton = holderView.findViewById(R.id.previewButton);
    }

    public void bind(Document document) {
        imageView.setImageLevel(0);
        title.setText(document.getDocumentType());
        subtitle.setText(document.getName());
    }

    public void bindAction(View.OnClickListener clickListener) {
        previewButton.setOnClickListener(clickListener);
    }
}

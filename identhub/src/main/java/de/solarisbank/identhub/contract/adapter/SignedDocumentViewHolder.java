package de.solarisbank.identhub.contract.adapter;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.data.entity.Document;

public class SignedDocumentViewHolder extends DocumentViewHolder {

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final Button previewButton;

    public SignedDocumentViewHolder(View holderView) {
        super(holderView);
        imageView = holderView.findViewById(R.id.imageView);
        title = holderView.findViewById(R.id.title);
        subtitle = holderView.findViewById(R.id.subtitle);
        previewButton = holderView.findViewById(R.id.previewButton);
    }

    @Override
    public void bind(Document signedDocument) {
        imageView.setImageLevel(1);
        imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.ident_hub_color_success)));
        title.setText(signedDocument.getDocumentType());
        subtitle.setText(signedDocument.getName());
    }
}

package de.solarisbank.identhub.contract.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.data.entity.Document;

public class DocumentViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;

    public DocumentViewHolder(View holderView) {
        super(holderView);
        title = holderView.findViewById(R.id.title);
    }

    public void bind(Document document) {
        SpannableString spannableString = SpannableString.valueOf(document.getDocumentType());
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        title.setText(spannableString);
    }

    public void bindAction(View.OnClickListener clickListener) {
        title.setOnClickListener(clickListener);
    }
}

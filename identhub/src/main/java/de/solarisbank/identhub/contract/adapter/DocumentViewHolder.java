package de.solarisbank.identhub.contract.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.solarisbank.identhub.R;
import de.solarisbank.sdk.data.entity.Document;

public class DocumentViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;

    public DocumentViewHolder(View holderView) {
        super(holderView);
        title = holderView.findViewById(R.id.title);
    }

    public void bind(Document document) {
        SpannableString spannableString = SpannableString.valueOf(getDocTypeString(document.getDocumentType()));
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        title.setText(spannableString);
    }

    public void bindAction(View.OnClickListener clickListener) {
        title.setOnClickListener(clickListener);
    }

    private String getDocTypeString(String documentTypeEnum) {
        Context context = title.getContext();
        switch (documentTypeEnum) {
            case "QES_DOCUMENT":
                return context.getString(R.string.qes_document);
            case "LOAN_MANDATE_CONTRACT":
                return context.getString(R.string.loan_mandate_contract);
            case "SIGNED_QES_DOCUMENT":
                return context.getString(R.string.signed_qes_document);
            case "SIGNED_LOAN_MANDATE_CONTRACT":
                return context.getString(R.string.signed_loan_mandate_contract);
            default:
                return documentTypeEnum;
        }
    }
}

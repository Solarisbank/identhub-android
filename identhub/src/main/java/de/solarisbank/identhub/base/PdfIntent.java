package de.solarisbank.identhub.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class PdfIntent {
    public static void openFile(Context context, File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(context, "de.solarisbank.identhub.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        pdfIntent.setDataAndType(pdfUri, "application/pdf");
        context.startActivity(pdfIntent);
    }
}

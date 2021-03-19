package de.solarisbank.identhub.file;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import de.solarisbank.identhub.base.Optional;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileController {
    private final String pdfDirectoryPath;

    public FileController(Context context) {
        pdfDirectoryPath = context.getFilesDir() + "/pdf";
    }

    public Optional<File> save(String fileName, BufferedSource bufferedSource) throws IOException {
        File pdfDirectory = new File(pdfDirectoryPath);
        pdfDirectory.mkdirs();

        File destinationFile = new File(pdfDirectoryPath, fileName);
        try (BufferedSink sink = Okio.buffer(Okio.sink(destinationFile))) {
            sink.writeAll(bufferedSource);
            return Optional.of(destinationFile);
        } catch (IOException io) {
            return Optional.empty();
        }
    }

    public Optional<File> get(String fileName) {
        File destinationFile = new File(pdfDirectoryPath, fileName);
        if (destinationFile.exists()) {
            return Optional.of(destinationFile);
        }
        return Optional.empty();
    }
}

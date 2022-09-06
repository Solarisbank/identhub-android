package de.solarisbank.identhub.qes.domain;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

public class OkioKotlinAdapter {

    /**
     * okio library inside okhttp uses incompatible version of kotlin compiler
     * to solve this java class is used
     * @param bufferedSource
     * @param destinationFile
     * @return
     */
    public static File fillFileIn(BufferedSource bufferedSource, File destinationFile) {
        try (BufferedSink sink = Okio.buffer(Okio.sink(destinationFile))) {
            sink.writeAll(bufferedSource);
            return destinationFile;
        } catch (IOException e) {
            Timber.e(e, "fillFileIn");
            return null;
        }
    }
}

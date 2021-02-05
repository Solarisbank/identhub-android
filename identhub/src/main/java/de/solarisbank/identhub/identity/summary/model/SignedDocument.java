package de.solarisbank.identhub.identity.summary.model;

public class SignedDocument {
    private final String fileName;
    private final String label;
    private final String link;

    public SignedDocument(String fileName, String label, String link) {
        this.fileName = fileName;
        this.label = label;
        this.link = link;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLabel() {
        return label;
    }

    public String getLink() {
        return link;
    }
}

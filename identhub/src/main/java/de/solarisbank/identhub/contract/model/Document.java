package de.solarisbank.identhub.contract.model;

public class Document {
    private final String fileName;
    private final String label;
    private final String link;

    public Document(String fileName, String label, String link) {
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

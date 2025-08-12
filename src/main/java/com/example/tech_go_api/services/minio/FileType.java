package com.example.tech_go_api.services.minio;

public enum FileType {
    LOGO("logos"),
    DOCUMENT("documents"),
    IMAGE("images"),
    OTHER("others");

    private final String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    public static FileType fromMimeType(String mimeType) {
        if (mimeType == null) {
            return OTHER;
        }
        
        if (mimeType.startsWith("image/")) {
            return mimeType.endsWith("svg+xml") ? LOGO : IMAGE;
        }
        
        if (mimeType.startsWith("application/")) {
            return DOCUMENT;
        }
        
        return OTHER;
    }
}

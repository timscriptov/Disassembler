package com.nbsp.materialfilepicker.utils;

import android.webkit.MimeTypeMap;

import com.nbsp.materialfilepicker.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FileTypeUtils {
    private static Map<String, FileType> fileTypeExtensions = new HashMap<>();

    static {
        for (FileType fileType : FileType.values()) {
            for (String extension : fileType.getExtensions()) {
                fileTypeExtensions.put(extension, fileType);
            }
        }
    }

    public static FileType getFileType(@NotNull File file) {
        if (file.isDirectory()) {
            return FileType.DIRECTORY;
        }

        FileType fileType = fileTypeExtensions.get(getExtension(file.getName()));
        if (fileType != null) {
            return fileType;
        }

        return FileType.DOCUMENT;
    }

    @NotNull
    public static String getExtension(String fileName) {
        String encoded;
        try {
            encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = fileName;
        }
        return MimeTypeMap.getFileExtensionFromUrl(encoded).toLowerCase();
    }

    public enum FileType {
        DIRECTORY(R.drawable.ic_folder, R.string.type_directory),
        DOCUMENT(R.drawable.ic_file, R.string.type_document),
        //CERTIFICATE(R.drawable.ic_certificate, R.string.type_certificate, "cer", "der", "pfx", "p12", "arm", "pem"),
        //DRAWING(R.drawable.ic_drawing, R.string.type_drawing, "ai", "cdr", "dfx", "eps", "svg", "stl", "wmf", "emf", "art", "xar"),
        //EXCEL(R.drawable.ic_excel, R.string.type_excel, "xls", "xlk", "xlsb", "xlsm", "xlsx", "xlr", "xltm", "xlw", "numbers", "ods", "ots"),
        IMAGE(R.drawable.ic_image, R.string.type_image, "bmp", "gif", "ico", "jpeg", "jpg", "pcx", "png", "psd", "tga", "tiff", "tif", "xcf"),
        MUSIC(R.drawable.ic_audio, R.string.type_audio, "aiff", "aif", "wav", "flac", "m4a", "wma", "amr", "mp2", "mp3", "wma", "aac", "mid", "m3u"),
        VIDEO(R.drawable.ic_video, R.string.type_video, "avi", "mov", "wmv", "mkv", "3gp", "f4v", "flv", "mp4", "mpeg", "webm"),
        //PDF(R.drawable.ic_pdf, R.string.type_pdf, "pdf"),
        //POWER_POINT(R.drawable.ic_powerpoint, R.string.type_power_point, "pptx", "keynote", "ppt", "pps", "pot", "odp", "otp"),
        //WORD(R.drawable.ic_word, R.string.type_word, "doc", "docm", "docx", "dot", "mcw", "rtf", "pages", "odt", "ott"),
        ARCHIVE(R.drawable.ic_archive, R.string.type_archive, "cab", "7z", "alz", "arj", "bzip2", "bz2", "dmg", "gzip", "gz", "jar", "lz", "lzip", "lzma", "zip", "rar", "tar", "tgz"),
        APK(R.drawable.ic_apk, R.string.type_apk, "apk"),
        TXT(R.drawable.ic_text, R.string.type_text, "txt", "text", "java", "css", "c", "cpp", "h", "hpp", "kt", "js", "php", "lua", "mk", "xml", "bat", "properties", "gradle", "md", "gitignore", "json", "conf", "prop", "log", "py", "smali", "cfg", "ini", "mf", "mtd"),
        HTML(R.drawable.ic_html, R.string.type_html, "htm", "html");

        private int icon;
        private int description;
        private String[] extensions;

        FileType(int icon, int description, String... extensions) {
            this.icon = icon;
            this.description = description;
            this.extensions = extensions;
        }

        public String[] getExtensions() {
            return extensions;
        }

        public int getIcon() {
            return icon;
        }

        public int getDescription() {
            return description;
        }
    }
}
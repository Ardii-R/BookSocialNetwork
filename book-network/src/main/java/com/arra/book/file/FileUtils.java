package com.arra.book.file;


import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {

    public static byte[] readFileFromLocation(String bookCoverFileUrl) {
        // check bookCoverFileUrl
        if (StringUtils.isBlank(bookCoverFileUrl)) {
            return null;
        }
        try{
            Path fileParth = new File(bookCoverFileUrl).toPath();
            return Files.readAllBytes(fileParth);
        }catch (IOException e){
            log.warn("No File found for the url: " + bookCoverFileUrl);
        }
        return null;
    }
}

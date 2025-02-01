package com.arra.book.file;

import com.arra.book.book.Book;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;                    // defines the location path of the photos

    // max-file size is 50MB (defined in application configuration)
    public String saveFile(@NonNull MultipartFile file, @NonNull Integer userId) {
        // subfile for users
        final String fileUploadSubPath = "users" + File.separator + userId;

        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile file, @NonNull String fileUploadSubPath) {

        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        // check if folder exists
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();  // generate directory with all sub directories
            if(!folderCreated){              // check if creation of folder succeeded
                log.warn("Folder creation failed!");
                return null;
            }
        }
        // extract file extension
        final String fileExtension = getFileExtension(file.getOriginalFilename());
        // generate the final file name
        String targetFilePath = finalUploadPath +  File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try{
            Files.write(targetPath, file.getBytes());
            log.info("Saved book cover file to: " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Something went wrong. Saving the cover file is not working!" + e);
        }

        return null;
    }

    private String getFileExtension(String filename) {
        // checks if file is empty or null
        if (filename == null || filename.isEmpty()){
            return "";
        }
        // get the index of the files last dot
        int indexLastDot = filename.lastIndexOf('.');
        // check if extension exists
        if(indexLastDot == -1){
            return "";
        }
        // return the extension
        return filename.substring(indexLastDot +1 ).toLowerCase();
    }
}

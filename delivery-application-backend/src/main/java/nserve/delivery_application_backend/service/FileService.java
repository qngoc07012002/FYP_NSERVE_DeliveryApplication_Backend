package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.FileResponse;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class FileService {

    CloudinaryService cloudinaryService;

    public FileResponse uploadFile(MultipartFile file) {
        String uploadDir = "src/main/resources/static/images/";
        //String uploadDir = "uploads/";
        if (!isImageFile(file)) {
            throw new AppException(ErrorCode.FILE_TYPE_INVALID);
        }

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            File serverFile = new File(dir.getAbsolutePath() + File.separator + newFileName);
            BufferedOutputStream stream = new BufferedOutputStream(new java.io.FileOutputStream(serverFile));
            stream.write(file.getBytes());
            stream.close();
            return new FileResponse("/images/" + serverFile.getName());
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        log.info("Content type: " + contentType);
        return contentType != null && (contentType.startsWith("image/"));
    }

    public FileResponse uploadImage(MultipartFile file) {
        if (!isImageFile(file)) {
            throw new AppException(ErrorCode.FILE_TYPE_INVALID);
        }

        try {
            String imgUrl = cloudinaryService.uploadImage(file);
            return new FileResponse(imgUrl);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}

package nserve.delivery_application_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class FileController {
    FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("File upload request received");
        return ApiResponse.<String>builder()
                .code(1000)
                .message("File uploaded successfully")
                .result(fileService.uploadFile(file).getPath())
                .build();
    }
}

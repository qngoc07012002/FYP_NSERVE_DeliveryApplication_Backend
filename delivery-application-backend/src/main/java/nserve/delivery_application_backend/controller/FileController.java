package nserve.delivery_application_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.FileResponse;
import nserve.delivery_application_backend.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class FileController {
    FileService fileService;

//    @PostMapping("/upload")
//    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        log.info("File upload request received");
//        return ApiResponse.<String>builder()
//                .code(1000)
//                .message("File uploaded successfully")
//                .result(fileService.uploadFile(file).getPath())
//                .build();
//    }

    @PostMapping("/upload")
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("File upload request received");
        return ApiResponse.<FileResponse>builder()
                .code(1000)
                .message("File uploaded successfully")
                .result(fileService.uploadImage(file))
                .build();
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<FileSystemResource> getImage(@PathVariable String filename) {
        File file = new File("uploads/" + filename);
        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}

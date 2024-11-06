package nserve.delivery_application_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file)  {
        try{
            Map data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());

            String fullUrl = data.get("secure_url").toString();

            String relativeUrl = fullUrl.substring(fullUrl.indexOf("/image/upload"));

            return relativeUrl;
        }catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }
}

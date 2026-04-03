package vn.kurisu.mentormatch.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", UUID.randomUUID().toString(),
                    "folder", "mentormatch/posts"
            ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public void deleteImage(String secureUrl) {
        try {
            // Extract public_id from URL: e.g. https://res.cloudinary.com/.../mentormatch/posts/asdfgh.jpg -> mentormatch/posts/asdfgh
            String[] parts = secureUrl.split("/");
            String publicIdWithExtension = parts[parts.length - 2] + "/" + parts[parts.length - 1];
            String publicId = publicIdWithExtension.substring(0, publicIdWithExtension.lastIndexOf('.'));
            
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Log issue, but typically don't fail business logic if cleanup fails
            e.printStackTrace();
        }
    }
}

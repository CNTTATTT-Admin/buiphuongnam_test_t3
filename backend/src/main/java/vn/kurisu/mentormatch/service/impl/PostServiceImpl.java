package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.request.PostCreationRequest;
import vn.kurisu.mentormatch.dto.request.PostUpdateRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.PostResponse;
import vn.kurisu.mentormatch.entity.Post;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.PostRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.CloudinaryService;
import vn.kurisu.mentormatch.service.PostService;
import vn.kurisu.mentormatch.entity.PostImage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userName = authentication.getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private void verifyAuthorOrAdmin(Post post, User currentUser) {
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isAdmin && !post.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public ApiResponse<PostResponse> create(PostCreationRequest request) {
        User currentUser = getAuthenticatedUser();

        Post post = Post.builder()
                .content(request.getContent())
                .user(currentUser)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            int order = 0;
            for (String url : request.getImageUrls()) {
                PostImage postImage = PostImage.builder()
                        .post(post)
                        .imageUrl(url)
                        .displayOrder(order++)
                        .build();
                post.getImages().add(postImage);
            }
        }

        post = postRepository.save(post);

        return ApiResponse.<PostResponse>builder().result(toPostResponse(post)).build();
    }

    @Override
    public ApiResponse<List<PostResponse>> getAll() {
        List<PostResponse> posts = postRepository.findAll().stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<PostResponse>>builder().result(posts).build();
    }

    @Override
    public ApiResponse<PostResponse> getById(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return ApiResponse.<PostResponse>builder().result(toPostResponse(post)).build();
    }

    @Override
    public ApiResponse<PostResponse> update(Integer id, PostUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User currentUser = getAuthenticatedUser();
        verifyAuthorOrAdmin(post, currentUser);

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            // Delete old images from Cloudinary
            if (post.getImages() != null) {
                for (PostImage image : post.getImages()) {
                    cloudinaryService.deleteImage(image.getImageUrl());
                }
                post.getImages().clear();
            }
            
            // Assign new images
            int order = 0;
            for (String url : request.getImageUrls()) {
                PostImage postImage = PostImage.builder()
                        .post(post)
                        .imageUrl(url)
                        .displayOrder(order++)
                        .build();
                post.getImages().add(postImage);
            }
        }

        post = postRepository.save(post);

        return ApiResponse.<PostResponse>builder().result(toPostResponse(post)).build();
    }

    @Override
    public ApiResponse<Void> delete(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User currentUser = getAuthenticatedUser();
        verifyAuthorOrAdmin(post, currentUser);

        if (post.getImages() != null) {
            for (PostImage image : post.getImages()) {
                cloudinaryService.deleteImage(image.getImageUrl());
            }
        }

        postRepository.delete(post);

        return ApiResponse.<Void>builder().message("Post deleted successfully").build();
    }

    private PostResponse toPostResponse(Post post) {
        List<String> imageUrls = post.getImages() != null ? post.getImages().stream()
                .sorted(Comparator.comparing(PostImage::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList()) : Collections.emptyList();

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .authorName(post.getUser().getFullName())
                .authorAvatarUrl(post.getUser().getAvatarUrl())
                .content(post.getContent())
                .imageUrls(imageUrls)
                .createdAt(post.getCreatedAt())
                .build();
    }
}

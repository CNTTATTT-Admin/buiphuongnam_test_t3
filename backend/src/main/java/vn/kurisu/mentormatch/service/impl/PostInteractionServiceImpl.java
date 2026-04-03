package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.CommentRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.CommentResponse;
import vn.kurisu.mentormatch.entity.Comment;
import vn.kurisu.mentormatch.entity.Post;
import vn.kurisu.mentormatch.entity.PostLike;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.repository.CommentRepository;
import vn.kurisu.mentormatch.repository.PostLikeRepository;
import vn.kurisu.mentormatch.repository.PostRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.NotificationService;
import vn.kurisu.mentormatch.service.PostInteractionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostInteractionServiceImpl implements PostInteractionService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public ApiResponse<Void> toggleLike(Integer postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, currentUser.getId());

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            return ApiResponse.<Void>builder().message("Unliked post").build();
        } else {
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(currentUser)
                    .build();
            postLikeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            User postOwner = post.getUser();
            if (postOwner != null && !postOwner.getId().equals(currentUser.getId())) {
                String actorName = currentUser.getFullName() != null && !currentUser.getFullName().isBlank()
                        ? currentUser.getFullName()
                        : currentUser.getUserName();
                notificationService.sendNotification(
                        postOwner,
                        "Bài viết của bạn có lượt thích mới",
                        actorName + " đã thích bài viết của bạn.",
                        "POST_LIKED",
                        post.getId()
                );
            }

            return ApiResponse.<Void>builder().message("Liked post").build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<CommentResponse> addComment(Integer postId, CommentRequest request) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(currentUser)
                .build();

        comment = commentRepository.save(comment);

        User postOwner = post.getUser();
        if (postOwner != null && !postOwner.getId().equals(currentUser.getId())) {
            String actorName = currentUser.getFullName() != null && !currentUser.getFullName().isBlank()
                ? currentUser.getFullName()
                : currentUser.getUserName();

            String commentContent = request.getContent() == null ? "" : request.getContent().trim();
            String preview = commentContent.length() > 80
                ? commentContent.substring(0, 77) + "..."
                : commentContent;

            String message = preview.isEmpty()
                ? actorName + " đã bình luận bài viết của bạn."
                : actorName + " đã bình luận: \"" + preview + "\"";

            notificationService.sendNotification(
                postOwner,
                "Bài viết của bạn có bình luận mới",
                message,
                "POST_COMMENTED",
                post.getId()
            );
        }

        return ApiResponse.<CommentResponse>builder()
                .message("Comment added successfully")
                .result(mapToCommentResponse(comment))
                .build();
    }

    @Override
    public ApiResponse<List<CommentResponse>> getComments(Integer postId) {
        // Validate post exists
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }

        List<CommentResponse> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<CommentResponse>>builder()
                .result(comments)
                .build();
    }

    @Override
    public ApiResponse<Page<CommentResponse>> getCommentsPaged(Integer postId, int page, int size) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }
        Page<Comment> commentPage = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, PageRequest.of(page, size));
        return ApiResponse.<Page<CommentResponse>>builder()
                .result(commentPage.map(this::mapToCommentResponse))
                .build();
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getUserName())
                .userAvatar(comment.getUser().getAvatarUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

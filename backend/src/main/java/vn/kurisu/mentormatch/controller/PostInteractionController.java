package vn.kurisu.mentormatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.CommentRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.CommentResponse;
import vn.kurisu.mentormatch.service.PostInteractionService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    @PostMapping("/{postId}/like")
    public ApiResponse<Void> toggleLike(@PathVariable Integer postId) {
        return postInteractionService.toggleLike(postId);
    }

    @PostMapping("/{postId}/comments")
    public ApiResponse<CommentResponse> addComment(
            @PathVariable Integer postId,
            @RequestBody @Valid CommentRequest request) {
        return postInteractionService.addComment(postId, request);
    }

    @GetMapping("/{postId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Integer postId) {
        return postInteractionService.getComments(postId);
    }

    @GetMapping("/{postId}/comments/paged")
    public ApiResponse<Page<CommentResponse>> getCommentsPaged(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postInteractionService.getCommentsPaged(postId, page, size);
    }
}

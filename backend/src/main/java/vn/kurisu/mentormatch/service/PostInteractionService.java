package vn.kurisu.mentormatch.service;

import org.springframework.data.domain.Page;
import vn.kurisu.mentormatch.dto.request.CommentRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.CommentResponse;

import java.util.List;

public interface PostInteractionService {
    ApiResponse<Void> toggleLike(Integer postId);
    ApiResponse<CommentResponse> addComment(Integer postId, CommentRequest request);
    ApiResponse<List<CommentResponse>> getComments(Integer postId);
    ApiResponse<Page<CommentResponse>> getCommentsPaged(Integer postId, int page, int size);
}

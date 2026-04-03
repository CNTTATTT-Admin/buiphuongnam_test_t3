package vn.kurisu.mentormatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.PostCreationRequest;
import vn.kurisu.mentormatch.dto.request.PostUpdateRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.PostResponse;
import vn.kurisu.mentormatch.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(@RequestBody @Valid PostCreationRequest request) {
        return ResponseEntity.ok(postService.create(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getMyPosts() {
        return ResponseEntity.ok(postService.getMyPosts());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<PostResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<PostResponse>> update(@PathVariable Integer id, @RequestBody @Valid PostUpdateRequest request) {
        return ResponseEntity.ok(postService.update(id, request));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.delete(id));
    }
}

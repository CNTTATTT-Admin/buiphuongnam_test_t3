package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserResponse;
import vn.kurisu.mentormatch.service.FollowService;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetId}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleFollow(@PathVariable Integer targetId) {
        return ResponseEntity.ok(followService.toggleFollow(targetId));
    }

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowing() {
        return ResponseEntity.ok(followService.getFollowing());
    }

    @GetMapping("/followers/{targetId}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowers(@PathVariable Integer targetId) {
        return ResponseEntity.ok(followService.getFollowers(targetId));
    }

    @GetMapping("/status/{targetId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFollowStatus(@PathVariable Integer targetId) {
        return ResponseEntity.ok(followService.checkFollowStatus(targetId));
    }
}

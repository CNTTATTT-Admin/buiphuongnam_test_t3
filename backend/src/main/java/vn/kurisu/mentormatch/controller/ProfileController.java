package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.MenteeProfileRequest;
import vn.kurisu.mentormatch.dto.request.MentorProfileRequest;
import vn.kurisu.mentormatch.dto.request.UserProfileRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserProfileResponse;
import vn.kurisu.mentormatch.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        return profileService.getMyProfile();
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateBasicProfile(@RequestBody @Valid UserProfileRequest request) {
        return profileService.updateBasicProfile(request);
    }

    @PutMapping("/mentor/me")
    public ApiResponse<UserProfileResponse> updateMentorProfile(@RequestBody @Valid MentorProfileRequest request) {
        return profileService.updateMentorProfile(request);
    }

    @PutMapping("/mentee/me")
    public ApiResponse<UserProfileResponse> updateMenteeProfile(@RequestBody @Valid MenteeProfileRequest request) {
        return profileService.updateMenteeProfile(request);
    }
}

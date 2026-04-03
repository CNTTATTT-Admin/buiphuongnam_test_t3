package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.MentorRequestResponse;
import vn.kurisu.mentormatch.service.MentorRegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MentorRegistrationController {

    private final MentorRegistrationService mentorRegistrationService;
    private final vn.kurisu.mentormatch.repository.UserRepository userRepository;

    @PostMapping("/mentors/register")
    public ApiResponse<String> submitRegistration(
            @RequestParam("bio") String bio,
            @RequestParam(value = "skillIds", required = false) List<Integer> skillIds,
            @RequestParam(value = "certificateFiles", required = false) List<MultipartFile> certificateFiles) {
        
        // Extract authenticated username
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Find user by username to get their ID
        vn.kurisu.mentormatch.entity.User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new vn.kurisu.mentormatch.exception.AppException(vn.kurisu.mentormatch.exception.ErrorCode.USER_NOT_EXISTED));

        return mentorRegistrationService.submitRegistration(user.getId(), bio, skillIds, certificateFiles);
    }

    @GetMapping("/admin/mentor-requests")
    public ApiResponse<List<MentorRequestResponse>> getPendingRequests() {
        return mentorRegistrationService.getPendingRequests();
    }

    @PostMapping("/admin/mentor-requests/{profileId}/approve")
    public ApiResponse<String> approveRequest(@PathVariable Integer profileId) {
        return mentorRegistrationService.approveRequest(profileId);
    }
}

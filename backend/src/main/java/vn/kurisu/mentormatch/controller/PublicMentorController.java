package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserProfileResponse;
import vn.kurisu.mentormatch.dto.response.TimeSlotResponse;
import vn.kurisu.mentormatch.service.PublicMentorService;

import java.util.List;

@RestController
@RequestMapping("/api/public/mentors")
@RequiredArgsConstructor
public class PublicMentorController {

    private final PublicMentorService publicMentorService;

    @GetMapping
    public ApiResponse<List<UserProfileResponse>> getAllMentors() {
        return publicMentorService.getAllPublicMentors();
    }

    @GetMapping("/search")
    public ApiResponse<List<UserProfileResponse>> searchMentors(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return publicMentorService.searchMentors(keyword);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserProfileResponse> getMentorById(@PathVariable Integer id) {
        return publicMentorService.getPublicMentorProfile(id);
    }

    @GetMapping("/{id}/time-slots")
    public ApiResponse<List<TimeSlotResponse>> getMentorTimeSlots(@PathVariable Integer id) {
        return publicMentorService.getPublicMentorTimeSlots(id);
    }
}

package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.SkillRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.SkillResponse;
import vn.kurisu.mentormatch.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/skills")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSkillController {

    private final SkillService skillService;

    @GetMapping
    public ApiResponse<List<SkillResponse>> getAllSkills() {
        return skillService.getAllSkills();
    }

    @PostMapping
    public ApiResponse<SkillResponse> createSkill(@RequestBody SkillRequest request) {
        return skillService.createSkill(request);
    }

    @PutMapping("/{id}")
    public ApiResponse<SkillResponse> updateSkill(@PathVariable Integer id, @RequestBody SkillRequest request) {
        return skillService.updateSkill(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<SkillResponse> deleteSkill(@PathVariable Integer id) {
        return skillService.deleteSkill(id);
    }
}

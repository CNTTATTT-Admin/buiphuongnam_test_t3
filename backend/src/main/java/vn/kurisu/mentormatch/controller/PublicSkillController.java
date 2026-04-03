package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.SkillResponse;
import vn.kurisu.mentormatch.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/skills")
@RequiredArgsConstructor
public class PublicSkillController {

    private final SkillRepository skillRepository;

    @GetMapping
    public ApiResponse<List<SkillResponse>> getAllSkills() {
        List<SkillResponse> skills = skillRepository.findAll().stream()
                .map(skill -> SkillResponse.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.<List<SkillResponse>>builder()
                .result(skills)
                .build();
    }
}

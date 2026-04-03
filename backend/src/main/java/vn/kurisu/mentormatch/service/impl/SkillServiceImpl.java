package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.request.SkillRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.SkillResponse;
import vn.kurisu.mentormatch.entity.Skill;
import vn.kurisu.mentormatch.repository.SkillRepository;
import vn.kurisu.mentormatch.service.SkillService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public ApiResponse<List<SkillResponse>> getAllSkills() {
        List<SkillResponse> skills = skillRepository.findAll().stream()
                .map(this::mapToSkillResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<SkillResponse>>builder()
                .result(skills)
                .build();
    }

    @Override
    public ApiResponse<SkillResponse> createSkill(SkillRequest request) {
        if (skillRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Skill already exists");
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .build();

        skill = skillRepository.save(skill);

        return ApiResponse.<SkillResponse>builder()
                .result(mapToSkillResponse(skill))
                .message("Skill created successfully")
                .build();
    }

    @Override
    public ApiResponse<SkillResponse> updateSkill(Integer id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setName(request.getName());
        skill = skillRepository.save(skill);

        return ApiResponse.<SkillResponse>builder()
                .result(mapToSkillResponse(skill))
                .message("Skill updated successfully")
                .build();
    }

    @Override
    public ApiResponse<SkillResponse> deleteSkill(Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getMentorProfiles().isEmpty()) {
            throw new RuntimeException("Cannot delete skill being used by mentors");
        }

        skillRepository.delete(skill);

        return ApiResponse.<SkillResponse>builder()
                .message("Skill deleted successfully")
                .build();
    }

    private SkillResponse mapToSkillResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .build();
    }
}

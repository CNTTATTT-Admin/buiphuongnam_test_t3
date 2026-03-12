package vn.kurisu.mentormatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.kurisu.mentormatch.dto.request.SkillRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.SkillResponse;


public interface SkillService {
    ApiResponse<List<SkillResponse>> getAllSkills();
    ApiResponse<SkillResponse> createSkill(SkillRequest request);
    ApiResponse<SkillResponse> updateSkill(Integer id, SkillRequest request);
    ApiResponse<SkillResponse> deleteSkill(Integer id);


    
}
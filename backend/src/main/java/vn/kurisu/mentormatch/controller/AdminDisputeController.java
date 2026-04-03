package vn.kurisu.mentormatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.ResolveDisputeRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.DisputeResponse;
import vn.kurisu.mentormatch.service.DisputeService;

@RestController
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisputeController {

    private final DisputeService disputeService;

    @GetMapping
    public ApiResponse<Page<DisputeResponse>> getAllDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return disputeService.getAllDisputes(pageable);
    }

    @PostMapping("/{disputeId}/resolve")
    public ApiResponse<DisputeResponse> resolveDispute(
            @PathVariable Integer disputeId,
            @RequestBody @Valid ResolveDisputeRequest request) {
        return disputeService.resolveDispute(disputeId, request);
    }
}

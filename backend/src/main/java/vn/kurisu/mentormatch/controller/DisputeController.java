package vn.kurisu.mentormatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.CreateDisputeRequest;
import vn.kurisu.mentormatch.dto.request.CounterDisputeRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.DisputeResponse;
import vn.kurisu.mentormatch.service.DisputeService;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    public ApiResponse<DisputeResponse> createDispute(@RequestBody @Valid CreateDisputeRequest request) {
        return disputeService.createDispute(request);
    }

    @PostMapping("/{id}/counter")
    public ApiResponse<DisputeResponse> counterDispute(@PathVariable Integer id, @RequestBody @Valid CounterDisputeRequest request) {
        return disputeService.counterDispute(id, request);
    }

    @GetMapping("/booking/{bookingId}")
    public ApiResponse<DisputeResponse> getDisputeByBooking(@PathVariable Integer bookingId) {
        return disputeService.getDisputeByBooking(bookingId);
    }

    @GetMapping("/my-disputes")
    public ApiResponse<Page<DisputeResponse>> getMyDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return disputeService.getMyDisputes(pageable);
    }

    @GetMapping("/mentor-disputes")
    public ApiResponse<Page<DisputeResponse>> getMentorDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return disputeService.getMentorDisputes(pageable);
    }
}

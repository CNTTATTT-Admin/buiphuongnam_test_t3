package vn.kurisu.mentormatch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.kurisu.mentormatch.dto.request.CreateDisputeRequest;
import vn.kurisu.mentormatch.dto.request.CounterDisputeRequest;
import vn.kurisu.mentormatch.dto.request.ResolveDisputeRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.DisputeResponse;

public interface DisputeService {
    ApiResponse<DisputeResponse> createDispute(CreateDisputeRequest request);
    ApiResponse<DisputeResponse> counterDispute(Integer disputeId, CounterDisputeRequest request);
    ApiResponse<DisputeResponse> getDisputeByBooking(Integer bookingId);
    ApiResponse<Page<DisputeResponse>> getMyDisputes(Pageable pageable);
    ApiResponse<Page<DisputeResponse>> getMentorDisputes(Pageable pageable);
    
    // Admin functions
    ApiResponse<Page<DisputeResponse>> getAllDisputes(Pageable pageable);
    ApiResponse<DisputeResponse> resolveDispute(Integer disputeId, ResolveDisputeRequest request);
}

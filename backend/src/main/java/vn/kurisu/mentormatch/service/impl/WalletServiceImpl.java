package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.WithdrawalRequestDto;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.WithdrawalResponse;
import vn.kurisu.mentormatch.entity.MentorProfile;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.entity.WithdrawalRequest;
import vn.kurisu.mentormatch.entity.WithdrawalStatus;
import vn.kurisu.mentormatch.repository.MentorProfileRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.repository.WithdrawalRequestRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ========== MENTOR APIs ==========

    @Transactional
    public ApiResponse<WithdrawalResponse> createWithdrawalRequest(WithdrawalRequestDto request) {
        User currentUser = getCurrentUser();
        MentorProfile mentorProfile = currentUser.getMentorProfile();
        if (mentorProfile == null) {
            throw new RuntimeException("You are not a mentor");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than 0");
        }

        if (mentorProfile.getWalletBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from wallet immediately (hold)
        mentorProfile.setWalletBalance(mentorProfile.getWalletBalance().subtract(request.getAmount()));
        mentorProfileRepository.save(mentorProfile);

        WithdrawalRequest withdrawal = WithdrawalRequest.builder()
                .mentorProfile(mentorProfile)
                .amount(request.getAmount())
                .bankName(request.getBankName())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankAccountHolder(request.getBankAccountHolder())
                .note(request.getNote())
                .status(WithdrawalStatus.PENDING)
                .build();

        withdrawal = withdrawalRequestRepository.save(withdrawal);

        return ApiResponse.<WithdrawalResponse>builder()
                .result(mapToResponse(withdrawal))
                .message("Withdrawal request submitted successfully")
                .build();
    }

    public ApiResponse<List<WithdrawalResponse>> getMyWithdrawals() {
        User currentUser = getCurrentUser();
        MentorProfile mentorProfile = currentUser.getMentorProfile();
        if (mentorProfile == null) {
            throw new RuntimeException("You are not a mentor");
        }

        List<WithdrawalResponse> responses = withdrawalRequestRepository
                .findByMentorProfileIdOrderByCreatedAtDesc(mentorProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<WithdrawalResponse>>builder()
                .result(responses)
                .build();
    }

    // ========== ADMIN APIs ==========

    public ApiResponse<List<WithdrawalResponse>> getAllWithdrawals() {
        List<WithdrawalResponse> responses = withdrawalRequestRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<WithdrawalResponse>>builder()
                .result(responses)
                .build();
    }

    @Transactional
    public ApiResponse<WithdrawalResponse> approveWithdrawal(Integer id) {
        WithdrawalRequest withdrawal = withdrawalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Withdrawal request not found"));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }

        withdrawal.setStatus(WithdrawalStatus.APPROVED);
        withdrawalRequestRepository.save(withdrawal);

        return ApiResponse.<WithdrawalResponse>builder()
                .result(mapToResponse(withdrawal))
                .message("Withdrawal approved")
                .build();
    }

    @Transactional
    public ApiResponse<WithdrawalResponse> rejectWithdrawal(Integer id, String adminNote) {
        WithdrawalRequest withdrawal = withdrawalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Withdrawal request not found"));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }

        // Refund the held amount back to the mentor's wallet
        MentorProfile mentorProfile = withdrawal.getMentorProfile();
        mentorProfile.setWalletBalance(mentorProfile.getWalletBalance().add(withdrawal.getAmount()));
        mentorProfileRepository.save(mentorProfile);

        withdrawal.setStatus(WithdrawalStatus.REJECTED);
        withdrawal.setAdminNote(adminNote);
        withdrawalRequestRepository.save(withdrawal);

        return ApiResponse.<WithdrawalResponse>builder()
                .result(mapToResponse(withdrawal))
                .message("Withdrawal rejected, amount refunded to mentor's wallet")
                .build();
    }

    private WithdrawalResponse mapToResponse(WithdrawalRequest w) {
        return WithdrawalResponse.builder()
                .id(w.getId())
                .mentorProfileId(w.getMentorProfile().getId())
                .mentorName(w.getMentorProfile().getUser().getFullName())
                .amount(w.getAmount())
                .bankName(w.getBankName())
                .bankAccountNumber(w.getBankAccountNumber())
                .bankAccountHolder(w.getBankAccountHolder())
                .note(w.getNote())
                .adminNote(w.getAdminNote())
                .status(w.getStatus())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }
}

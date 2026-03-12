package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.WithdrawalResponse;
import vn.kurisu.mentormatch.service.impl.WalletServiceImpl;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/withdrawals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminWithdrawalController {

    private final WalletServiceImpl walletService;

    @GetMapping
    public ApiResponse<List<WithdrawalResponse>> getAllWithdrawals() {
        return walletService.getAllWithdrawals();
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<WithdrawalResponse> approveWithdrawal(@PathVariable Integer id) {
        return walletService.approveWithdrawal(id);
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<WithdrawalResponse> rejectWithdrawal(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) {
        String adminNote = body != null ? body.get("adminNote") : null;
        return walletService.rejectWithdrawal(id, adminNote);
    }
}

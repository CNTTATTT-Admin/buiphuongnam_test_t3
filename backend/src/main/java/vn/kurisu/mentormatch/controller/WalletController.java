package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.WithdrawalRequestDto;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.WithdrawalResponse;
import vn.kurisu.mentormatch.service.impl.WalletServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletServiceImpl walletService;

    @PostMapping("/withdraw")
    public ApiResponse<WithdrawalResponse> createWithdrawalRequest(@RequestBody WithdrawalRequestDto request) {
        return walletService.createWithdrawalRequest(request);
    }

    @GetMapping("/withdrawals")
    public ApiResponse<List<WithdrawalResponse>> getMyWithdrawals() {
        return walletService.getMyWithdrawals();
    }
}

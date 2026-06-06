package com.pacta.pacta_app.banking.infrastructure.controller;

import com.pacta.pacta_app.banking.application.BankAccountService;
import com.pacta.pacta_app.banking.application.dto.BankAccountRequest;
import com.pacta.pacta_app.banking.application.dto.BankAccountResponse;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponse add(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                   @Valid @RequestBody BankAccountRequest req) {
        return BankAccountResponse.from(
                bankAccountService.add(userId, req.bankName(), req.accountNumber(),
                        req.accountType(), req.holderName()));
    }

    @GetMapping("/me")
    public List<BankAccountResponse> getMine(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return bankAccountService.findByUserId(userId).stream().map(BankAccountResponse::from).toList();
    }

    @GetMapping("/users/{userId}")
    public List<BankAccountResponse> getByUser(@PathVariable String userId) {
        return bankAccountService.findByUserId(userId).stream().map(BankAccountResponse::from).toList();
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String accountId,
                       @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        bankAccountService.remove(userId, accountId);
    }
}

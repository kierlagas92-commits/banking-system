package com.example.bankingsystem.controller;

import com.example.bankingsystem.dto.CreateAccountRequest;
import com.example.bankingsystem.dto.DepositRequest;
import com.example.bankingsystem.dto.TransferRequest;
import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.service.BankService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService){
        this.bankService = bankService;
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable long accountNumber){
        return  bankService.findAccountByAccountNumber(accountNumber);
    }

    @PostMapping
    public Account createAccount(@RequestBody CreateAccountRequest request){
        return bankService.createAccount(
                request.getAccountNumber(),
                request.getAccountHolder(),
                request.getInitialBalance()
        );
    }

    @PostMapping("/{accountNumber}/deposit")
    public Account deposit(
            @PathVariable long accountNumber,
            @RequestBody DepositRequest depositRequest
            ){
        return bankService.deposit(accountNumber, depositRequest.getAmount());
    }

    @PostMapping("/{accountNumber}/withdraw")
    public Account withdraw(
        @PathVariable long accountNumber,
        @RequestBody DepositRequest depositRequest
    ){
        return bankService.withdraw(accountNumber, depositRequest.getAmount());
    }

    @PostMapping("/transfer")
    public void transfer(
            @RequestBody TransferRequest transferRequest
            ){
        bankService.transfer(
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber(),
                transferRequest.getAmount()
        );
    }

}

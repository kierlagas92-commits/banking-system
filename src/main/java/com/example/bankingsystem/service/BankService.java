package com.example.bankingsystem.service;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BankService {
    private final AccountRepository accountRepository;

    public BankService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account findAccountByAccountNumber(long accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found!"));
    }

    public Account createAccount(
            long accountNumber,
            String accountHolder,
            BigDecimal initialBalance
    ){
        Account account = new Account(accountNumber, accountHolder, initialBalance);
        return accountRepository.save(account);
    }

    public Account deposit(long accountNumber, BigDecimal amount){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found: " + accountNumber));

        account.deposit(amount);

        // Call the Update balance in repository
        accountRepository.updateBalance(accountNumber, account.getBalance());

        return account;
    }

    public Account withdraw(long accountNumber, BigDecimal amount){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found: " + accountNumber));

        account.withdraw(amount);

        // Update the account balance
        accountRepository.updateBalance(accountNumber, account.getBalance());

        return account;
    }

    @Transactional
    public void transfer(
            long fromAccountNumber,
            long toAccountNumber,
            BigDecimal amount
    ){

        if(fromAccountNumber == toAccountNumber){
            throw new IllegalArgumentException(
                    "Source and Destination account must be different"
            );
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + fromAccountNumber
                ));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + toAccountNumber
                ));

        // processing changes
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        // Update the repository
        accountRepository.updateBalance(fromAccountNumber, fromAccount.getBalance());
        accountRepository.updateBalance(toAccountNumber, toAccount.getBalance());

    }


}

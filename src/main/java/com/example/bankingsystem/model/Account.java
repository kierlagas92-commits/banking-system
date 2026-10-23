package com.example.bankingsystem.model;

import java.math.BigDecimal;

public class Account {

    private Long id;
    private long accountNumber;
    private String accountHolder;
    private BigDecimal balance;

    public Account(
            long accountNumber,
            String accountHolder,
            BigDecimal balance
    ){
        /* Check if the accountNumber is valid */
        if(accountNumber <= 0){
            throw new IllegalArgumentException("Account number must be positive");
        } else {
            this.accountNumber = accountNumber;
        }

        /* Check if the accountHolder is valid */
        if(accountHolder == null || accountHolder.isBlank()){
            throw new IllegalArgumentException("Account Holder must not be blank");
        } else {
            this.accountHolder = accountHolder;
        }

        /* Check if balance is valid */
        if(balance == null || balance.signum() < 0){
            throw new IllegalArgumentException("Balance must be positive or zero");
        } else {
            this.balance = balance;
        }
    }

    public Account(
            Long id,
            long accountNumber,
            String accountHolder,
            BigDecimal balance
    ){
        this(accountNumber, accountHolder, balance);

        if(id == null || id <= 0){
            throw new IllegalArgumentException("Account Id must be positive");
        }

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount){
        if(amount == null || amount.signum() <= 0){
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        // Compute and update balance
        BigDecimal newBalance = getBalance().add(amount);
        this.balance = newBalance;
    }

    public void withdraw(BigDecimal amount){
        if(amount == null || amount.signum() <= 0){
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }

        if(amount.compareTo(balance) > 0){
            throw new IllegalArgumentException("Insufficient funds");
        }

        BigDecimal newBalance = balance.subtract(amount);
        this.balance = newBalance;
    }
}

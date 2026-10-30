package com.example.bankingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferAudit {
    private Long id;
    private long fromAccount;
    private long toAccount;
    private BigDecimal amount;
    private TransferStatus status;
    private String message;
    private LocalDateTime createdAt;

    public TransferAudit(
            long fromAccount,
            long toAccount,
            BigDecimal amount,
            TransferStatus status,
            String message
    ){
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }

    public TransferAudit(
            Long id,
            long fromAccount,
            long toAccount,
            BigDecimal amount,
            TransferStatus status,
            String message,
            LocalDateTime createdAt
    ){
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public long getFromAccount() {
        return fromAccount;
    }

    public long getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

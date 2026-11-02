package com.example.bankingsystem.service;

import com.example.bankingsystem.model.TransferAudit;
import com.example.bankingsystem.repository.TransferAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferServiceAudit {

    private final TransferAuditRepository transferAuditRepository;

    public TransferServiceAudit(TransferAuditRepository transferAuditRepository){
        this.transferAuditRepository = transferAuditRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public TransferAudit recordSuccessfulTransfer(TransferAudit audit){
        return transferAuditRepository.save(audit);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransferAudit recordFailedTransfer(TransferAudit audit){
        return transferAuditRepository.save(audit);
    }


}

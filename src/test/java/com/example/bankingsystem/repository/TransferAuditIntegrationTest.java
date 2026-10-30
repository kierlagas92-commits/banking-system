package com.example.bankingsystem.repository;

import com.example.bankingsystem.model.TransferAudit;
import com.example.bankingsystem.model.TransferStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

@SpringBootTest
public class TransferAuditIntegrationTest {

    @Autowired
    private TransferAuditRepository transferAuditRepository;

    @Test
    void saveShouldInsertAndReturnTransferAudit(){

        TransferAudit audit = new TransferAudit(
                10004L,
                10005L,
                new BigDecimal("100.00"),
                TransferStatus.SUCCESS,
                "Transfer Completed"
        );

        TransferAudit savedAudit = transferAuditRepository.save(audit);

        assertNotNull(savedAudit.getId());
        assertEquals(10004L, savedAudit.getFromAccount());
        assertEquals(10005L, savedAudit.getToAccount());
        assertEquals(
                new BigDecimal("100.00"),
                savedAudit.getAmount()
        );
        assertEquals(
                TransferStatus.SUCCESS,
                savedAudit.getStatus()
        );
        assertEquals("Transfer Completed", savedAudit.getMessage());
        assertNotNull(savedAudit.getCreatedAt());
    }


}

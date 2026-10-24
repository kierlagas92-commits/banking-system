package com.example.bankingsystem.repository;

import com.example.bankingsystem.model.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update(
                "DELETE FROM accounts WHERE account_number = ?",
                10003L
        );
    }


    @Test
    void checkDatabasePasswordVariableExists() {
        String password = System.getenv("DB_PASSWORD");

        assertNotNull(password, "DB_PASSWORD environment variable is missing");
        assertFalse(password.isBlank(), "DB_PASSWORD is blank");
    }

    @Test
    void savedAccountShouldBeRetrievableFromDatabase() {
        jdbcTemplate.update(
                "DELETE FROM accounts WHERE account_number = ?",
                10003L
        );

        // Arrange
        Account newAccount = new Account(
                10003L,
                "Mark",
                new BigDecimal("20000.00")
        );

        // Act
        Account savedAccount = accountRepository.save(newAccount);

        Optional<Account> result = accountRepository.findByAccountNumber(
                savedAccount.getAccountNumber()
        );

        // Assert
        assertTrue(result.isPresent());

        Account retrievedAccount = result.orElseThrow();

        assertEquals(savedAccount.getId(), retrievedAccount.getId());
        assertEquals(
                savedAccount.getAccountNumber(),
                retrievedAccount.getAccountNumber()
        );
        assertEquals(
                savedAccount.getAccountHolder(),
                retrievedAccount.getAccountHolder()
        );
        assertEquals(
                0,
                new BigDecimal("20000.00").compareTo(retrievedAccount.getBalance())
        );
    }


}

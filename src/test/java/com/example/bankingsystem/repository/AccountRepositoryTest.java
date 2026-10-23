package com.example.bankingsystem.repository;

import com.example.bankingsystem.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AccountRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AccountRepository accountRepository;

    @Test
    public void findByAccountNumberShouldReturnedAccount(){
        Optional<Account> result = accountRepository.findByAccountNumber(10001L);

        assertTrue(result.isPresent());

        Account account = result.orElseThrow();

        assertEquals(10001L, account.getAccountNumber());
        assertEquals("John", account.getAccountHolder());
        assertEquals(0, new BigDecimal("1000.00").compareTo(account.getBalance()));
    }

    @Test
    public void findByAccountNumberShouldReturnEmptyWhenAccountDoesNotExists(){
        Optional<Account> result = accountRepository.findByAccountNumber(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldInsertAccountAndReturnGeneratedId(){
        // Arrange
        Account newAccount = new Account(
                10002L,
                "Anna",
                new BigDecimal("5000.00")
        );

        // Act
        Account savedAccount = accountRepository.save(newAccount);

        // Assert
        assertTrue(savedAccount.getId() != null);
        assertEquals(10002L, savedAccount.getAccountNumber());
        assertEquals("Anna", savedAccount.getAccountHolder());
        assertEquals(0, new BigDecimal("5000.00").compareTo(savedAccount.getBalance()));
    }



    @Test
    public void savedAccountShouldBeRetrievableFromDatabase(){
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
        assertEquals(savedAccount.getAccountNumber(), retrievedAccount.getAccountNumber());
        assertEquals(savedAccount.getAccountHolder(), retrievedAccount.getAccountHolder());
        assertEquals(0, new BigDecimal("20000.00").compareTo(retrievedAccount.getBalance()));
    }

    @Test
    void updateBalanceShouldExecuteUpdateWithCorrectParameters(){
        // Arrange
        long accountNumber = 10002L;
        BigDecimal newBalance = new BigDecimal("5500.00");

        // Act
        accountRepository.updateBalance(accountNumber, newBalance);

        // Assert
        verify(jdbcTemplate).update(
                contains("UPDATE accounts"),
                eq(newBalance),
                eq(accountNumber)
        );
    }

}


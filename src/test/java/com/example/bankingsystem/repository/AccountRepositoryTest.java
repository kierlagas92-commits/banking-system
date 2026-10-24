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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AccountRepository accountRepository;

    @Test
    public void findByAccountNumberShouldReturnedAccount() throws SQLException{
        mockAccountQuery(1L, 10001L, "John", new BigDecimal("1000"));

        Optional<Account> result = accountRepository.findByAccountNumber(10001L);

        assertTrue(result.isPresent());

        Account account = result.orElseThrow();

        System.out.println("Actual balance: " + account.getBalance());

        assertEquals(10001L, account.getAccountNumber());
        assertEquals("John", account.getAccountHolder());
        assertEquals(0, new BigDecimal("1000.00").compareTo(account.getBalance()));
    }

    @Test
    public void findByAccountNumberShouldReturnEmptyWhenAccountDoesNotExists()
            throws SQLException {

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        when(jdbcTemplate.query(
                any(String.class),
                any(ResultSetExtractor.class),
                eq(99999L)
        )).thenAnswer(invocation -> {
            ResultSetExtractor<?> extractor = invocation.getArgument(1);
            return extractor.extractData(resultSet);
        });

        Optional<Account> result =
                accountRepository.findByAccountNumber(99999L);

        assertTrue(result.isEmpty());
    }


    @Test
    public void saveShouldInsertAccountAndReturnGeneratedId() {
        // Arrange
        Account newAccount = new Account(
                10002L,
                "Anna",
                new BigDecimal("5000.00")
        );

        when(jdbcTemplate.update(
                any(PreparedStatementCreator.class),
                any(KeyHolder.class)
        )).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);

            keyHolder.getKeyList().add(
                    java.util.Map.of("GENERATED_KEY", 4L)
            );

            return 1;
        });

        // Act
        Account savedAccount = accountRepository.save(newAccount);

        // Assert
        assertTrue(savedAccount.getId() != null);
        assertEquals(4L, savedAccount.getId());
        assertEquals(10002L, savedAccount.getAccountNumber());
        assertEquals("Anna", savedAccount.getAccountHolder());
        assertEquals(
                0,
                new BigDecimal("5000.00").compareTo(savedAccount.getBalance())
        );
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

    private void mockAccountQuery(
            long id,
            long accountNumber,
            String accountHolder,
            BigDecimal balance) throws SQLException {

        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(id);
        when(resultSet.getLong("account_number")).thenReturn(accountNumber);
        when(resultSet.getString("account_holder")).thenReturn(accountHolder);
        when(resultSet.getBigDecimal("balance")).thenReturn(balance);

        when(jdbcTemplate.query(
                any(String.class),
                any(ResultSetExtractor.class),
                eq(accountNumber)
        )).thenAnswer(invocation -> {
            ResultSetExtractor<?> extractor = invocation.getArgument(1);
            return extractor.extractData(resultSet);
        });
    }

}


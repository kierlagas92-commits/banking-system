package com.example.bankingsystem.repository;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.service.BankService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class AccountRepositoryIntegrationTest {

//    @Autowired
//    private AccountRepository accountRepository;

    @MockitoSpyBean
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BankService bankService;

//    @AfterEach
//    void cleanUp() {
//        jdbcTemplate.update(
//                "DELETE FROM accounts WHERE account_number = ?",
//                10003L
//        );
//    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update(
                "DELETE FROM accounts WHERE account_number IN (?, ?, ?, ?)",
                10003L,
                10004L,
                10005L,
                10006L
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

    @Test
    void transferShouldRollbackWhenSecondBalanceUpdateFails(){
        // Arrange: create two accounts in the real database
        jdbcTemplate.update(
                "DELETE FROM accounts WHERE account_number IN (?, ?)",
                10004L, 10005L
        );

        Account anna = accountRepository.save(
                new Account(10004L, "Anna", new BigDecimal("500.00"))
        );

        Account david = accountRepository.save(
                new Account(10005L, "David", new BigDecimal("300.00"))
        );

        // doCallRealMethod = allows the first update to execute against MySQL
        Mockito.doCallRealMethod()
                .doThrow(new RuntimeException("Simulated database failure"))
                        .when(accountRepository)
                                .updateBalance(
                                        Mockito.anyLong(),
                                        Mockito.any(BigDecimal.class)
                                );

        // Act + Assert: transfer should throw an exception
        assertThrows(
                RuntimeException.class,
                () -> bankService.transfer(
                        10004L,
                        10005L,
                        new BigDecimal("100.00")
                )
        );

        //Query and Verify that the database balances were rollback
        BigDecimal annaBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE account_number = ?",
                BigDecimal.class,
                10004L
        );

        BigDecimal davidBalance = jdbcTemplate.queryForObject(
            "SELECT balance FROM accounts WHERE account_number = ?",
                BigDecimal.class,
                10005L
        );

        assertEquals(0, new BigDecimal("500.00").compareTo(annaBalance));
        assertEquals(0, new BigDecimal("300.00").compareTo(davidBalance));

    }

    @Test
    void transferShouldFailWhenDestinationAccountDoesntExists(){
        // Arrange

        // ===== Create the data source =====
        jdbcTemplate.update(
                "DELETE FROM accounts WHERE account_number = ?",
                10006L
        );

        accountRepository.save(
                new Account(10006L, "David", new BigDecimal("500.00"))
        );

        // Act + Assert: Destination account does not exists
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        10006L,
                        99999L,
                        new BigDecimal("100.00")
                )
        );

        // ===== Verify: source account balance remains unchanged =====
        BigDecimal davidBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE account_number = ? ",
                BigDecimal.class,
                10006L
        );

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(davidBalance)
        );


    }


}

package com.example.bankingsystem.service;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mock.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

import javax.swing.text.html.Option;


@ExtendWith(MockitoExtension.class)
public class BankServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BankService bankService;

    @Test
    void findAccountByAccountNumberShouldReturnAccount(){
        Account account = new Account(
                10001L,
                "John",
                new BigDecimal("10000.00")
        );

        when(accountRepository.findByAccountNumber(10001L))
                .thenReturn(Optional.of(account));

        Account result = bankService.findAccountByAccountNumber(10001L);

        assertEquals(10001L, result.getAccountNumber());
        assertEquals("John", result.getAccountHolder());
        assertEquals(0, new BigDecimal("10000").compareTo(result.getBalance()));
    }

    @Test
    void findAccountByAccountNumberShouldThrowExceptionWhenAccountDoesNotExist(){
        when(accountRepository.findByAccountNumber(9999L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bankService.findAccountByAccountNumber(9999L));
    }

//    @Test
//    void createAccountShouldSaveAndReturnAccount(){
//        Account account = new Account(
//                10004L,
//                "Anna",
//                new BigDecimal("500.00")
//        );
//
//        Account savedAccount = new Account(
//                4L,
//                10004L,
//                "Anna",
//                new BigDecimal("500.00")
//        );
//
//        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
//
//        verify(accountRepository).save(captor.capture());
//
//        Account capturedAccount = captor.getValue();
//
//        when(accountRepository.save(any(Account.class)))
//                .thenReturn(savedAccount);
//
//        Account result = bankService.createAccount(
//                10004L,
//                "Anna",
//                new BigDecimal("500.00")
//        );
//
//        assertEquals(10004L, capturedAccount.getAccountNumber());
//        assertEquals("Anna", capturedAccount.getAccountHolder());
//        assertEquals(
//                0,
//                new BigDecimal("500.00").compareTo(capturedAccount.getBalance())
//        );
//
////        assertEquals(4L, result.getId());
////        assertEquals(10004L, result.getAccountNumber());
////        assertEquals("Anna", result.getAccountHolder());
////        assertEquals(0, new BigDecimal("500.00").compareTo(result.getBalance()));
//
////        verify(accountRepository).save(any(Account.class));
//    }



    @Test
    void createAccountShouldSaveAndReturnAccount() {
        // Arrange
        Account savedAccount = new Account(
                4L,
                10004L,
                "Anna",
                new BigDecimal("500.00")
        );

        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        // Act
        Account result = bankService.createAccount(
                10004L,
                "Anna",
                new BigDecimal("500.00")
        );

        // Capture the Account passed to the repository
        ArgumentCaptor<Account> captor =
                ArgumentCaptor.forClass(Account.class);

        verify(accountRepository).save(captor.capture());

        Account capturedAccount = captor.getValue();

        // Assert the Account sent to the repository
        assertEquals(10004L, capturedAccount.getAccountNumber());
        assertEquals("Anna", capturedAccount.getAccountHolder());
        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(capturedAccount.getBalance())
        );

        // Assert the Account returned by the service
        assertEquals(4L, result.getId());
        assertEquals(10004L, result.getAccountNumber());
        assertEquals("Anna", result.getAccountHolder());
        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(result.getBalance())
        );
    }

    @Test
    void createAccountShouldThrowExceptionWhenRepositoryFails(){
        // Arrange
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new RuntimeException("Database Error"));

        // Act and Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankService.createAccount(
                        10005L,
                        "David",
                        new BigDecimal("750.00")
                )
        );

        assertEquals("Database Error", exception.getMessage());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccountShouldRejectInvalidAccountNumber(){
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.createAccount(
                        0L,
                        "David",
                        new BigDecimal("750.00")
                )
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccountShouldRejectBlankAccountHolder(){
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.createAccount(
                 10006L,
                 " ",
                 new BigDecimal("750.00")
                )
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccountShouldRejectNegativeInitialBalance(){
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.createAccount(
                        10007L,
                        "David",
                        new BigDecimal("-1.00")
                )
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void depositShouldUpdateBalanceAndReturnAccount(){
        // Arrange
        Account account = new Account(
                4L,
                10004L,
                "Anna",
                new BigDecimal("500.00")
        );


        when(accountRepository.findByAccountNumber(10004L))
                .thenReturn(Optional.of(account));

        // Act
        Account result = bankService.deposit(
                10004L, new BigDecimal("200.00")
        );

        // Assert
        assertEquals(
                0,
                new BigDecimal("700.00").compareTo(result.getBalance())
        );

        verify(accountRepository).updateBalance(10004L, new BigDecimal("700.00"));

    }

    @Test
    void depositShouldThrowExceptionWhenAccountNotFound(){
        // Arrange
        long accountNumber = 99999L;

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        // Act and Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit(
                        accountNumber,
                        new BigDecimal("200.00")
                )
        );

        assertEquals(
                "Account not found: 99999",
                exception.getMessage()
        );

        verify(accountRepository, never()).updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void depositShouldRejectInvalidAmount(){
        // Arrange
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit(
                        accountNumber,
                        BigDecimal.ZERO
                )
        );

        verify(accountRepository, never()).
                updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void depositShouldRejectNegativeAmount(){
        // Arrange
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit(accountNumber, new BigDecimal("-100.00"))
        );

        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void depositShouldRejectNullAmount(){
        // Arrange
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.deposit(accountNumber, null)
        );

        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void withdrawShouldUpdateBalanceAndReturnAccount(){
        // Arrange
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "David",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // Act and Assert
        Account savedAccount = bankService.withdraw(accountNumber, new BigDecimal("150.00"));

        assertEquals(0, new BigDecimal("350.00").compareTo(savedAccount.getBalance()));

//        verify(accountRepository).updateBalance(accountNumber, new BigDecimal("350.00"));
        verify(accountRepository).updateBalance(
                eq(accountNumber),
                argThat(balance -> balance.compareTo(
                        new BigDecimal("350.00")
                ) == 0)
        );
    }

    @Test
    void withdrawShouldThrowExceptionWhenInsufficientBalance(){
        // Arrange
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // Act and Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(accountNumber, new BigDecimal("600.00"))
        );

        assertEquals(
                "Insufficient funds",
                exception.getMessage()
        );

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(account.getBalance())
        );

        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void withdrawShouldThrowExceptionWhenAccountNotFound(){
        // Arrange
        long accountNumber = 99999L;

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(accountNumber, new BigDecimal("100.00"))
        );

        assertEquals(
                "Account not found: " + accountNumber,
                exception.getMessage()
        );

        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void withdrawShouldRejectZeroAmount(){
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "David",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(
                        accountNumber,
                        BigDecimal.ZERO
                )
        );

        // The database balance must not be updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void withdrawShouldRejectNegativeAmount(){
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "David",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(
                        accountNumber,
                        new BigDecimal("-100.00")
                )
        );

        // Ensure the database is not updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void withdrawShouldRejectNullAmount(){
        long accountNumber = 10004L;

        Account account = new Account(
                4L,
                accountNumber,
                "David",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.withdraw(accountNumber, null)
        );

        // Ensure the database is not updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void transferShouldUpdateBothAccountBalances(){
        long annaAccountNumber = 10004L;
        long davidAccountNumber = 10005L;

        Account anna = new Account(
                4L,
                annaAccountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        Account david = new Account(
                5L,
                davidAccountNumber,
                "David",
                new BigDecimal("300.00")
        );

        when(accountRepository.findByAccountNumber(annaAccountNumber))
                .thenReturn(Optional.of(anna));

        when(accountRepository.findByAccountNumber(davidAccountNumber))
                .thenReturn(Optional.of(david));

        bankService.transfer(
                annaAccountNumber,
                davidAccountNumber,
                new BigDecimal("200.00"));

        assertEquals(
                0,
                new BigDecimal("300.00").compareTo(anna.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(david.getBalance())
        );

        verify(accountRepository).updateBalance(
                eq(annaAccountNumber),
                argThat(balance -> balance.compareTo(
                        new BigDecimal("300.00")
                ) == 0)
        );


        verify(accountRepository).updateBalance(
                eq(davidAccountNumber),
                argThat(balance ->
                        balance.compareTo(new BigDecimal("500.00")) == 0 )
        );

    }

    @Test
    void transferShouldFailWhenSourceAccountHasInsufficientBalance(){
        long annaAccountNumber = 10004L;
        long davidAccountNumber = 10005L;

        Account anna = new Account(
                4L,
                annaAccountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        Account david = new Account(
                5L,
                davidAccountNumber,
                "David",
                new BigDecimal("300.00")
        );

        when(accountRepository.findByAccountNumber(annaAccountNumber))
                .thenReturn(Optional.of(anna));

        when(accountRepository.findByAccountNumber(davidAccountNumber))
                .thenReturn(Optional.of(david));

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        annaAccountNumber,
                        davidAccountNumber,
                        new BigDecimal("600.00")
                )
        );

        // Neither account should be updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));

        // Both in-memory balance should remain unchanged
        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(anna.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("300.00").compareTo(david.getBalance())
        );
    }

    @Test
    void transferShouldRejectWhenDestinationAccountNotFound(){
        long annaAccountNumber = 10004L;
        long missingAccountNumber = 99999L;

        Account anna = new Account(
                4L,
                annaAccountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        when(accountRepository.findByAccountNumber(annaAccountNumber))
                .thenReturn(Optional.of(anna));

        when(accountRepository.findByAccountNumber(missingAccountNumber))
                .thenReturn(Optional.empty());

        // Act and Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        annaAccountNumber,
                        missingAccountNumber,
                        new BigDecimal("200.00")
                )
        );

        assertEquals(
                "Account not found: " + missingAccountNumber,
                exception.getMessage()
        );

        // No account balance should be updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));

        // Check if the source account didn't change
        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(anna.getBalance())
        );
    }

    @Test
    void transferShouldRejectSameSourceAndDestinationAccount(){
        // Arrange
        long accountNumber = 10004L;

        // Act and Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        accountNumber,
                        accountNumber,
                        new BigDecimal("100.00")
                )
        );

        assertEquals(
                "Source and Destination account must be different",
                exception.getMessage()
        );

        // The service should reject the request before looking up accounts.
        verify(accountRepository, never())
                .findByAccountNumber(anyLong());

        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void transferShouldRejectZeroAmount(){
        // Arrange
        long annaAccountNumber = 10004L;
        long davidAccountNumber = 1005L;

        Account anna = new Account(
                annaAccountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        Account david = new Account(
                davidAccountNumber,
                "David",
                new BigDecimal("300.00")
        );

        // Act and Assert
        when(accountRepository.findByAccountNumber(annaAccountNumber))
                .thenReturn(Optional.of(anna));

        when(accountRepository.findByAccountNumber(davidAccountNumber))
                .thenReturn(Optional.of(david));

        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        annaAccountNumber,
                        davidAccountNumber,
                        BigDecimal.ZERO
                )
        );

        // neither account should be persisted
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(anna.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("300.00").compareTo(david.getBalance())
        );
    }

    @Test
    void transferShouldRejectNegativeAmount(){
        // Arrange
        long annaAccountNumber = 10004L;
        long davidAccountNumber = 10005L;

        Account anna = new Account(
                annaAccountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        Account david = new Account(
                davidAccountNumber,
                "David",
                new BigDecimal("300.00")
        );

        when(accountRepository.findByAccountNumber(annaAccountNumber))
                .thenReturn(Optional.of(anna));

        when(accountRepository.findByAccountNumber(davidAccountNumber))
                .thenReturn(Optional.of(david));

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        annaAccountNumber,
                        davidAccountNumber,
                        new BigDecimal("-5.00")
                )
        );

        // Neither account should be updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(anna.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("300.00").compareTo(david.getBalance())
        );
    }

    @Test
    void transferShouldRejectNullAmount(){
        long annaAccountNumber = 10004L;
        long davidAccountNumber = 10005L;

        Account anna = new Account(
                annaAccountNumber,
                "Anna",
                new BigDecimal("500.00")
        );

        Account david = new Account(
                davidAccountNumber,
                "David",
                 new BigDecimal("300.00")
        );

        when(accountRepository.findByAccountNumber(annaAccountNumber))
                .thenReturn(Optional.of(anna));

        when(accountRepository.findByAccountNumber(davidAccountNumber))
                .thenReturn(Optional.of(david));

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(
                        annaAccountNumber,
                        davidAccountNumber,
                        null
                )
        );

        // Neither account should be updated
        verify(accountRepository, never())
                .updateBalance(anyLong(), any(BigDecimal.class));

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(anna.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("300.00").compareTo(david.getBalance())
        );


    }

}

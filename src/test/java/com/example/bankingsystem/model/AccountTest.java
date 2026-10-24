package com.example.bankingsystem.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

//    @Test
//    void depositShouldIncreaseABalance(){
//        // Create an account
//        Account account = new Account(
//            10001L,
//                "John",
//                new BigDecimal("1000.00")
//        );
//
//        // Deposit money
//        account.deposit(new BigDecimal("250.00"));
//
//        // Verify the new balance
//        assertEquals(0, new BigDecimal("1250")
//                .compareTo(account.getBalance()));
//
//    }

    @Test
    public void depositShouldIncreaseBalance(){
        // Arrange: Create an account
        Account account = new Account(
            10001L,
            "John",
                new BigDecimal("1000.00")
        );

        // Act: Deposit money
        account.deposit(new BigDecimal("250.00"));

        // Assert: Verify the new balance
        assertEquals(0, new BigDecimal("1250.00").compareTo(account.getBalance()));

    }

    @Test
    public void withdrawShouldDecreaseBalance(){
        // Arrange: Create an account
        Account account = new Account(
                10001L,
                "John",
                new BigDecimal("1000.00")
        );

        // Act: Withdraw money
        account.withdraw(new BigDecimal("200.00"));

        // Assert
        assertEquals(0, new BigDecimal("800.00").compareTo(account.getBalance()));
    }

    @Test
    public void withdrawShouldRejectInsufficientFunds(){
        // Arrange
        Account account = new Account(
            10001L,
            "John",
            new BigDecimal("1000.00")
        );

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("1200.00"))
        );

        assertEquals(
                0,
                new BigDecimal("1000.00").compareTo(account.getBalance())
        );
    }

    @Test
    public void depositShouldRejectZeroAmount(){
        Account account = new Account(
                10001L,
                "John",
                new BigDecimal("1000.00")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("0.00"))
        );

        assertEquals(
                0,
                new BigDecimal("1000.00").compareTo(account.getBalance())
        );

    }

    @Test
    public void depositShouldRejectNegativeAmount(){
        Account account = new Account(
                10001L,
                "JOhn",
                new BigDecimal("1000.00")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("-100.00"))
        );

        assertEquals(
                0,
                new BigDecimal("1000.00").compareTo(account.getBalance())
        );
    }


    @Test
    void withdrawShouldRejectZeroAmount() {
        Account account = new Account(
                10001L,
                "John",
                new BigDecimal("1000.00")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("0.00"))
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void withdrawShouldRejectNegativeAmount() {
        Account account = new Account(
                10001L,
                "John",
                new BigDecimal("1000.00")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("-100.00"))
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(account.getBalance())
        );
    }


}

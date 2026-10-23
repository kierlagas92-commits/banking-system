package com.example.bankingsystem.repository;

import com.example.bankingsystem.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Account> findByAccountNumber(long accountNumber) {
        String sql = """
                    SELECT id, account_number, account_holder, balance
                    FROM accounts
                    WHERE account_number = ?
                """;

        return jdbcTemplate.query(sql, resultSet -> {
            if (resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong("id"),
                        resultSet.getLong("account_number"),
                        resultSet.getString("account_holder"),
                        resultSet.getBigDecimal("balance")
                );
                return Optional.of(account);
            }

            return Optional.empty();
        }, accountNumber);
    }


    public Account save(Account account) {
        String sql = """
            
                INSERT INTO accounts (account_number, account_holder, balance)
                VALUES (?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setLong(1, account.getAccountNumber());
            ps.setString(2, account.getAccountHolder());
            ps.setBigDecimal(3, account.getBalance());

            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException("Failed to retrieve generated account ID");
        }

        return new Account(
                generatedId.longValue(),
                account.getAccountNumber(),
                account.getAccountHolder(),
                account.getBalance()
        );
    }

    public void updateBalance(long accountNumber, BigDecimal balance){
        String sql = """
                    UPDATE accounts
                    SET balance = ?
                    WHERE account_number = ?
                """;

        jdbcTemplate.update(sql, balance, accountNumber);
    }
}

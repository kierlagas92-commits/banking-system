package com.example.bankingsystem.repository;

import com.example.bankingsystem.model.TransferAudit;
import com.example.bankingsystem.model.TransferStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class TransferAuditRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransferAuditRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

//    public TransferAudit save(TransferAudit transferAudit){
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        String sql = """
//                    INSERT INTO transfer_audit
//                    (from_account, to_account, amount, status, message)
//                    VALUES(?,?,?,?,?)
//                """;
//
//        jdbcTemplate.update(
//                connection -> {
//                    PreparedStatement statement = connection.prepareStatement(
//                            sql,
//                            Statement.RETURN_GENERATED_KEYS
//                    );
//
//                    statement.setLong(1, transferAudit.getFromAccount());
//                    statement.setLong(2, transferAudit.getToAccount());
//                    statement.setBigDecimal(3, transferAudit.getAmount());
//                    statement.setString(4, transferAudit.getStatus().name());
//                    statement.setString(5, transferAudit.getMessage());
//
//                    return statement;
//                },
//                keyHolder
//        );
//
//        // Retrieve the generated database id
//        Number generatedId = keyHolder.getKey();
//
//        if(generatedId == null){
//            throw new IllegalStateException(
//                    "Failed to retrieve generated audit ID"
//            );
//        }
//
//        Long id = generatedId.longValue();
//
//        return jdbcTemplate.queryForObject(
//                """
//                       SELECT id, from_account, to_account, amount,
//                            status, message, created_at
//                       FROM transfer_audit
//                       WHERE id = ?
//                    """,
//                (rs, rowNum) -> new TransferAudit(
//                        rs.getLong("id"),
//                        rs.getLong("from_account"),
//                        rs.getLong("to_account"),
//                        rs.getBigDecimal("amount"),
//                        TransferStatus.valueOf(rs.getString("status")),
//                        rs.getString("message"),
//                        rs.getTimestamp("created_at").toLocalDateTime()
//                ),
//                id
//        );
//    }

    public TransferAudit save(TransferAudit transferAudit){
        // Prepare a container for the generated database id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Define the SQL Statements and its placeholders
        String sql = """
                    INSERT INTO transfer_audit
                    (from_account, to_account, amount, status, message)
                    VALUES(?, ?, ?, ?, ?)
                """;

        // Bind the parameters: Transfer the values from TransferAudit into the SQL Statements
        // Execute the INSERT: JdbcTemplate executes the statement and MySQL generates the id
        jdbcTemplate.update(
                con -> {
                    PreparedStatement statement = con.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

                    statement.setLong(1, transferAudit.getFromAccount());
                    statement.setLong(2, transferAudit.getToAccount());
                    statement.setBigDecimal(3, transferAudit.getAmount());
                    statement.setString(4, transferAudit.getStatus().name());
                    statement.setString(5, transferAudit.getMessage());

                    return statement;
                },
                keyHolder
        );

        // Retrieve the generated ID and convert into Long
        Number generatedId = keyHolder.getKey();

        // Check if the generatedId is null
        if(generatedId == null){
            throw new IllegalStateException(
                    "Failed to generate Audit id"
            );
        }

        Long id = generatedId.longValue();

        // Retrieve the compelete record
        return jdbcTemplate.queryForObject(
                """
                       SELECT id, from_account, to_account, amount, status, message, created_at
                       FROM transfer_audit
                       WHERE id = ?        
                    """,
                (rs, rowNum) -> {
                    return new TransferAudit(
                            rs.getLong("id"),
                            rs.getLong("from_account"),
                            rs.getLong("to_account"),
                            rs.getBigDecimal("amount"),
                            TransferStatus.valueOf(rs.getString("status")),
                            rs.getString("message"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                    , id
        );

    }


}

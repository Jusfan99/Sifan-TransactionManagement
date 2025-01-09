package com.example.transaction.management.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.transaction.management.model.Transaction;
import com.example.transaction.management.model.TransactionDTO;
import com.example.transaction.management.repository.TransactionRepository;
import com.example.transaction.management.util.TransactionMapper;

/**
 * @author jiasifan
 * Created on 2025-01-09
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public int addTransaction(TransactionDTO dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        if (transaction == null) {
            return -1;
        }
        return transactionRepository.save(transaction);
    }

    @Cacheable(value = "transactions", key = "#id")
    public TransactionDTO getTransactionById(long id) {
        Transaction transaction = transactionRepository.findById(id);
        if (transaction != null) {
            return TransactionMapper.toDTO(transaction);
        }
        return null;
    }

    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (!transactions.isEmpty()) {
            return transactions.stream().map(TransactionMapper::toDTO).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<TransactionDTO> getTransactionsWithPagination(int page, int size) {
        List<Transaction> transactions = transactionRepository.findAll().stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        if (!transactions.isEmpty()) {
            return transactions.stream().map(TransactionMapper::toDTO).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @CacheEvict(value = "transactions", key = "#id")
    public int updateTransaction(long id, TransactionDTO dto) {
        if (dto == null || id <= 0) {
            return -1;
        }
        Transaction transaction = TransactionMapper.toEntity(dto);
        return transactionRepository.update(id, transaction);
    }

    @CacheEvict(value = "transactions", key = "#id")
    public boolean deleteTransaction(long id) {
        return transactionRepository.delete(id);
    }

}
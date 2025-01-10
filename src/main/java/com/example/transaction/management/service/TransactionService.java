package com.example.transaction.management.service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.transaction.management.controller.param.TransactionQueryParam;
import com.example.transaction.management.controller.vo.Page;
import com.example.transaction.management.model.Transaction;
import com.example.transaction.management.model.Transaction.TransactionScene;
import com.example.transaction.management.model.Transaction.TransactionStatus;
import com.example.transaction.management.model.Transaction.TransactionType;
import com.example.transaction.management.model.TransactionDTO;
import com.example.transaction.management.repository.TransactionRepository;
import com.example.transaction.management.util.TransactionMapper;

import io.micrometer.common.util.StringUtils;

/**
 * @author jiasifan
 * Created on 2025-01-09
 * 本项目中唯一的service，负责处理交易相关crud业务逻辑，缓存也放在这一层
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public int addTransaction(TransactionDTO dto) {
        Transaction transaction = TransactionMapper.toEntity(dto);
        if (transaction == null || StringUtils.isBlank(transaction.getFromAccountId()) || StringUtils.isBlank(
                transaction.getToAccountId()) || StringUtils.isBlank(transaction.getCurrency())
                || transaction.getAmount() == null) {
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

    @Cacheable(value = "allTransactions")
    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (!transactions.isEmpty()) {
            return transactions.stream().map(TransactionMapper::toDTO).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Cacheable(value = "paginatedTransactions", key = "{#page, #size}")
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

    @CacheEvict(value = {"transactions", "allTransactions", "paginatedTransactions"}, key = "#id")
    public int updateTransaction(long id, TransactionDTO dto) {
        if (dto == null || id <= 0) {
            return -1;
        }
        Transaction transaction = TransactionMapper.toEntity(dto);
        return transactionRepository.update(id, transaction);
    }

    @CacheEvict(value = {"transactions", "allTransactions", "paginatedTransactions"}, key = "#id")
    public boolean deleteTransaction(long id) {
        return transactionRepository.delete(id);
    }

    /**
     * 根据分页请求查询交易记录
     * 支持多种过滤条件和排序方式
     * 建议使用es存储数据，使用redis缓存
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @Cacheable(value = "filteredTransactions", key = "#request.generateCacheKey()")
    public Page<TransactionDTO> findTransactionsByPage(TransactionQueryParam request) {
        List<Transaction> allTransactions = transactionRepository.findAll();
        // 过滤条件
        allTransactions = allTransactions.stream()
                .filter(t -> request.getFromAccountId() == null || t.getFromAccountId()
                        .equals(request.getFromAccountId()))
                .filter(t -> request.getToAccountId() == null || t.getToAccountId().equals(request.getToAccountId()))
                .filter(t -> request.getCurrency() == null || t.getCurrency().equals(request.getCurrency()))
                .filter(t -> request.getType() == null || t.getType() == TransactionType.valueOf(request.getType())
                        .getCode())
                .filter(t -> request.getStatus() == null || t.getStatus() == TransactionStatus.valueOf(
                        request.getStatus()).getCode())
                .filter(t -> request.getScene() == null || t.getScene() == TransactionScene.valueOf(request.getScene())
                        .getCode())
                .filter(t -> request.getCreatedTimeStart() == null
                        || t.getCreatedTime() >= request.getCreatedTimeStart())
                .filter(t -> request.getCreatedTimeEnd() == null || t.getCreatedTime() <= request.getCreatedTimeEnd())
                .filter(t -> request.getInitiatedTimeStart() == null
                        || t.getInitiatedTime() >= request.getInitiatedTimeStart())
                .filter(t -> request.getInitiatedTimeEnd() == null
                        || t.getInitiatedTime() <= request.getInitiatedTimeEnd())
                .filter(t -> request.getProcessedTimeStart() == null
                        || t.getProcessedTime() >= request.getProcessedTimeStart())
                .filter(t -> request.getProcessedTimeEnd() == null
                        || t.getProcessedTime() <= request.getProcessedTimeEnd())
                .filter(t -> request.getCompletedTimeStart() == null
                        || t.getCompletedTime() >= request.getCompletedTimeStart())
                .filter(t -> request.getCompletedTimeEnd() == null
                        || t.getCompletedTime() <= request.getCompletedTimeEnd())
                .filter(t -> request.getFailedTimeStart() == null || t.getFailedTime() >= request.getFailedTimeStart())
                .filter(t -> request.getFailedTimeEnd() == null || t.getFailedTime() <= request.getFailedTimeEnd())
                .collect(Collectors.toList());

        // 排序
        if (request.getSortField() != null && request.getSortOrder() != null) {
            Comparator<Transaction> comparator = (t1, t2) -> {
                try {
                    Field field = Transaction.class.getDeclaredField(request.getSortField());
                    field.setAccessible(true);
                    Comparable value1 = (Comparable) field.get(t1);
                    Comparable value2 = (Comparable) field.get(t2);
                    return request.getSortOrder().equalsIgnoreCase("asc") ? value1.compareTo(value2)
                                                                          : value2.compareTo(value1);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
            allTransactions.sort(comparator);
        }

        // 分页
        int total = allTransactions.size();
        int fromIndex = (request.getPage() - 1) * request.getSize();
        int toIndex = Math.min(fromIndex + request.getSize(), total);

        List<Transaction> pagedTransactions = allTransactions.subList(fromIndex, toIndex);

        if (pagedTransactions.isEmpty()) {
            return new Page<>(Collections.emptyList(), 0, request.getPage(), request.getSize());
        }
        List<TransactionDTO> transactionDTOS = pagedTransactions.stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toList());
        return new Page<>(transactionDTOS, total, request.getPage(), request.getSize());
    }
}
package com.example.transaction.management.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.transaction.management.model.Transaction;

/**
 * @author jiasifan
 * 这个类会模拟Spring Data JPA的CrudRepository接口的功能，但会使用ConcurrentHashMap来存储数据。
 * Created on 2025-01-09
 */
@Repository
public class TransactionRepository {

    private final Map<Long, Transaction> transactionMap = new ConcurrentHashMap<>();

    public int save(Transaction transaction) {
        if (transactionMap.containsKey(transaction.getId())) {
            //重复插入
            return -1;
        }
        transactionMap.put(transaction.getId(), transaction);
        // 实际返回数据库的write结果
        return 1;
    }

    public Transaction findById(long id) {
        return transactionMap.get(id);
    }

    public List<Transaction> findAll() {
        return transactionMap.values().stream().collect(Collectors.toList());
    }

    public int update(long id, Transaction transaction) {
        if (transactionMap.containsKey(id)) {
            transactionMap.put(id, transaction);
            return 1;
        }
        return 0;
    }

    public boolean delete(long id) {
        return transactionMap.remove(id) != null;
    }
}
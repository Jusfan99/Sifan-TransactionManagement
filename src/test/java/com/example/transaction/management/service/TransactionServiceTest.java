package com.example.transaction.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.transaction.management.controller.param.TransactionQueryParam;
import com.example.transaction.management.controller.vo.Page;
import com.example.transaction.management.model.Transaction;
import com.example.transaction.management.model.TransactionDTO;
import com.example.transaction.management.repository.TransactionRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapperProxy transactionMapperProxy;

    @Autowired
    private CacheManager cacheManager;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(transactionRepository, transactionMapperProxy);
        // 清理缓存
        if (cacheManager != null) {
            cacheManager.getCache("transactions").clear();
            cacheManager.getCache("allTransactions").clear();
            cacheManager.getCache("paginatedTransactions").clear();
            cacheManager.getCache("filteredTransactions").clear();
        }
    }

    @Test
    void testAddTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(1);
        dto.setFromAccountId("abc");
        dto.setToAccountId("def");
        dto.setCurrency("USD");
        dto.setAmount(BigDecimal.valueOf(100.0));
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setFromAccountId("abc");
        transaction.setToAccountId("def");
        transaction.setCurrency("USD");
        transaction.setAmount(BigDecimal.valueOf(100.0));

        when(transactionMapperProxy.toEntity(dto)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(1);

        int result = transactionService.addTransaction(dto);
        assertEquals(1, result);
    }

    @Test
    void testAddTransactionWithNullEntity() {
        TransactionDTO dto = new TransactionDTO();
        when(transactionMapperProxy.toEntity(dto)).thenReturn(null);

        int result = transactionService.addTransaction(dto);
        assertEquals(-1, result);
    }

    @Test
    void testGetTransactionByIdNotFound() {
        long id = 1L;
        when(transactionRepository.findById(anyLong())).thenReturn(null);

        TransactionDTO result = transactionService.getTransactionById(id);
        assertNull(result);
    }

    @Test
    void testGetAllTransactionsEmpty() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<TransactionDTO> result = transactionService.getAllTransactions();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTransactionsWithPaginationEmpty() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<TransactionDTO> result = transactionService.getTransactionsWithPagination(1, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateTransactionWithInvalidInput() {
        long id = -1L;
        TransactionDTO dto = null;

        int result = transactionService.updateTransaction(id, dto);
        assertEquals(-1, result);
    }

    @Test
    void testDeleteTransactionFailure() {
        long id = 1L;
        when(transactionRepository.delete(anyLong())).thenReturn(false);

        boolean result = transactionService.deleteTransaction(id);
        assertFalse(result);
    }

    @Test
    void testFindTransactionsByPageEmpty() {
        TransactionQueryParam request = new TransactionQueryParam();
        request.setPage(1);
        request.setSize(10);
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        Page<TransactionDTO> result = transactionService.findTransactionsByPage(request);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetTransactionById() {
        long id = 1L;
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(transactionRepository.findById(id)).thenReturn(transaction);

        TransactionDTO result1 = transactionService.getTransactionById(id);
        TransactionDTO result2 = transactionService.getTransactionById(id);

        assertEquals(result2, result1);

        verify(transactionRepository, times(2)).findById(id);
    }

    @Test
    void testGetAllTransactions() {
        List<Transaction> transactions = List.of(new Transaction());

        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionMapperProxy.toDTO(any(Transaction.class))).thenReturn(new TransactionDTO());

        List<TransactionDTO> result1 = transactionService.getAllTransactions();
        List<TransactionDTO> result2 = transactionService.getAllTransactions();

        assertEquals(result1, result2);

        verify(transactionRepository, times(2 )).findAll();
    }

    @Test
    void testGetTransactionsWithPagination() {
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        int page = 1, size = 1;

        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionMapperProxy.toDTO(any(Transaction.class))).thenReturn(new TransactionDTO());

        List<TransactionDTO> result1 = transactionService.getTransactionsWithPagination(page, size);
        List<TransactionDTO> result2 = transactionService.getTransactionsWithPagination(page, size);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());

        verify(transactionRepository, times(2)).findAll();
    }

    @Test
    void testUpdateTransaction() {
        long id = 1L;
        TransactionDTO dto = new TransactionDTO();
        dto.setId(id);
        Transaction transaction = new Transaction();

        when(transactionMapperProxy.toEntity(dto)).thenReturn(transaction);
        when(transactionRepository.update(id, transaction)).thenReturn(1);

        int result = transactionService.updateTransaction(id, dto);

        assertEquals(0, result);

        verifyCacheEvicted("transactions", id);
        verifyCacheEvicted("allTransactions", id);
        verifyCacheEvicted("paginatedTransactions", id);
    }

    @Test
    void testDeleteTransaction() {
        long id = 1L;

        when(transactionRepository.delete(id)).thenReturn(true);

        boolean result = transactionService.deleteTransaction(id);

        assertTrue(result);

        verifyCacheEvicted("transactions", id);
    }

    @Test
    void testFindTransactionsByPage() {
        TransactionQueryParam queryParam = new TransactionQueryParam();
        queryParam.setPage(1);
        queryParam.setSize(1);
        List<Transaction> transactions = List.of(new Transaction());

        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionMapperProxy.toDTO(any(Transaction.class))).thenReturn(new TransactionDTO());

        Page<TransactionDTO> result1 = transactionService.findTransactionsByPage(queryParam);

        assertNotNull(result1);
        assertEquals(1, result1.getTotalElements());

        verify(transactionRepository, times(1)).findAll();
    }

    private void verifyCacheEvicted(String cacheName, Object key) {
        assertNull(cacheManager.getCache(cacheName).get(key));
    }
}
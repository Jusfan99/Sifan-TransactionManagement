package com.example.transaction.management.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.transaction.management.controller.param.TransactionCreateParam;
import com.example.transaction.management.controller.param.TransactionUpdateParam;
import com.example.transaction.management.controller.vo.TransactionVO;
import com.example.transaction.management.model.TransactionDTO;
import com.example.transaction.management.service.TransactionService;
import com.example.transaction.management.util.TransactionMapper;

/**
 * @author jiasifan
 * Created on 2025-01-09
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<Integer> createTransaction(@RequestBody TransactionCreateParam createParam) {
        TransactionDTO dto = TransactionMapper.toDTO(createParam);
        if (dto == null) {
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
        }
        int result = transactionService.addTransaction(dto);
        if (result <= 0) {
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/getById")
    public ResponseEntity<TransactionVO> getTransaction(@RequestParam long id) {
        TransactionDTO dto = transactionService.getTransactionById(id);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TransactionVO vo = TransactionMapper.toVO(dto);
        return new ResponseEntity<>(vo, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<TransactionVO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        if (transactions == null || transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<TransactionVO> transactionVos = transactions.stream()
                .map(TransactionMapper::toVO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(transactionVos, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Integer> updateTransaction(@RequestBody TransactionUpdateParam updateParam) {
        TransactionDTO transactionDTO = TransactionMapper.toDTO(updateParam);
        if (transactionDTO == null) {
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
        }
        int result = transactionService.updateTransaction(transactionDTO.getId(), transactionDTO);
        if (result <= 0) {
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteTransaction(@RequestParam long id) {
        boolean isDeleted = transactionService.deleteTransaction(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
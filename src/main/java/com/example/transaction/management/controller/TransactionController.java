package com.example.transaction.management.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.example.transaction.management.controller.param.TransactionQueryParam;
import com.example.transaction.management.controller.param.TransactionUpdateParam;
import com.example.transaction.management.controller.vo.Page;
import com.example.transaction.management.controller.vo.TransactionVO;
import com.example.transaction.management.model.ApiResponse;
import com.example.transaction.management.model.ErrorCode;
import com.example.transaction.management.model.TransactionDTO;
import com.example.transaction.management.service.TransactionService;
import com.example.transaction.management.util.TransactionMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiasifan
 * Created on 2025-01-09
 * 很多校验不应该放在这一层，但时间原因先耦合在一起
 */
@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTransaction(
            @RequestBody TransactionCreateParam createParam) {
        TransactionDTO dto = TransactionMapper.toDTO(createParam);
        if (dto == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(ErrorCode.INVALID_PARAMETERS.getCode(), "missing params"),
                    HttpStatus.BAD_REQUEST);
        }
        int result = transactionService.addTransaction(dto);
        if (result <= 0) {
            // 实际需要根据不同的错误码返回不同的错误提示
            if (result == -99) {
                return new ResponseEntity<>(
                        new ApiResponse<>(ErrorCode.DUPLICATE_RESOURCE_CREATE.getCode(), "duplicate insert !"),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(
                    new ApiResponse<>(ErrorCode.INVALID_PARAMETERS.getCode(), "missing params"),
                    HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        return new ResponseEntity<>(new ApiResponse<>(resultMap), HttpStatus.CREATED);
    }

    @GetMapping("/getById")
    public ResponseEntity<ApiResponse<TransactionVO>> getTransaction(@RequestParam long id) {
        TransactionDTO dto = transactionService.getTransactionById(id);
        if (dto == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(ErrorCode.RESOURCE_NOT_FOUND_GET.getCode(), "not found id : " + id),
                    HttpStatus.NO_CONTENT);
        }
        TransactionVO vo = TransactionMapper.toVO(dto);
        return new ResponseEntity<>(new ApiResponse<>(vo), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<TransactionVO>>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        if (transactions == null || transactions.isEmpty()) {
            // 不合理返回，需要记录日志和打点监控
            log.warn("[getAll] return empty list ! ");
            return new ResponseEntity<>(new ApiResponse<>(Collections.emptyList()), HttpStatus.NO_CONTENT);
        }
        List<TransactionVO> transactionVos = transactions.stream()
                .map(TransactionMapper::toVO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ApiResponse<>(transactionVos), HttpStatus.OK);
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<Page<TransactionVO>>> getAllTransactions(
            @RequestBody TransactionQueryParam queryParam) {
        Page<TransactionDTO> dtos = transactionService.findTransactionsByPage(queryParam);
        if (dtos == null || dtos.getContent() == null || dtos.getContent().isEmpty()) {
            Page<TransactionVO> emptyPage = new Page<>(Collections.emptyList(), 0, 0, 0);
            return new ResponseEntity<>(new ApiResponse<>(emptyPage), HttpStatus.OK);
        }
        List<TransactionVO> vos = dtos.getContent().stream()
                .map(TransactionMapper::toVO)
                .collect(Collectors.toList());
        Page<TransactionVO> resultPage =
                new Page<>(vos, dtos.getTotalElements(), dtos.getPageNumber(), dtos.getPageSize());
        return new ResponseEntity<>(new ApiResponse<>(resultPage), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateTransaction(
            @RequestBody TransactionUpdateParam updateParam) {
        TransactionDTO transactionDTO = TransactionMapper.toDTO(updateParam);
        if (transactionDTO == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(ErrorCode.INVALID_PARAMETERS.getCode(), "missing params"),
                    HttpStatus.BAD_REQUEST);
        }
        int result = transactionService.updateTransaction(transactionDTO.getId(), transactionDTO);
        if (result <= 0) {
            if (result == 0) {
                return new ResponseEntity<>(
                        new ApiResponse<>(ErrorCode.RESOURCE_NOT_FOUND_UPDATE.getCode(),
                                "not found id =" + updateParam.getId()),
                        HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(
                    new ApiResponse<>(ErrorCode.UPDATE_CONFLICT.getCode(), "update failed"),
                    HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        return new ResponseEntity<>(new ApiResponse<>(resultMap), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteTransaction(@RequestParam long id) {
        boolean isDeleted = transactionService.deleteTransaction(id);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponse<>("done"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new ApiResponse<>(ErrorCode.RESOURCE_NOT_FOUND_DELETE.getCode(), "not found id = " + id),
                    HttpStatus.NO_CONTENT);
        }
    }

}
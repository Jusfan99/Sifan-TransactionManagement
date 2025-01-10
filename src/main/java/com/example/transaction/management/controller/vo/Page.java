package com.example.transaction.management.controller.vo;

import java.util.List;

/**
 * @author jiasifan
 * Created on 2025-01-10
 * 分页类型
 */
public class Page<T> {
    private List<T> content;
    private int totalElements;
    private int pageNumber;
    private int pageSize;

    public Page(List<T> content, int totalElements, int pageNumber, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }
}
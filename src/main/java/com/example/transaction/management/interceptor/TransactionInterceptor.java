package com.example.transaction.management.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.example.transaction.management.model.HttpContext;

/**
 * @author jiasifan
 * Created on 2025-01-10
 */
public class TransactionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 填充上下文
        HttpContext context = new HttpContext();

        /**
         * 可以在这里根据请求中的信息拦截一些不合法的需求，比如说陌生的 IP 地址，或者请求频率过快等等
         * 也可以做一些后台的权限校验
         * 这里只是简单地模拟一下，直接返回 true
         */
        if (false) {
            return false;
        }
        return true; // 返回 true 表示继续处理请求
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 在请求处理完成后执行
        if (ex != null) {
            // 可以在这里添加一些日志或者打点，记录异常信息
        }

        // 可以在这里添加更多的完成请求后的处理逻辑, 比如做一些后台的写入操作记录
    }
}

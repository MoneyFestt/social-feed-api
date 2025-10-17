package com.example.socialfeedapi.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

import com.example.socialfeedapi.controller.MetricsController;
import com.example.socialfeedapi.controller.ChaosController;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(RequestResponseLoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        Instant start = Instant.now();
        String serverId = System.getenv().getOrDefault("SERVER_ID", UUID.randomUUID().toString());

        // Chaos Monkey
        try {
            ChaosController.maybeThrowChaos();
        } catch (ResponseStatusException e) {
            httpRes.sendError(e.getStatusCode().value(), e.getReason());
            return; // зупиняємо ланцюжок, щоб chain.doFilter не виконувався
        }

        chain.doFilter(request, response);

        long durationMs = Duration.between(start, Instant.now()).toMillis();

        // Логування запиту
        String logEntry = String.format(
                "{ \"timestamp\": \"%s\", \"method\": \"%s\", \"path\": \"%s\", \"status\": %d, \"duration_ms\": %d, \"server_id\": \"%s\" }",
                LocalDateTime.now(), httpReq.getMethod(), httpReq.getRequestURI(),
                httpRes.getStatus(), durationMs, serverId
        );
        logger.info(logEntry);

        // Оновлення метрик
        boolean isError = httpRes.getStatus() >= 400;
        MetricsController.recordRequest(durationMs, isError);

        // Додаємо заголовки
        httpRes.addHeader("X-Response-Time", durationMs + "ms");
        httpRes.addHeader("X-Server-Id", serverId);
    }
}

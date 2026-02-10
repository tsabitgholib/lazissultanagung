package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> hasAgenId(Long agenId) {
        return (root, query, criteriaBuilder) -> {
            if (agenId == null) return null;
            return criteriaBuilder.equal(root.get("agenId"), agenId);
        };
    }

    public static Specification<Transaction> hasEventId(Long eventId) {
        return (root, query, criteriaBuilder) -> {
            if (eventId == null) return null;
            return criteriaBuilder.equal(root.get("eventId"), eventId);
        };
    }

    public static Specification<Transaction> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) return null;
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    public static Specification<Transaction> hasPaymentMethod(String paymentMethod) {
        return (root, query, criteriaBuilder) -> {
            if (paymentMethod == null || paymentMethod.isEmpty()) return null;
            return criteriaBuilder.equal(root.get("method"), paymentMethod);
        };
    }

    public static Specification<Transaction> transactionDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) return null;
            return criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);
        };
    }
    
    public static Specification<Transaction> isDebit() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("debit"), 0);
    }
    
    public static Specification<Transaction> isNotPenyaluran() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("penyaluran"), false);
    }

    public static Specification<Transaction> hasChannel(String channel) {
        return (root, query, criteriaBuilder) -> {
            if (channel == null || channel.isEmpty()) return null;
            return criteriaBuilder.equal(root.get("channel"), channel);
        };
    }

    public static Specification<Transaction> searchByNameOrPhone(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) return null;
            String searchPattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), searchPattern)
            );
        };
    }
}

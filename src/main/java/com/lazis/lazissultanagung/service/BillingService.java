package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.TransactionRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Billing;

public interface BillingService {
    Billing createBilling(String categoryType, Long id, TransactionRequest transactionRequest) throws BadRequestException;
}

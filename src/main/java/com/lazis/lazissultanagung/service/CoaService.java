package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Coa;

import java.util.List;
import java.util.Map;

public interface CoaService {
    List<Coa> getAllParentCoa();

    List<Coa> getAllCoa();

    List<Coa> getAllCoas();

    Coa getCoaById(Long coaId);

    List<Coa> getCoaByAccountType(String accountType);

    Coa createCoa(Coa coa);

    Coa editCoa(Long id, Coa coa);

    ResponseMessage deleteCoa(Long id);

    List<Map<String, Object>> getCoaPengelola();
}

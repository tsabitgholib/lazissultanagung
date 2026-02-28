package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Coa;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoaRepository extends JpaRepository<Coa, Long> {

    @Query("SELECT c FROM Coa c where c.accountType = Asset")
    List<Coa> getCoaAsset();

    @Query("SELECT c FROM Coa c where c.accountType = Liability")
    List<Coa> getCoaLiability();

    @Query("SELECT c FROM Coa c where c.accountType = Equity")
    List<Coa> getCoaEquity();

    @Query("SELECT c FROM Coa c where c.accountType = Revenue")
    List<Coa> getCoaRevenue();

    @Query("SELECT c FROM Coa c where c.accountType = Expense")
    List<Coa> getCoaExpense();

    List<Coa> findByParentAccountIsNotNull(Sort sort);

    List<Coa> findByParentAccountIsNull(Sort sort);

    List<Coa> findByParentAccount_IdIn(List<Long> parentId);

    @Query("SELECT c FROM Coa c WHERE c.parentAccount.id = :parentAccountId order by accountCode ASC")
    List<Coa> findByParentAccountId(@Param("parentAccountId") Long parentAccountId);

    @Query("SELECT c FROM Coa c WHERE c.parentAccount.id = :parentId OR c.id = :coaId order by accountCode ASC")
    List<Coa> findByParentAccount_IdOrId(@Param("parentId") Long parentId, @Param("coaId") Long coaId);

    @Query("SELECT c.id, c.accountCode, c.accountName FROM Coa c WHERE c.parentAccount.id IN (121)")
    List<Object[]> findByParentAccountsPengelola();


}

package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Coa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoaRepository extends JpaRepository<Coa, Long> {

    @Query("SELECT c FROM Coa c where c.accountType = Asset AND c.deletedAt IS NULL")
    List<Coa> getCoaAsset();

    @Query("SELECT c FROM Coa c where c.accountType = Liability AND c.deletedAt IS NULL")
    List<Coa> getCoaLiability();

    @Query("SELECT c FROM Coa c where c.accountType = Equity AND c.deletedAt IS NULL")
    List<Coa> getCoaEquity();

    @Query("SELECT c FROM Coa c where c.accountType = Revenue AND c.deletedAt IS NULL")
    List<Coa> getCoaRevenue();

    @Query("SELECT c FROM Coa c where c.accountType = Expense AND c.deletedAt IS NULL")
    List<Coa> getCoaExpense();

    List<Coa> findByParentAccountIsNotNullAndDeletedAtIsNull(Sort sort);

    List<Coa> findByParentAccountIsNullAndDeletedAtIsNull(Sort sort);

    List<Coa> findByParentAccount_IdInAndDeletedAtIsNull(List<Long> parentId);

    @Query("SELECT c FROM Coa c WHERE c.parentAccount.id = :parentAccountId AND c.deletedAt IS NULL order by accountCode ASC")
    List<Coa> findByParentAccountId(@Param("parentAccountId") Long parentAccountId);

    @Query("SELECT c FROM Coa c WHERE (c.parentAccount.id = :parentId OR c.id = :coaId) AND c.deletedAt IS NULL order by accountCode ASC")
    List<Coa> findByParentAccount_IdOrId(@Param("parentId") Long parentId, @Param("coaId") Long coaId);

    @Query("SELECT c.id, c.accountCode, c.accountName FROM Coa c WHERE c.parentAccount.id IN (121) AND c.deletedAt IS NULL")
    List<Object[]> findByParentAccountsPengelola();

    Page<Coa> findByDeletedAtIsNotNull(Pageable pageable);

    @Query("SELECT c FROM Coa c WHERE c.deletedAt IS NULL")
    List<Coa> findAllActive(Sort sort);
}

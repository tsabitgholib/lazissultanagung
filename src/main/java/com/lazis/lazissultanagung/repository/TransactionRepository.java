package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:month IS NULL OR FUNCTION('MONTH', t.transactionDate) = :month) AND " +
            "(:year IS NULL OR FUNCTION('YEAR', t.transactionDate) = :year) AND " +
            "t.debit != 0 AND " +
            "t.penyaluran = false " +
            "ORDER BY t.transactionId DESC")
    Page<Transaction> findAllByMonthAndYear(
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable);


    @Query("SELECT t FROM Transaction t WHERE t.category = 'campaign' " +
            "AND t.campaign.campaignId = :campaignId AND t.debit != 0 AND t.penyaluran = false ORDER BY t.transactionDate DESC")
    Page<Transaction> findByCampaignId(@Param("campaignId") Long campaignId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.category = 'zakat' " +
            "AND t.zakat.id = :zakatId AND t.debit != 0 AND t.penyaluran = false ORDER BY t.transactionDate DESC ")
    Page<Transaction> findByZakatId(@Param("zakatId") Long zakatId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.category = 'infak' " +
            "AND t.infak.id = :infakId AND t.debit != 0 AND t.penyaluran = false ORDER BY t.transactionDate DESC ")
    Page<Transaction> findByInfakId(@Param("infakId") Long infakId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.category = 'dskl' " +
            "AND t.dskl.id = :dsklId AND t.debit != 0 AND t.penyaluran = false ORDER BY t.transactionDate DESC ")
    Page<Transaction> findByDSKLId(@Param("dsklId") Long dsklId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.category = 'wakaf' " +
            "AND t.wakaf.id = :wakafId AND t.debit != 0 AND t.penyaluran = false ORDER BY t.transactionDate DESC ")
    Page<Transaction> findByWakafId(@Param("wakafId") Long wakafId, Pageable pageable);

    List<Transaction> findByPhoneNumber(String phoneNumber);

    List<Transaction> findByEmail(String email);

    @Query("SELECT COALESCE(SUM(t.debit), 0.0) " +
            "FROM Transaction t WHERE t.penyaluran = false")
    Double totalTransactionAmount();

    @Query("""
       SELECT COUNT(DISTINCT t.transactionId) 
       FROM Transaction t 
       WHERE t.penyaluran = false 
         AND t.debit != 0
       """)
    long getTotalDonatur();


    @Query("SELECT MAX(t.transactionId) FROM Transaction t")
    Integer findLastTransactionNumber();

    List<Transaction> findAllByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.debit), 0.0) FROM Transaction t JOIN t.campaign c WHERE c.admin.id = :adminId")
    double totalTransactionAmountByOperator(@Param("adminId") Long adminId);

    @Query("SELECT COALESCE(COUNT(DISTINCT t.phoneNumber), 0) FROM Transaction t JOIN t.campaign c WHERE c.admin.id = :adminId")
    int getTotalDonaturByOperator(@Param("adminId") Long adminId);

    @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.zakat is not null AND t.penyaluran = false")
    Double totalTransactionZakatAmount();

    @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.infak is not null AND t.penyaluran = false")
    Double totalTransactionInfakAmount();

    @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.wakaf is not null AND t.penyaluran = false")
    Double totalTransactionWakafAmount();

    @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.dskl is not null AND t.penyaluran = false")
    Double totalTransactionDSKLAmount();

    @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.zakat is not null and t.debit != 0 AND t.penyaluran = false")
    long getTotalDonaturZakat();

    @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.infak is not null and t.debit != 0 AND t.penyaluran = false")
    long getTotalDonaturInfak();

    @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.wakaf is not null and t.debit != 0 AND t.penyaluran = false")
    long getTotalDonaturWakaf();

    @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.dskl is not null and t.debit != 0 AND t.penyaluran = false")
    long getTotalDonaturDSKL();

    List<Transaction> findByCoaIdAndTransactionDateBetween(Long coaId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(LOWER(t.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.nomorBukti) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "t.debit != 0 AND t.penyaluran = false")
    Page<Transaction> searchTransactions(@Param("search") String search, Pageable pageable);

    @Query("SELECT SUM(t.debit) FROM Transaction t WHERE (t.email = :email OR t.phoneNumber = :phoneNumber) AND t.debit != 0 AND t.category = :category AND t.penyaluran = false")
    Double sumTransactionByDonaturAndCategory(@Param("email") String email, @Param("phoneNumber") String phoneNumber, @Param("category") String category);


    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
    SELECT 
        t.coa.accountCode AS accountCode, 
        t.coa.accountName AS accountName,
        SUM(CASE WHEN t.transactionDate BETWEEN :startMonth1 AND :endMonth1 THEN t.debit - t.kredit ELSE 0 END) AS totalMonth1,
        SUM(CASE WHEN t.transactionDate BETWEEN :startMonth2 AND :endMonth2 THEN t.debit - t.kredit ELSE 0 END) AS totalMonth2,
        t.coa.parentAccount.accountCode AS parentAccountCode,
        t.coa.parentAccount.accountName AS parentAccountName
    FROM Transaction t
    WHERE (t.zakat IS NOT NULL AND :jenis = 'zakat')
       OR (t.infak IS NOT NULL AND :jenis = 'infak')
       OR (t.wakaf IS NOT NULL AND :jenis = 'wakaf')
       OR (t.dskl IS NOT NULL AND :jenis = 'dskl')
    GROUP BY t.coa.accountCode, t.coa.accountName, t.coa.parentAccount.accountCode, t.coa.parentAccount.accountName
    ORDER BY t.coa.accountCode
""")
    List<Object[]> getAktivitasKeuangan(
            @Param("jenis") String jenis,
            @Param("startMonth1") LocalDateTime startMonth1,
            @Param("endMonth1") LocalDateTime endMonth1,
            @Param("startMonth2") LocalDateTime startMonth2,
            @Param("endMonth2") LocalDateTime endMonth2
    );

    @Query("SELECT SUM(t.debit) FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double sumDebitByCoaIdAndDateRange(@Param("coaId") Long coaId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.debit) FROM Transaction t WHERE t.coa.id = :coaId AND FUNCTION('MONTH', t.transactionDate) = :month AND FUNCTION('YEAR', t.transactionDate) = :year")
    double sumDebitByCoaIdAndMonthYear(@Param("coaId") Long coaId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(t.debit) FROM Transaction t WHERE t.coa.id = :coaId OR t.coa.parentAccount.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double sumDebitByCoaIdAndParentIdAndDateRange(
            @Param("coaId") Long coaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(CASE WHEN t.debit > 0 THEN t.debit ELSE 0 END + CASE WHEN t.kredit > 0 THEN t.kredit ELSE 0 END) " +
            "FROM Transaction t WHERE (t.coa.id = :coaId OR t.coa.parentAccount.id = :coaId) " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double sumDebitOrKreditByCoaIdAndParentIdAndDateRange(
            @Param("coaId") Long coaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(t.debit) FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<Double> sumDebitByCoaIdAndDateRanges(@Param("coaId") Long coaId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.transactionAmount) FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<Double> sumCreditByCoaIdAndDateRange(@Param("coaId") Long coaId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    int countByZakatIdAndDebitGreaterThan(Long zakatId, int value);

    @Query("SELECT t.username, t.transactionDate, t.debit FROM Transaction t where t.debit != 0 AND t.penyaluran = false ORDER BY t.transactionDate DESC")
    List<Object[]> findAllDonaturMinimal();


    List<Transaction> findByNomorBukti(String nomorBukti);
    List<Transaction> findByPenyaluranTrueOrderByNomorBuktiDesc();

}

package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

        @Query("SELECT t FROM Transaction t WHERE " +
                        "(:month IS NULL OR FUNCTION('MONTH', t.transactionDate) = :month) AND " +
                        "(:year IS NULL OR FUNCTION('YEAR', t.transactionDate) = :year) AND " +
                        "t.debit != 0 AND " +
                        "t.penyaluran = false AND " +
                        "t.category != 'hasil bagi bank' AND " +
                        "t.deletedAt IS NULL " +
                        "ORDER BY t.transactionId DESC")
        Page<Transaction> findAllByMonthAndYear(
                        @Param("month") Integer month,
                        @Param("year") Integer year,
                        Pageable pageable);

        @Query("SELECT t FROM Transaction t WHERE t.category = 'campaign' " +
                        "AND t.campaign.campaignId = :campaignId AND t.debit != 0 AND t.penyaluran = false AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC")
        Page<Transaction> findByCampaignId(@Param("campaignId") Long campaignId, Pageable pageable);

        @Query("SELECT t FROM Transaction t WHERE t.category = 'zakat' " +
                        "AND t.zakat.id = :zakatId AND t.debit != 0 AND t.penyaluran = false AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC ")
        Page<Transaction> findByZakatId(@Param("zakatId") Long zakatId, Pageable pageable);

        @Query("SELECT t FROM Transaction t WHERE t.category = 'infak' " +
                        "AND t.infak.id = :infakId AND t.debit != 0 AND t.penyaluran = false AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC ")
        Page<Transaction> findByInfakId(@Param("infakId") Long infakId, Pageable pageable);

        @Query("SELECT t FROM Transaction t WHERE t.category = 'dskl' " +
                        "AND t.dskl.id = :dsklId AND t.debit != 0 AND t.penyaluran = false AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC ")
        Page<Transaction> findByDSKLId(@Param("dsklId") Long dsklId, Pageable pageable);

        @Query("SELECT t FROM Transaction t WHERE t.category = 'wakaf' " +
                        "AND t.wakaf.id = :wakafId AND t.debit != 0 AND t.penyaluran = false AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC ")
        Page<Transaction> findByWakafId(@Param("wakafId") Long wakafId, Pageable pageable);

        @Query("SELECT t FROM Transaction t WHERE t.phoneNumber = :phoneNumber AND t.deletedAt IS NULL")
        List<Transaction> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

        @Query("SELECT t FROM Transaction t WHERE t.email = :email AND t.deletedAt IS NULL")
        List<Transaction> findByEmail(@Param("email") String email);

        @Query("SELECT COALESCE(SUM(t.debit), 0.0) " +
                        "FROM Transaction t WHERE t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double totalTransactionAmount();

        @Query("""
                        SELECT COUNT(DISTINCT t.transactionId)
                        FROM Transaction t
                        WHERE t.penyaluran = false
                          AND t.debit != 0
                          AND t.category != 'hasil bagi bank'
                          AND t.deletedAt IS NULL
                        """)
        long getTotalDonatur();

        @Query("SELECT MAX(t.transactionId) FROM Transaction t")
        Integer findLastTransactionNumber();

        @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        List<Transaction> findAllByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COALESCE(SUM(t.debit), 0.0) FROM Transaction t JOIN t.campaign c WHERE c.admin.id = :adminId AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        double totalTransactionAmountByOperator(@Param("adminId") Long adminId);

        @Query("SELECT COALESCE(COUNT(DISTINCT t.phoneNumber), 0) FROM Transaction t JOIN t.campaign c WHERE c.admin.id = :adminId AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        int getTotalDonaturByOperator(@Param("adminId") Long adminId);

        @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.zakat is not null AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double totalTransactionZakatAmount();

        @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.infak is not null AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double totalTransactionInfakAmount();

        @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.wakaf is not null AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double totalTransactionWakafAmount();

        @Query("select COALESCE(sum(t.debit), 0.0) from Transaction t where t.dskl is not null AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double totalTransactionDSKLAmount();

        @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.zakat is not null and t.debit != 0 AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        long getTotalDonaturZakat();

        @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.infak is not null and t.debit != 0 AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        long getTotalDonaturInfak();

        @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.wakaf is not null and t.debit != 0 AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        long getTotalDonaturWakaf();

        @Query("SELECT t.category, SUM(t.debit), COUNT(t) FROM Transaction t " +
                        "WHERE t.agenId = :agenId AND t.channel = 'POS' AND t.penyaluran = false AND t.debit > 0 AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL " +
                        "GROUP BY t.category")
        List<Object[]> getCategorySummaryByAgenId(@Param("agenId") Long agenId);

        @Query("SELECT t.method, COUNT(t) FROM Transaction t " +
                        "WHERE t.agenId = :agenId AND t.channel = 'POS' AND t.penyaluran = false AND t.debit > 0 AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL " +
                        "GROUP BY t.method")
        List<Object[]> getPaymentMethodSummaryByAgenId(@Param("agenId") Long agenId);

        @Query("SELECT t.eventId, SUM(t.debit) FROM Transaction t " +
                        "WHERE t.agenId = :agenId AND t.channel = 'POS' AND t.penyaluran = false AND t.debit > 0 AND t.eventId IS NOT NULL AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL "
                        +
                        "GROUP BY t.eventId")
        List<Object[]> getEventSummaryByAgenId(@Param("agenId") Long agenId);

        @Query("SELECT COALESCE(SUM(t.debit), 0) FROM Transaction t WHERE t.agenId = :agenId AND t.channel = 'POS' AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double getTotalDonationByAgenId(@Param("agenId") Long agenId);

        @Query("SELECT COALESCE(SUM(t.debit), 0) FROM Transaction t WHERE t.agenId = :agenId AND t.channel = 'POS' AND t.penyaluran = false AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double getTotalDonationByAgenIdAndDateRange(@Param("agenId") Long agenId,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("select COALESCE(count(t.phoneNumber), 0.0) from Transaction t where t.dskl is not null and t.debit != 0 AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        long getTotalDonaturDSKL();

        @Query("SELECT t FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        List<Transaction> findByCoaIdAndTransactionDateBetween(@Param("coaId") Long coaId, @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT t FROM Transaction t WHERE (LOWER(t.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(t.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(t.nomorBukti) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                        "t.debit != 0 AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC")
        Page<Transaction> searchTransactions(@Param("search") String search, Pageable pageable);

        @Query("SELECT SUM(t.debit) FROM Transaction t WHERE (t.email = :email OR t.phoneNumber = :phoneNumber) AND t.debit != 0 AND t.category = :category AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double sumTransactionByDonaturAndCategory(@Param("email") String email,
                        @Param("phoneNumber") String phoneNumber, @Param("category") String category);

        @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        List<Transaction> findByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("""
                            SELECT
                                t.coa.accountCode AS accountCode,
                                t.coa.accountName AS accountName,
                                SUM(CASE WHEN t.transactionDate BETWEEN :startMonth1 AND :endMonth1 THEN t.debit - t.kredit ELSE 0 END) AS totalMonth1,
                                SUM(CASE WHEN t.transactionDate BETWEEN :startMonth2 AND :endMonth2 THEN t.debit - t.kredit ELSE 0 END) AS totalMonth2,
                                t.coa.parentAccount.accountCode AS parentAccountCode,
                                t.coa.parentAccount.accountName AS parentAccountName
                            FROM Transaction t
                            WHERE ((t.zakat IS NOT NULL AND :jenis = 'zakat')
                               OR (t.infak IS NOT NULL AND :jenis = 'infak')
                               OR (t.wakaf IS NOT NULL AND :jenis = 'wakaf')
                               OR (t.dskl IS NOT NULL AND :jenis = 'dskl'))
                               AND t.category != 'hasil bagi bank'
                               AND t.deletedAt IS NULL
                            GROUP BY t.coa.accountCode, t.coa.accountName, t.coa.parentAccount.accountCode, t.coa.parentAccount.accountName
                            ORDER BY t.coa.accountCode
                        """)
        List<Object[]> getAktivitasKeuangan(
                        @Param("jenis") String jenis,
                        @Param("startMonth1") LocalDateTime startMonth1,
                        @Param("endMonth1") LocalDateTime endMonth1,
                        @Param("startMonth2") LocalDateTime startMonth2,
                        @Param("endMonth2") LocalDateTime endMonth2);

        @Query("SELECT COALESCE(SUM(t.debit), 0.0) FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double sumDebitByCoaIdAndDateRange(@Param("coaId") Long coaId, @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COALESCE(SUM(t.debit), 0.0) FROM Transaction t WHERE t.coa.id = :coaId AND FUNCTION('MONTH', t.transactionDate) = :month AND FUNCTION('YEAR', t.transactionDate) = :year AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        double sumDebitByCoaIdAndMonthYear(@Param("coaId") Long coaId, @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT COALESCE(SUM(t.debit), 0.0) FROM Transaction t WHERE (t.coa.id = :coaId OR t.coa.parentAccount.id = :coaId) AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double sumDebitByCoaIdAndParentIdAndDateRange(
                        @Param("coaId") Long coaId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COALESCE(SUM(CASE WHEN t.debit > 0 THEN t.debit ELSE 0 END + CASE WHEN t.kredit > 0 THEN t.kredit ELSE 0 END), 0.0) "
                        +
                        "FROM Transaction t WHERE (t.coa.id = :coaId OR t.coa.parentAccount.id = :coaId) " +
                        "AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Double sumDebitOrKreditByCoaIdAndParentIdAndDateRange(
                        @Param("coaId") Long coaId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(t.debit) FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Optional<Double> sumDebitByCoaIdAndDateRanges(@Param("coaId") Long coaId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(t.transactionAmount) FROM Transaction t WHERE t.coa.id = :coaId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL")
        Optional<Double> sumCreditByCoaIdAndDateRange(@Param("coaId") Long coaId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        int countByZakatIdAndDebitGreaterThan(Long zakatId, int value);

        @Query("SELECT t.username, t.transactionDate, t.debit FROM Transaction t where t.debit != 0 AND t.penyaluran = false AND t.category != 'hasil bagi bank' AND t.deletedAt IS NULL ORDER BY t.transactionDate DESC")
        List<Object[]> findAllDonaturMinimal();

        @Query("SELECT t FROM Transaction t WHERE t.nomorBukti = :nomorBukti AND t.deletedAt IS NULL")
        List<Transaction> findByNomorBukti(@Param("nomorBukti") String nomorBukti);

        @Query("SELECT t FROM Transaction t WHERE t.penyaluran = true AND t.deletedAt IS NULL ORDER BY t.nomorBukti DESC")
        List<Transaction> findByPenyaluranTrueOrderByNomorBuktiDesc();

        @Query("""
                        SELECT SUM(
                            CASE WHEN t.debit > 0 THEN t.debit ELSE 0 END +
                            CASE WHEN t.kredit > 0 THEN t.kredit ELSE 0 END
                        )
                        FROM Transaction t
                        WHERE t.coa.id IN :coaIds
                        AND t.transactionDate BETWEEN :startDate AND :endDate
                        AND t.category != 'hasil bagi bank'
                        AND t.deletedAt IS NULL
                        """)
        Double sumByCoaIdsAndDateRange(
                        @Param("coaIds") List<Long> coaIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                        SELECT COALESCE(SUM(t.debit), 0.0)
                        FROM Transaction t
                        WHERE t.penyaluran = false
                          AND t.debit > 0
                          AND (t.zakat IS NOT NULL
                               OR t.infak IS NOT NULL
                               OR t.dskl IS NOT NULL
                               OR t.campaign IS NOT NULL)
                          AND t.transactionDate BETWEEN :startDate AND :endDate
                          AND t.category != 'hasil bagi bank'
                          AND t.deletedAt IS NULL
                        """)
        Double sumBaseForAmilByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT t FROM Transaction t WHERE t.transactionId IN (" +
                        "SELECT MAX(t2.transactionId) FROM Transaction t2 " +
                        "WHERE t2.channel = 'POS' AND t2.debit > 0 AND t2.category != 'hasil bagi bank' AND t2.deletedAt IS NULL " +
                        "AND (:search IS NULL OR :search = '' OR LOWER(t2.username) LIKE LOWER(CONCAT('%', :search, '%')) OR t2.phoneNumber LIKE CONCAT('%', :search, '%')) "
                        +
                        "GROUP BY t2.username" +
                        ") ORDER BY t.username ASC")
        List<Transaction> findDistinctDonaturPos(@Param("search") String search);

        @Query("""
                        SELECT COALESCE(SUM(t.debit), 0.0)
                        FROM Transaction t
                        WHERE t.penyaluran = false
                        AND t.debit > 0
                        AND t.zakat IS NOT NULL
                        AND t.transactionDate BETWEEN :startDate AND :endDate
                        AND t.category != 'hasil bagi bank'
                        AND t.deletedAt IS NULL
                        """)
        Double sumZakatByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                        SELECT COALESCE(SUM(t.debit), 0.0)
                        FROM Transaction t
                        WHERE t.penyaluran = false
                          AND t.debit > 0
                          AND t.infak IS NOT NULL
                          AND t.transactionDate BETWEEN :startDate AND :endDate
                          AND t.category != 'hasil bagi bank'
                          AND t.deletedAt IS NULL
                        """)
        Double sumInfakByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                        SELECT COALESCE(SUM(t.debit), 0.0)
                        FROM Transaction t
                        WHERE t.penyaluran = false
                          AND t.debit > 0
                          AND t.campaign IS NOT NULL
                          AND t.transactionDate BETWEEN :startDate AND :endDate
                          AND t.category != 'hasil bagi bank'
                          AND t.deletedAt IS NULL
                        """)
        Double sumCampaignByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                        SELECT COALESCE(SUM(t.debit), 0.0)
                        FROM Transaction t
                        WHERE t.penyaluran = false
                          AND t.debit > 0
                          AND t.dskl IS NOT NULL
                          AND t.transactionDate BETWEEN :startDate AND :endDate
                          AND t.category != 'hasil bagi bank'
                          AND t.deletedAt IS NULL
                        """)
        Double sumDSKLByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query(value = "CALL sp_laporan_dana_infak(:bulan1, :bulan2)", nativeQuery = true)
        List<Object[]> getInfakActivityReportNative(@Param("bulan1") String bulan1, @Param("bulan2") String bulan2);

        @Query(value = "CALL sp_laporan_dana_zakat(:bulan1, :bulan2)", nativeQuery = true)
        List<Object[]> getZakatActivityReportNative(@Param("bulan1") String bulan1, @Param("bulan2") String bulan2);

        @Query(value = "CALL sp_laporan_dana_dskl(:bulan1, :bulan2)", nativeQuery = true)
        List<Object[]> getDsklActivityReportNative(@Param("bulan1") String bulan1, @Param("bulan2") String bulan2);

        @Query(value = "CALL sp_laporan_dana_pengelola(:bulan1, :bulan2)", nativeQuery = true)
        List<Object[]> getPengelolaActivityReportNative(@Param("bulan1") String bulan1, @Param("bulan2") String bulan2);

    @Query(value = "SELECT t.nomor_bukti FROM transaction t WHERE t.nomor_bukti LIKE CONCAT('%/', :period) ORDER BY CAST(SUBSTRING_INDEX(t.nomor_bukti, '/', 1) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastNomorBuktiByPeriod(@Param("period") String period);
}

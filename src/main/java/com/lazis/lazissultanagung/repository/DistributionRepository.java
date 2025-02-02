package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Distribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DistributionRepository extends JpaRepository<Distribution, Long> {

    List<Distribution> findByCategoryAndCampaign_CampaignId(String category, Long campaignId);
    List<Distribution> findByCategoryAndZakat_Id(String category, Long zakatId);
    List<Distribution> findByCategoryAndInfak_Id(String category, Long infakId);
    List<Distribution> findByCategoryAndWakaf_Id(String category, Long wakafId);
    List<Distribution> findByCategoryAndDskl_Id(String category, Long dsklId);

    @Query("SELECT d FROM Distribution d WHERE " +
            "(:month IS NULL OR FUNCTION('MONTH', d.distributionDate) = :month) AND " +
            "(:year IS NULL OR FUNCTION('YEAR', d.distributionDate) = :year) " +
            "ORDER BY d.distributionDate DESC")
    Page<Distribution> findAllByMonthAndYear(
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.distributionAmount), 0.0) AS Total_distribusi FROM Distribution d")
    Double totalDistributionAmount();


    @Query("SELECT COALESCE(COUNT(DISTINCT d.receiver), 0.0) AS penerima_manfaat FROM Distribution d")
    long totalDistributionReceiver();

    @Query("SELECT COALESCE(SUM(d.distributionAmount), 0.0) FROM Distribution d JOIN d.campaign c WHERE c.admin.id = :adminId")
    double totalDistributionAmountByOperator(@Param("adminId") Long adminId);

    @Query("SELECT COALESCE(COUNT(DISTINCT d.receiver), 0.0) FROM Distribution d JOIN d.campaign c WHERE c.admin.id = :adminId")
    int totalDistributionReceiverByOperator(@Param("adminId") Long adminId);

    @Query("SELECT COALESCE(SUM(d.distributionAmount), 0.0) AS Total_distribusi FROM Distribution d where d.zakat is not null")
    Double totalDistributionZakatAmount();

    @Query("SELECT COALESCE(SUM(d.distributionAmount), 0.0) AS Total_distribusi FROM Distribution d where d.infak is not null")
    Double totalDistributionInfakAmount();

    @Query("SELECT COALESCE(SUM(d.distributionAmount), 0.0) AS Total_distribusi FROM Distribution d where d.wakaf is not null")
    Double totalDistributionWakafAmount();

    @Query("SELECT COALESCE(SUM(d.distributionAmount), 0.0) AS Total_distribusi FROM Distribution d where d.dskl is not null")
    Double totalDistributionDSKLAmount();

    @Query("SELECT COALESCE(COUNT(DISTINCT d.receiver), 0.0) AS penerima_manfaat FROM Distribution d where d.zakat is not null")
    long totalDistributionZakatReceiver();

    @Query("SELECT COALESCE(COUNT(DISTINCT d.receiver), 0.0) AS penerima_manfaat FROM Distribution d where d.infak is not null")
    long totalDistributionInfakReceiver();

    @Query("SELECT COALESCE(COUNT(DISTINCT d.receiver), 0.0) AS penerima_manfaat FROM Distribution d where d.wakaf is not null")
    long totalDistributionWakafReceiver();

    @Query("SELECT COALESCE(COUNT(DISTINCT d.receiver), 0.0) AS penerima_manfaat FROM Distribution d where d.dskl is not null")
    long totalDistributionDSKLReceiver();
}

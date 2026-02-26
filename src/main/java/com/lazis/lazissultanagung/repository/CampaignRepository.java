package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Campaign;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;



@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findAll(Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.campaignCategory.campaignCategory = :campaignCategory " +
            "AND c.active = true " +
            "AND c.approved = true ORDER BY c.campaignId DESC")
    Page<Campaign> findByCategoryName(@Param("campaignCategory") String campaignCategory, Pageable pageable);

    Page<Campaign> findByAdmin(Admin admin, Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.approved = true AND c.admin = :admin ORDER BY c.campaignId DESC")
    Page<Campaign> findActiveApproveCampaignOperator(Admin admin, Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.approved = false AND c.admin = :admin ORDER BY c.campaignId DESC")
    Page<Campaign> findPendingCampaignOperator(Admin admin, Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.active = false AND c.approved = true AND c.admin = :admin ORDER BY c.campaignId DESC")
    Page<Campaign> findHistoryCampaignOperator(Admin admin, Pageable pageable);

//    List<Campaign> findByApproved(boolean approved);
//    List<Campaign> findCampaignByActive(boolean isActive);

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.approved = true ORDER BY c.emergency DESC, c.campaignId DESC")
    Page<Campaign> findCampaignByActiveAndApproved(Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.approved = true AND c.emergency = true ORDER BY c.campaignId DESC")
    Page<Campaign> findCampaignByEmergency(Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.approved = false ORDER BY c.campaignId DESC")
    Page<Campaign> findPendingCampaign(Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.active = false AND c.approved = true ORDER BY c.campaignId DESC")
    Page<Campaign> findHistoryCampaign(Pageable pageable);
//
//    Campaign findByCampaignCode(String campaignCode);
//
//    Campaign findById(long campaignId);
//
    @Query("SELECT c FROM Campaign c WHERE LOWER(c.campaignName) LIKE LOWER(CONCAT('%', :campaignName, '%')) AND c.approved = true AND c.active = true")
    Page<Campaign> findByCampaignName(@Param("campaignName") String campaignName, Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE LOWER(c.campaignName) LIKE LOWER(CONCAT('%', :campaignName, '%')) AND c.approved = false AND c.active = true")
    Page<Campaign> findByCampaignNamePending(@Param("campaignName") String campaignName, Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE LOWER(c.campaignName) LIKE LOWER(CONCAT('%', :campaignName, '%')) AND c.approved = true AND c.active = false")
    Page<Campaign> findByCampaignNameNonaktif(@Param("campaignName") String campaignName, Pageable pageable);
//
//    @Query("SELECT c FROM Campaign c WHERE YEAR(c.startDate) = :year")
//    Page<Campaign> findByYear(@Param("year") int year, Pageable pageable);
//
    @Query("SELECT \n" +
            " c.campaignId,\n" +
            " c.campaignName,\n" +
            " c.location,\n" +
            " c.targetAmount,\n" +
            " c.currentAmount,\n" +
            " c.currentAmount * 0.125 AS amil,\n" +
            " c.active\n" +
            "FROM \n" +
            " Campaign c\n" +
            "GROUP BY c.campaignId, c.currentAmount \n" +
            "ORDER BY c.campaignId DESC"
    )
    Page<Object []> getAmilCampaign(Pageable pageable);
//
//    @Query("SELECT SUM(c.currentAmount) AS totalCampaignTransactionAmount, " +
//            "SUM(c.currentAmount * 0.15) AS totalAmil, " +
//            "SUM(c.distribution) AS totalCampaignDistributionAmount " +
//            "FROM Campaign c")
//    Optional<Map<String, Double>> getSummaryCampaign();
//
//    @Query("SELECT c FROM Campaign c WHERE c.creator IN (SELECT sa FROM SubAdmin sa WHERE sa.serviceOffice.serviceOfficeId = :serviceOfficeId) ORDER BY c.campaignId DESC")
//    Page<Campaign> findCampaignsByServiceOfficeId(@Param("serviceOfficeId") long serviceOfficeId, Pageable pageable);
//
//    Page<Campaign> findAllByApprovedIsTrue(Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Campaign c SET c.currentAmount = c.currentAmount + :transactionAmount WHERE c.campaignId = :campaignId")
    void updateCampaignCurrentAmount(@Param("campaignId") Long campaignId, @Param("transactionAmount") double transactionAmount);

    @Transactional
    @Modifying
    @Query("UPDATE Campaign c SET c.distribution = c.distribution + :distributionAmount WHERE c.campaignId = :campaignId")
    void updateCampaignDistribution(@Param("campaignId") Long campaignId, @Param("distributionAmount") double distributionAmount);

    @Query("SELECT COALESCE(SUM(c.targetAmount), 0.0) FROM Campaign c")
    double getTotalTargetAmount();

    @Query("SELECT COALESCE(SUM(c.currentAmount), 0.0) FROM Campaign c")
    double getTotalCurrentAmount();

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.approved = true AND c.priority = true ORDER BY c.campaignId DESC")
    List<Campaign> findCampaignByPriority();

    @Query("SELECT c.campaignName, c.currentAmount FROM Campaign c where c.approved = true")
    List<Object[]> findAllPenghimpunan();

}

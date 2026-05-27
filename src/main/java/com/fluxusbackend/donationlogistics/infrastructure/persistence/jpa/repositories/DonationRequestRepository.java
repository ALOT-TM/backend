package com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.DonationRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {

    @Query("select s from DonationRequest s where s.beneficiaryReferenceId.value = :beneficiaryId")
    List<DonationRequest> findByBeneficiaryId(@Param("beneficiaryId") Long beneficiaryId);

    @Query("select s from DonationRequest s where s.shrinkageReferenceId.value = :shrinkageId")
    List<DonationRequest> findByShrinkageId(@Param("shrinkageId") Long shrinkageId);

    @Query("select s from DonationRequest s where s.companyId.value = :companyId")
    List<DonationRequest> findByCompanyId(@Param("companyId") Long companyId);

    @Query("select s from DonationRequest s, Shrinkage m where s.shrinkageReferenceId.value = m.id and m.name = :productName")
    List<DonationRequest> findByProductName(@Param("productName") String productName);
}


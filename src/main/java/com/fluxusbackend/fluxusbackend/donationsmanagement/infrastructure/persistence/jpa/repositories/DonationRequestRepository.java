package com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.DonationRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {

    @Query("select s from DonationRequest s where s.beneficiaryReferenceId.value = :beneficiaryId")
    List<DonationRequest> findByBeneficiaryId(@Param("beneficiaryId") Long beneficiaryId);

    @Query("select s from DonationRequest s where s.mermaReferenceId.value = :mermaId")
    List<DonationRequest> findByMermaId(@Param("mermaId") Long mermaId);

    @Query("select s from DonationRequest s where s.companyId.value = :companyId")
    List<DonationRequest> findByCompanyId(@Param("companyId") Long companyId);

    @Query("select s from DonationRequest s inner join Merma m on s.mermaReferenceId.value = m.id where m.productName.value = :productName")
    List<DonationRequest> findByProductName(@Param("productName") String productName);
}

package com.frontstep.deal_analyzer.repository;

import com.frontstep.deal_analyzer.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {
    Optional<Organization> findBySlug(String slug);
    Optional<Organization> findByClerkOrganizationId(String clerkOrganizationId);
}

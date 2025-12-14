package com.frontstep.deal_analyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @Column(name = "clerk_organization_id", unique = true, nullable = false)
    private String clerkOrganizationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

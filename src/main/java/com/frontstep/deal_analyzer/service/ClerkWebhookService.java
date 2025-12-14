package com.frontstep.deal_analyzer.service;

import com.frontstep.deal_analyzer.entity.Organization;
import com.frontstep.deal_analyzer.entity.User;
import com.frontstep.deal_analyzer.repository.OrganizationRepository;
import com.frontstep.deal_analyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClerkWebhookService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public void handleUserCreated(Map<String, Object> data) {
        log.info("Processing user.created event");

        String clerkUserId = (String) data.get("id");
        List<Map<String, Object>> emailAddresses = (List<Map<String, Object>>) data.get("email_addresses");
        String email = emailAddresses != null && !emailAddresses.isEmpty()
            ? (String) emailAddresses.get(0).get("email_address")
            : null;

        String firstName = (String) data.get("first_name");
        String lastName = (String) data.get("last_name");
        String profileImageUrl = (String) data.get("image_url");

        // Extract Microsoft OAuth tokens if present
        Map<String, Object> publicMetadata = (Map<String, Object>) data.get("public_metadata");
        String microsoftAccessToken = null;
        String microsoftRefreshToken = null;
        Instant microsoftTokenExpiry = null;

        if (publicMetadata != null) {
            Map<String, Object> oauth = (Map<String, Object>) publicMetadata.get("oauth_microsoft");
            if (oauth != null) {
                microsoftAccessToken = (String) oauth.get("access_token");
                microsoftRefreshToken = (String) oauth.get("refresh_token");
                Object expiresAt = oauth.get("expires_at");
                if (expiresAt != null) {
                    microsoftTokenExpiry = Instant.ofEpochSecond(((Number) expiresAt).longValue());
                }
            }
        }

        User user = User.builder()
            .clerkUserId(clerkUserId)
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .profileImageUrl(profileImageUrl)
            .microsoftAccessToken(microsoftAccessToken)
            .microsoftRefreshToken(microsoftRefreshToken)
            .microsoftTokenExpiry(microsoftTokenExpiry)
            .build();

        userRepository.save(user);
        log.info("User created: {}", clerkUserId);
    }

    @Transactional
    public void handleUserUpdated(Map<String, Object> data) {
        log.info("Processing user.updated event");

        String clerkUserId = (String) data.get("id");
        User user = userRepository.findByClerkUserId(clerkUserId)
            .orElseThrow(() -> new RuntimeException("User not found: " + clerkUserId));

        List<Map<String, Object>> emailAddresses = (List<Map<String, Object>>) data.get("email_addresses");
        if (emailAddresses != null && !emailAddresses.isEmpty()) {
            user.setEmail((String) emailAddresses.get(0).get("email_address"));
        }

        user.setFirstName((String) data.get("first_name"));
        user.setLastName((String) data.get("last_name"));
        user.setProfileImageUrl((String) data.get("image_url"));

        // Update Microsoft OAuth tokens if present
        Map<String, Object> publicMetadata = (Map<String, Object>) data.get("public_metadata");
        if (publicMetadata != null) {
            Map<String, Object> oauth = (Map<String, Object>) publicMetadata.get("oauth_microsoft");
            if (oauth != null) {
                user.setMicrosoftAccessToken((String) oauth.get("access_token"));
                user.setMicrosoftRefreshToken((String) oauth.get("refresh_token"));
                Object expiresAt = oauth.get("expires_at");
                if (expiresAt != null) {
                    user.setMicrosoftTokenExpiry(Instant.ofEpochSecond(((Number) expiresAt).longValue()));
                }
            }
        }

        userRepository.save(user);
        log.info("User updated: {}", clerkUserId);
    }

    @Transactional
    public void handleUserDeleted(Map<String, Object> data) {
        log.info("Processing user.deleted event");

        String clerkUserId = (String) data.get("id");
        userRepository.deleteById(clerkUserId);
        log.info("User deleted: {}", clerkUserId);
    }

    @Transactional
    public void handleOrganizationCreated(Map<String, Object> data) {
        log.info("Processing organization.created event");

        String clerkOrgId = (String) data.get("id");
        String name = (String) data.get("name");
        String slug = (String) data.get("slug");
        String logoUrl = (String) data.get("logo_url");

        Organization organization = Organization.builder()
            .clerkOrganizationId(clerkOrgId)
            .name(name)
            .slug(slug)
            .logoUrl(logoUrl)
            .build();

        organizationRepository.save(organization);
        log.info("Organization created: {}", clerkOrgId);
    }

    @Transactional
    public void handleOrganizationUpdated(Map<String, Object> data) {
        log.info("Processing organization.updated event");

        String clerkOrgId = (String) data.get("id");
        Organization organization = organizationRepository.findByClerkOrganizationId(clerkOrgId)
            .orElseThrow(() -> new RuntimeException("Organization not found: " + clerkOrgId));

        organization.setName((String) data.get("name"));
        organization.setSlug((String) data.get("slug"));
        organization.setLogoUrl((String) data.get("logo_url"));

        organizationRepository.save(organization);
        log.info("Organization updated: {}", clerkOrgId);
    }

    @Transactional
    public void handleOrganizationDeleted(Map<String, Object> data) {
        log.info("Processing organization.deleted event");

        String clerkOrgId = (String) data.get("id");
        organizationRepository.deleteById(clerkOrgId);
        log.info("Organization deleted: {}", clerkOrgId);
    }

    @Transactional
    public void handleOrganizationMembershipCreated(Map<String, Object> data) {
        log.info("Processing organizationMembership.created event");

        Map<String, Object> orgData = (Map<String, Object>) data.get("organization");
        Map<String, Object> userData = (Map<String, Object>) data.get("public_user_data");

        if (orgData != null && userData != null) {
            String clerkOrgId = (String) orgData.get("id");
            String clerkUserId = (String) userData.get("user_id");

            Organization organization = organizationRepository.findByClerkOrganizationId(clerkOrgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + clerkOrgId));

            User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + clerkUserId));

            user.setOrganization(organization);
            userRepository.save(user);
            log.info("User {} added to organization {}", clerkUserId, clerkOrgId);
        }
    }

    @Transactional
    public void handleOrganizationMembershipDeleted(Map<String, Object> data) {
        log.info("Processing organizationMembership.deleted event");

        Map<String, Object> userData = (Map<String, Object>) data.get("public_user_data");

        if (userData != null) {
            String clerkUserId = (String) userData.get("user_id");

            User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + clerkUserId));

            user.setOrganization(null);
            userRepository.save(user);
            log.info("User {} removed from organization", clerkUserId);
        }
    }
}

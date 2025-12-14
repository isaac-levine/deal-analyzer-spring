package com.frontstep.deal_analyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontstep.deal_analyzer.dto.ClerkWebhookEvent;
import com.frontstep.deal_analyzer.service.ClerkWebhookService;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/clerk")
@RequiredArgsConstructor
@Slf4j
public class ClerkWebhookController {

    private final ClerkWebhookService webhookService;
    private final ObjectMapper objectMapper;

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
        @RequestBody String payload,
        @RequestHeader("svix-id") String svixId,
        @RequestHeader("svix-timestamp") String svixTimestamp,
        @RequestHeader("svix-signature") String svixSignature
    ) {
        log.info("Received Clerk webhook");

        // Verify webhook signature
        try {
            Webhook webhook = new Webhook(webhookSecret);

            // Build HttpHeaders for Svix verification
            Map<String, List<String>> headersMap = new HashMap<>();
            headersMap.put("svix-id", List.of(svixId));
            headersMap.put("svix-timestamp", List.of(svixTimestamp));
            headersMap.put("svix-signature", List.of(svixSignature));
            HttpHeaders headers = HttpHeaders.of(headersMap, (k, v) -> true);

            webhook.verify(payload, headers);
        } catch (WebhookVerificationException e) {
            log.error("Webhook verification failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        // Parse and handle the event
        try {
            ClerkWebhookEvent event = objectMapper.readValue(payload, ClerkWebhookEvent.class);
            log.info("Processing event type: {}", event.getType());

            switch (event.getType()) {
                case "user.created":
                    webhookService.handleUserCreated(event.getData());
                    break;
                case "user.updated":
                    webhookService.handleUserUpdated(event.getData());
                    break;
                case "user.deleted":
                    webhookService.handleUserDeleted(event.getData());
                    break;
                case "organization.created":
                    webhookService.handleOrganizationCreated(event.getData());
                    break;
                case "organization.updated":
                    webhookService.handleOrganizationUpdated(event.getData());
                    break;
                case "organization.deleted":
                    webhookService.handleOrganizationDeleted(event.getData());
                    break;
                case "organizationMembership.created":
                    webhookService.handleOrganizationMembershipCreated(event.getData());
                    break;
                case "organizationMembership.deleted":
                    webhookService.handleOrganizationMembershipDeleted(event.getData());
                    break;
                default:
                    log.warn("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing webhook: " + e.getMessage());
        }
    }
}

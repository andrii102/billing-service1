package dre.billingservice.dto;

import dre.billingservice.model.MembershipStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MembershipDTO(
        Long id,
        Long userId,
        Long membershipPlanId,
        MembershipStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean autoRenew
) { }

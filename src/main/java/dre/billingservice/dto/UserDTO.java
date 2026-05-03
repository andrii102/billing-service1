package dre.billingservice.dto;

import dre.billingservice.model.UserRole;
import dre.billingservice.model.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String accessCardId,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String address,
        UserStatus status
) {
}

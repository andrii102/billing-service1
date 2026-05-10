package dre.billingservice.client;

import dre.billingservice.dto.MembershipDTO;
import dre.billingservice.model.MembershipStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "membership-service", url = "${membership-service.url:http://localhost:8081}", configuration = FeignConfig.class)
public interface MembershipServiceClient {

    @GetMapping("/api/memberships/{id}")
    MembershipDTO getMembershipById(@PathVariable Long id);

    @PutMapping("/api/memberships/{id}")
    void setMembershipStatus(@PathVariable Long id, @RequestParam MembershipStatus status);
}

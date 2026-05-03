package dre.billingservice.client;

import dre.billingservice.dto.MembershipDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "membership-service", url = "${membership-service.url:http://localhost:8081}", configuration = FeignConfig.class)
public interface MembershipServiceClient {

    @GetMapping("/api/memberships/{id}")
    MembershipDTO getMembershipById(@PathVariable Long id);

}

package com.thomazcollet.usermanagementauthapp.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.thomazcollet.usermanagementauthapp.infra.feign.dto.ViaCepResponse;

@FeignClient(name = "viaCepClient", url = "https://viacep.com.br/ws")
public interface ViaCepClient {

    @GetMapping("/{zipCode}/json/")
    ViaCepResponse getAddressByZipCode(@PathVariable("zipCode") String zipCode);
}
package com.thomazcollet.usermanagementauthapp.infra.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String regioes,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        Boolean erro // A API do ViaCEP retorna {"erro": "true"} quando o CEP é válido em formato mas
                     // não existe
) {
}

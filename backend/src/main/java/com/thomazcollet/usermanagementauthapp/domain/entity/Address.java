package com.thomazcollet.usermanagementauthapp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Representa a tabela tb_addresses no banco de dados.
 */
@Entity
@Table(name = "tb_addresses")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id") // Boa prática: Equals/HashCode baseado apenas na PK
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Correto para PostgreSQL (SERIAL/BIGSERIAL)
    private Long id;

    @NotBlank
    @Size(min = 8, max = 9)
    @Column(name = "zip_code", nullable = false, length = 9)
    private String zipCode;

    @NotBlank
    @Size(max = 150)
    @Column(name = "street", nullable = false, length = 150)
    private String street;

    @Size(max = 20)
    @Column(name = "number", length = 20)
    private String number; // String permite "S/N", "102-B", etc.

    @Size(max = 100)
    @Column(name = "complement", length = 100)
    private String complement;

    @NotBlank
    @Size(max = 100)
    @Column(name = "neighborhood", nullable = false, length = 100)
    private String neighborhood;

    @NotBlank
    @Size(max = 100)
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "state", nullable = false, length = 2)
    private String state; // Guardará a sigla da UF (ex: "SP", "RJ")

    // Método de negócio para atualizar o endereço com dados enriquecidos pelo
    // ViaCEP
    public void updateDetails(String zipCode, String street, String number,
            String complement, String neighborhood, String city, String state) {
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
    }
}
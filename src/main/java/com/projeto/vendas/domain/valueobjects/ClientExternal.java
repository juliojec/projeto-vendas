package com.projeto.vendas.domain.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Embeddable
@Getter
@ToString
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ClientExternal {

    private String id;
    private String description;

    public ClientExternal(String id, String description) {
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo");
        this.description = Objects.requireNonNull(description, "Descrição não pode ser nula");

        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID não pode ser vazio");
        }

        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientExternal that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
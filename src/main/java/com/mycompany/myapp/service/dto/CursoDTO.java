package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatusCurso;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Curso} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CursoDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String nome;

    private String descricao;

    @NotNull(message = "must not be null")
    private StatusCurso status;

    @NotNull(message = "must not be null")
    private Instant dataCriacao;

    private Instant dataInatividade;

    private AreaDTO area;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusCurso getStatus() {
        return status;
    }

    public void setStatus(StatusCurso status) {
        this.status = status;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Instant getDataInatividade() {
        return dataInatividade;
    }

    public void setDataInatividade(Instant dataInatividade) {
        this.dataInatividade = dataInatividade;
    }

    public AreaDTO getArea() {
        return area;
    }

    public void setArea(AreaDTO area) {
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CursoDTO)) {
            return false;
        }

        CursoDTO cursoDTO = (CursoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cursoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CursoDTO{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", descricao='" + getDescricao() + "'" +
            ", status='" + getStatus() + "'" +
            ", dataCriacao='" + getDataCriacao() + "'" +
            ", dataInatividade='" + getDataInatividade() + "'" +
            ", area=" + getArea() +
            "}";
    }
}

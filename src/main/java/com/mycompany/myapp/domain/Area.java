package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.StatusCurso;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Area.
 */
@Table("area")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nome")
    private String nome;

    @Column("descricao")
    private String descricao;

    @NotNull(message = "must not be null")
    @Column("status")
    private StatusCurso status;

    @NotNull(message = "must not be null")
    @Column("data_criacao")
    private Instant dataCriacao;

    @Column("data_inatividade")
    private Instant dataInatividade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Area id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Area nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public Area descricao(String descricao) {
        this.setDescricao(descricao);
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusCurso getStatus() {
        return this.status;
    }

    public Area status(StatusCurso status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(StatusCurso status) {
        this.status = status;
    }

    public Instant getDataCriacao() {
        return this.dataCriacao;
    }

    public Area dataCriacao(Instant dataCriacao) {
        this.setDataCriacao(dataCriacao);
        return this;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Instant getDataInatividade() {
        return this.dataInatividade;
    }

    public Area dataInatividade(Instant dataInatividade) {
        this.setDataInatividade(dataInatividade);
        return this;
    }

    public void setDataInatividade(Instant dataInatividade) {
        this.dataInatividade = dataInatividade;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Area)) {
            return false;
        }
        return id != null && id.equals(((Area) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Area{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", descricao='" + getDescricao() + "'" +
            ", status='" + getStatus() + "'" +
            ", dataCriacao='" + getDataCriacao() + "'" +
            ", dataInatividade='" + getDataInatividade() + "'" +
            "}";
    }
}

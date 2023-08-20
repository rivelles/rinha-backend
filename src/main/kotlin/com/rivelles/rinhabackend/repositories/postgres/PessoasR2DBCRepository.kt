package com.rivelles.rinhabackend.repositories.postgres

import com.rivelles.rinhabackend.repositories.postgres.entities.Pessoa
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class PessoasR2DBCRepository(val databaseClient: DatabaseClient) {
    fun count(): Mono<Long> = databaseClient.sql("SELECT COUNT(*) as numero_pessoas FROM pessoas")
        .fetch()
        .one()
        .mapNotNull {
            it["numero_pessoas"]?.let { numeroPessoas -> numeroPessoas as Long } ?: 0
        }

    fun findByExternalId(externalId: String): Mono<Pessoa?> = databaseClient.sql("SELECT * FROM pessoas WHERE external_id = :external_id")
        .bind("external_id", externalId)
        .fetch()
        .one()
        .map { row ->
            if (row.isEmpty()) null
            row.toPessoa()
        }

    fun save(pessoa: Pessoa): Mono<Long> {
        return databaseClient
            .sql {
                """
                        INSERT INTO pessoas (external_id, apelido, nome, nascimento, stacks) 
                        VALUES (:external_id, :apelido, :nome, :nascimento, :stacks)
                    """.trimIndent()
            }
            .bind("external_id", pessoa.externalId)
            .bind("apelido", pessoa.apelido)
            .bind("nome", pessoa.nome)
            .bind("nascimento", pessoa.nascimento)
            .bind("stacks", pessoa.stacks)
            .fetch()
            .rowsUpdated()
    }

    fun fetchByTerm(term: String): Flux<Pessoa> = databaseClient.sql("SELECT * FROM PESSOAS WHERE TS @@ to_tsquery(:term) LIMIT 50")
        .bind("term", term)
        .fetch()
        .all()
        .map { row ->
            if (row.isEmpty()) null
            row.toPessoa()
        }
}

fun Map<String, Any>.toPessoa() =
    Pessoa(
        externalId = this["external_id"] as String,
        apelido = this["apelido"] as String,
        nome = this["nome"] as String,
        nascimento = this["nascimento"] as String,
        stacks = this["stacks"] as String
    )

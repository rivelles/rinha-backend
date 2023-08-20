package com.rivelles.rinhabackend.api

import com.rivelles.rinhabackend.repositories.postgres.PessoasR2DBCRepository
import com.rivelles.rinhabackend.repositories.postgres.entities.Pessoa
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.net.URI
import java.util.UUID

@Configuration
class PessoasApi(val handler: Handler) {
    @Bean
    fun route() = router {
        POST("/pessoas", handler::create)
        GET("/pessoas/{externalId}", handler::fetchByExternalID)
        GET("/pessoas", handler::fetchByTerm)
        GET("/contagem-pessoas", handler::fetchCount)
    }
}

@Component
class Handler(val pessoasR2DBCRepository: PessoasR2DBCRepository) {
    fun create(request: ServerRequest): Mono<ServerResponse> {
        val pessoaMono = request.bodyToMono(PessoaVM::class.java).doOnNext(PessoaVM::validate)
        return pessoaMono.flatMap { pessoaRequest ->
            val pessoa = Pessoa(
                externalId = UUID.randomUUID().toString(),
                apelido = pessoaRequest.apelido!!.toString(),
                nome = pessoaRequest.nome!!.toString(),
                nascimento = pessoaRequest.nascimento!!.toString(),
                stacks = pessoaRequest.stack?.joinToString(",")
            )
            pessoasR2DBCRepository.save(pessoa).flatMap { savedRows ->
                if (savedRows > 0L) created(URI.create("/pessoas/${pessoa.externalId}")).build()
                else badRequest().build()
            }
        }.onErrorResume { error ->
            when (error) {
                is UnprocessableEntityException -> unprocessableEntity().build()
                is IllegalArgumentException -> badRequest().build()
                else -> status(500).bodyValue(error.message ?: "Erro desconhecido")
            }
        }
    }

    fun fetchByExternalID(request: ServerRequest): Mono<ServerResponse> {
        val externalId = request.pathVariable("externalId")
        if (externalId.isEmpty()) return badRequest().build()
        val pessoa = pessoasR2DBCRepository.findByExternalId(request.pathVariable(externalId))
        return pessoa.flatMap {
            it?.let {
                val pessoaResponse = PessoaVM(
                    apelido = it.apelido,
                    nome = it.nome,
                    nascimento = it.nascimento,
                    stack = it.stacks?.split(",")
                )
                ok().bodyValue(pessoaResponse)
            } ?: notFound().build()
        }
    }

    fun fetchByTerm(request: ServerRequest): Mono<ServerResponse> {
        val term = request.queryParam("t")
        if (term.isEmpty) return badRequest().build()
        val pessoasResponse = pessoasR2DBCRepository.fetchByTerm(term.orElse("")).map {
            PessoaVM(
                apelido = it.apelido,
                nome = it.nome,
                nascimento = it.nascimento,
                stack = it.stacks?.split(",")
            )
        }
        return ok().body(pessoasResponse)
    }

    fun fetchCount(request: ServerRequest): Mono<ServerResponse> = ok().body(pessoasR2DBCRepository.count())
}

data class PessoaVM(val apelido: Any?, val nome: Any?, val nascimento: Any?, val stack: List<Any>?) {
    fun validate() {
        if (this.apelido == null || this.nome == null || this.nascimento == null) throw UnprocessableEntityException("")

        if (this.apelido !is String || this.nome !is String || this.nascimento !is String) throw IllegalArgumentException(
            "Apelido, Nome e Nascimento devem ser do tipo String"
        )
        if (!this.nascimento.toString()
                .matches(Regex("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])\$"))
        ) throw IllegalArgumentException("Nascimento: Formato incorreto")
    }
}

class UnprocessableEntityException(message: String) : RuntimeException(message)
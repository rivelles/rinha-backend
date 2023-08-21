package com.rivelles.rinhabackend.api.handlers

import com.rivelles.rinhabackend.api.model.PessoaVM
import com.rivelles.rinhabackend.api.model.UnprocessableEntityException
import com.rivelles.rinhabackend.repositories.postgres.PessoasR2DBCRepository
import com.rivelles.rinhabackend.repositories.postgres.entities.Pessoa
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

@Component
class PessoasHandler(val pessoasR2DBCRepository: PessoasR2DBCRepository) {
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
                if (savedRows > 0L) ServerResponse.created(URI.create("/pessoas/${pessoa.externalId}")).build()
                else ServerResponse.badRequest().build()
            }
        }.onErrorResume { error ->
            when (error) {
                is UnprocessableEntityException -> ServerResponse.unprocessableEntity().build()
                is IllegalArgumentException -> ServerResponse.badRequest().build()
                else -> ServerResponse.status(500).bodyValue(error.message ?: "Erro desconhecido")
            }
        }
    }

    fun fetchByExternalID(request: ServerRequest): Mono<ServerResponse> {
        val externalId = request.pathVariable("externalId")
        if (externalId.isEmpty()) return ServerResponse.badRequest().build()
        val pessoa = pessoasR2DBCRepository.findByExternalId(request.pathVariable(externalId))
        return pessoa.flatMap {
            it?.let {
                val pessoaResponse = PessoaVM(
                    apelido = it.apelido,
                    nome = it.nome,
                    nascimento = it.nascimento,
                    stack = it.stacks?.split(",")
                )
                ServerResponse.ok().bodyValue(pessoaResponse)
            } ?: ServerResponse.notFound().build()
        }
    }

    fun fetchByTerm(request: ServerRequest): Mono<ServerResponse> {
        val term = request.queryParam("t")
        if (term.isEmpty) return ServerResponse.badRequest().build()
        val pessoasResponse = pessoasR2DBCRepository.fetchByTerm(term.orElse("")).map {
            PessoaVM(
                apelido = it.apelido,
                nome = it.nome,
                nascimento = it.nascimento,
                stack = it.stacks?.split(",")
            )
        }
        return ServerResponse.ok().body(pessoasResponse)
    }

    fun fetchCount(request: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().body(pessoasR2DBCRepository.count())
}
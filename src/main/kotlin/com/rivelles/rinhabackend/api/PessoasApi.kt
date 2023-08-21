package com.rivelles.rinhabackend.api

import com.rivelles.rinhabackend.api.handlers.PessoasHandler
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
class PessoasApi(val handler: PessoasHandler) {
    @Bean
    fun route() = router {
        POST("/pessoas", handler::create)
        GET("/pessoas/{externalId}", handler::fetchByExternalID)
        GET("/pessoas", handler::fetchByTerm)
        GET("/contagem-pessoas", handler::fetchCount)
    }
}
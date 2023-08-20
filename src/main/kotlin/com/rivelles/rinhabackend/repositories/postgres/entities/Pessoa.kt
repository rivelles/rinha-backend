package com.rivelles.rinhabackend.repositories.postgres.entities

data class Pessoa(
    var externalId: String,
    var apelido: String,
    var nome: String,
    var nascimento: String,
    var stacks: String?
)
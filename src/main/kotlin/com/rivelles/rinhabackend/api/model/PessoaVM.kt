package com.rivelles.rinhabackend.api.model

data class PessoaVM(var apelido: String?, var nome: String?, var nascimento: String?, var stack: List<String>?) {
    fun validate() {
        if (this.apelido == null || this.nome == null || this.nascimento == null) throw UnprocessableEntityException("")

        if (!this.nascimento!!.matches(Regex("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])\$"))
        ) throw IllegalArgumentException("Nascimento: Formato incorreto")
    }
}
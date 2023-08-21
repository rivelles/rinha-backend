package com.rivelles.rinhabackend.api.model

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
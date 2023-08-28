# (Inspired by) Rinha de Backend 2023

## 📖 About
This project was inspired by [Rinha de Backend 2023](https://github.com/zanfranceschi/rinha-de-backend-2023-q3). 
I haven't submitted it since I found it 2 days before the due date, so it was created as an inspiration to simulate 
similar scenarios using Kotlin with Spring Boot in a reactive way, connecting with a Postgres database with R2DBC and 
using a local cache.

I chose K6 to run the load tests since I wanted to explore the tool better.

This project has several limitation and was built just for fun.

## 💻 Stack

- [Kotlin](https://kotlinlang.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [R2DBC](https://r2dbc.io/)
- [PostgreSQL](https://www.postgresql.org/)
- [K6](https://k6.io/)

## 🚀 Executing locally

Clone the project

```bash
  git clone git@github.com:rivelles/rinha-backend.git
```

Go inside the project's directory.

```bash
  cd rinha-backend
```

Simply run:

```bash
  ./run-load-test.sh
```

<p>
    <a href="LICENSE.md"><img src="https://img.shields.io/static/v1?label=License&message=MIT&color=22c55e&labelColor=202024" alt="License"></a>
</p>
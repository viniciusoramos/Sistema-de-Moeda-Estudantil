# Changelog — Sistema de Moeda Estudantil

Resumo das versões do projeto. O detalhamento de cada alteração está na
[Seção 15 do README](README.md#15-registro-de-alterações-por-versão). Os diagramas de cada versão
ficam em [`docs/diagramas/versoes/`](docs/diagramas/versoes/).

## v2 — Lab03S02

- Modelo ER (`docs/diagramas/modelo-er.puml`).
- Persistência com JPA/Hibernate + Spring Data JPA (padrão Repository) e banco H2.
- CRUDs de aluno e de empresa parceira (Thymeleaf + Spring MVC), com validação e hash BCrypt.
- Carga inicial de instituições e professores, com crédito semestral de 1.000 moedas.
- Diagramas de classes e de componentes atualizados; nova regra RN12.
- 15 testes automatizados (JUnit 5).

## v1 — Lab03S01

- Requisitos, regras de negócio, casos de uso (UC01–UC13) e histórias de usuário (US01–US13).
- Diagrama de casos de uso, diagrama de classes e diagrama de componentes (MVC).

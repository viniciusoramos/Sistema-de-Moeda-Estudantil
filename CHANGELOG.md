# Changelog — Sistema de Moeda Estudantil

Resumo das versões do projeto. O detalhamento de cada alteração está na
[Seção 16 do README](README.md#16-registro-de-alterações-por-versão). Os diagramas de cada versão
ficam em [`docs/diagramas/versoes/`](docs/diagramas/versoes/).

## v2.1 — Lab03S02 (novo requisito)

- Transferência de moedas entre alunos com aprovação de professor da instituição.
- Critérios: mesma instituição, 1 a 100 moedas por transferência, 300 moedas por mês, até 3 pendentes,
  motivo obrigatório, saldo reservado, rejeição com justificativa e expiração em 7 dias (RN13–RN20).
- Novos UC14–UC17, US14–US17, diagrama de estados; classes, ER e componentes atualizados.
- Limites configuráveis em `moeda.transferencia.*`; expiração por tarefa agendada.
- 31 testes automatizados (16 novos).

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

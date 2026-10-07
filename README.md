# Sistema de Moeda Estudantil — PUC Minas

**Laboratório de Desenvolvimento de Software — Laboratório 3 (2º Semestre/2026)**
Curso de Engenharia de Software — Prof. Glender Brás

Sistema para estimular o reconhecimento do mérito estudantil com uma moeda virtual. Professores
recebem moedas a cada semestre e as distribuem aos alunos como reconhecimento. Os alunos trocam
as moedas por vantagens (produtos e descontos) oferecidas por empresas parceiras.

> **Versão atual: v1 (Lab03S01).** Modelagem do sistema: requisitos, diagrama de casos de uso,
> histórias de usuário, diagrama de classes e diagrama de componentes.

---

## Sumário

- [1. Visão Geral](#1-visão-geral)
- [2. Atores](#2-atores)
- [3. Requisitos Funcionais](#3-requisitos-funcionais)
- [4. Requisitos Não Funcionais](#4-requisitos-não-funcionais)
- [5. Regras de Negócio](#5-regras-de-negócio)
- [6. Diagrama de Casos de Uso](#6-diagrama-de-casos-de-uso)
- [7. Casos de Uso](#7-casos-de-uso)
- [8. Histórias de Usuário](#8-histórias-de-usuário)
- [9. Diagrama de Classes](#9-diagrama-de-classes)
- [10. Diagrama de Componentes (Arquitetura MVC)](#10-diagrama-de-componentes-arquitetura-mvc)
- [11. Cronograma das Sprints](#11-cronograma-das-sprints)
- [12. Equipe](#12-equipe)

---

## 1. Visão Geral

- **Alunos** fazem o próprio cadastro (nome, e-mail, CPF, RG, endereço, instituição de ensino e
  curso), escolhendo uma das instituições participantes, que já estão pré-cadastradas.
- **Professores** são pré-cadastrados, porque a instituição envia a lista no momento da parceria.
  Cada professor tem nome, CPF e departamento, e faz parte de uma instituição.
- A cada semestre, cada professor recebe **1.000 moedas**, que se somam ao saldo que sobrou.
- Para **enviar moedas**, o professor precisa ter saldo suficiente, escolher o aluno e escrever o
  motivo do reconhecimento (obrigatório). O aluno é **notificado por e-mail**.
- Professores e alunos consultam o **extrato**: saldo atual e transações (envios, para o professor;
  recebimentos e trocas, para o aluno).
- **Empresas parceiras** se cadastram e cadastram **vantagens**, cada uma com descrição, foto e
  custo em moedas.
- Ao **resgatar uma vantagem**, o valor sai do saldo do aluno, e o sistema envia um e-mail de cupom
  ao aluno e outro à empresa, ambos com o mesmo **código gerado pelo sistema** para conferência na
  troca presencial.
- Alunos, professores e empresas precisam de **login e senha**, e todas as funcionalidades exigem
  autenticação.

## 2. Atores

| Ator | Descrição |
|---|---|
| **Usuário** | Generalização de Aluno, Professor e Empresa Parceira: qualquer pessoa com login e senha. |
| **Aluno** | Faz o próprio cadastro, recebe moedas, consulta o extrato e troca moedas por vantagens. |
| **Professor** | Pré-cadastrado; recebe 1.000 moedas por semestre, envia moedas e consulta o extrato. |
| **Empresa Parceira** | Faz o próprio cadastro e cadastra as vantagens que oferece. |
| **Serviço de E-mail** *(sistema)* | Entrega as notificações de moedas recebidas e os cupons de resgate. |
| **Tempo** *(sistema)* | Dispara o crédito semestral das moedas dos professores. |

## 3. Requisitos Funcionais

| Código | Requisito | Caso de uso | Sprint prevista |
|---|---|---|---|
| RF01 | O sistema deve autenticar alunos, professores e empresas parceiras por e-mail e senha. | UC01 | S03 |
| RF02 | O aluno deve poder se cadastrar informando nome, e-mail, senha, CPF, RG, endereço, instituição e curso. | UC02 | S02 |
| RF03 | O aluno deve escolher a instituição de ensino entre as pré-cadastradas. | UC02 | S02 |
| RF04 | O aluno deve poder consultar, alterar e excluir o próprio cadastro. | UC03 | S02 |
| RF05 | A empresa parceira deve poder se cadastrar informando nome, CNPJ, e-mail e senha. | UC04 | S02 |
| RF06 | A empresa parceira deve poder consultar, alterar e excluir o próprio cadastro. | UC05 | S02 |
| RF07 | A empresa parceira deve poder cadastrar e alterar vantagens (título, descrição, foto e custo). | UC06 | S03 |
| RF08 | O professor deve poder enviar moedas a um aluno, informando valor e motivo. | UC07 | S03 |
| RF09 | O aluno deve ser notificado por e-mail ao receber moedas. | UC12 | S03 |
| RF10 | Professores e alunos devem poder consultar o extrato (saldo e transações). | UC08 | S03 |
| RF11 | O aluno deve poder consultar as vantagens disponíveis. | UC09 | S03 |
| RF12 | O aluno deve poder resgatar uma vantagem, tendo o custo debitado do saldo. | UC10 | S03 |
| RF13 | No resgate, o sistema deve gerar um código e enviar e-mail de cupom ao aluno e e-mail de conferência à empresa. | UC13 | S03 |
| RF14 | O sistema deve creditar 1.000 moedas a cada professor no início de cada semestre. | UC11 | S02 |
| RF15 | Instituições e professores devem estar pré-cadastrados no sistema. | — | S02 |

## 4. Requisitos Não Funcionais

| Código | Requisito |
|---|---|
| RNF01 | O sistema deve seguir a arquitetura **MVC** (Model, View, Controller). |
| RNF02 | O sistema deve ser uma aplicação web acessível por navegador, com layout que funcione em telas de celular. |
| RNF03 | Os dados devem ser persistidos em banco de dados relacional. |
| RNF04 | As senhas devem ser armazenadas apenas como hash, nunca em texto puro. |
| RNF05 | O sistema deve validar os dados de entrada (CPF, CNPJ, e-mail, campos obrigatórios) e exibir as mensagens de erro junto ao campo. |
| RNF06 | O repositório deve manter todas as versões dos modelos UML. |

## 5. Regras de Negócio

| Código | Regra |
|---|---|
| RN01 | As instituições de ensino participantes são pré-cadastradas; o aluno apenas seleciona uma delas. |
| RN02 | Os professores são pré-cadastrados a partir da lista enviada pela instituição; todo professor pertence a uma instituição. |
| RN03 | A cada semestre, cada professor recebe 1.000 moedas, somadas ao saldo atual (o saldo é acumulável). Um mesmo semestre não é creditado duas vezes. |
| RN04 | Para enviar moedas, o professor deve ter saldo suficiente, e o motivo é obrigatório. O valor deve ser positivo. |
| RN05 | O valor enviado é debitado do professor e creditado ao aluno na mesma transação. |
| RN06 | E-mail, CPF (de aluno) e CNPJ são únicos no sistema. O e-mail é o login, único entre todos os tipos de usuário. |
| RN07 | Toda vantagem tem título, descrição, foto e custo em moedas maior que zero. |
| RN08 | O aluno só resgata uma vantagem se tiver saldo maior ou igual ao custo dela. |
| RN09 | O extrato do professor mostra os créditos semestrais e os envios; o do aluno, os recebimentos e os resgates. |
| RN10 | Cada resgate gera um código de cupom único, presente nos e-mails ao aluno e à empresa. |
| RN11 | Todas as funcionalidades, exceto o autocadastro de aluno e de empresa, exigem autenticação. |

## 6. Diagrama de Casos de Uso

![Diagrama de Casos de Uso](docs/diagramas/casos-de-uso.png)

Fonte PlantUML: [`docs/diagramas/casos-de-uso.puml`](docs/diagramas/casos-de-uso.puml)

## 7. Casos de Uso

| Código | Caso de uso | Ator(es) | Histórias |
|---|---|---|---|
| UC01 | Realizar Login | Aluno, Professor, Empresa Parceira | US01 |
| UC02 | Cadastrar-se como Aluno | Aluno | US02 |
| UC03 | Manter Cadastro de Aluno | Aluno | US03 |
| UC04 | Cadastrar-se como Empresa Parceira | Empresa Parceira | US04 |
| UC05 | Manter Cadastro de Empresa Parceira | Empresa Parceira | US05 |
| UC06 | Manter Vantagens | Empresa Parceira | US06 |
| UC07 | Enviar Moedas | Professor | US07 |
| UC08 | Consultar Extrato | Professor, Aluno | US08, US09 |
| UC09 | Consultar Vantagens | Aluno | US10 |
| UC10 | Resgatar Vantagem | Aluno | US11 |
| UC11 | Creditar Moedas Semestrais | Tempo | US12 |
| UC12 | Notificar Aluno sobre Moedas Recebidas | Serviço de E-mail | US07 |
| UC13 | Enviar Cupom ao Aluno e ao Parceiro | Serviço de E-mail | US11, US13 |

## 8. Histórias de Usuário

**US01 — Login** (UC01)
Como **usuário** (aluno, professor ou empresa parceira), quero entrar no sistema com meu e-mail e
senha, para acessar as funcionalidades do meu perfil.
- Com e-mail e senha corretos, sou levado à página inicial do meu perfil.
- Com dados incorretos, vejo a mensagem "E-mail ou senha inválidos", sem indicar qual dos dois está errado.
- Sem login, qualquer página protegida me redireciona para a tela de login.

**US02 — Cadastro de aluno** (UC02)
Como **aluno**, quero me cadastrar informando nome, e-mail, senha, CPF, RG, endereço, instituição
de ensino e curso, para participar do sistema de mérito.
- A instituição é escolhida numa lista com as instituições pré-cadastradas.
- CPF e e-mail inválidos ou já cadastrados são recusados, com a mensagem junto ao campo.
- A senha tem pelo menos 6 caracteres e é gravada como hash.
- Após o cadastro, meu saldo inicial é 0 moedas.

**US03 — Manter cadastro de aluno** (UC03)
Como **aluno**, quero consultar, alterar e excluir meu cadastro, para manter meus dados corretos.
- Posso alterar todos os dados; deixar a senha em branco mantém a senha atual.

**US04 — Cadastro de empresa parceira** (UC04)
Como **empresa parceira**, quero me cadastrar informando nome, CNPJ, e-mail e senha, para oferecer
vantagens aos alunos.
- CNPJ e e-mail inválidos ou já cadastrados são recusados.

**US05 — Manter cadastro de empresa parceira** (UC05)
Como **empresa parceira**, quero consultar, alterar e excluir meu cadastro.
- Ao excluir, minhas vantagens também são removidas.

**US06 — Manter vantagens** (UC06)
Como **empresa parceira**, quero cadastrar vantagens com título, descrição, foto e custo em moedas,
para que os alunos possam trocá-las.
- Todos os campos são obrigatórios e o custo é maior que zero (RN07).

**US07 — Enviar moedas** (UC07, UC12)
Como **professor**, quero enviar moedas a um aluno, informando o valor e o motivo, para reconhecer
o mérito dele.
- O envio é recusado se o valor for maior que meu saldo ou se o motivo estiver vazio (RN04).
- Meu saldo diminui e o do aluno aumenta no mesmo instante (RN05).
- O aluno recebe um e-mail com o valor, meu nome e o motivo.

**US08 — Extrato do professor** (UC08)
Como **professor**, quero ver meu saldo e a lista dos créditos semestrais e envios que fiz, com data,
aluno, valor e motivo.

**US09 — Extrato do aluno** (UC08)
Como **aluno**, quero ver meu saldo e a lista das moedas que recebi (com professor e motivo) e das
vantagens que resgatei (com código do cupom).

**US10 — Consultar vantagens** (UC09)
Como **aluno**, quero ver as vantagens disponíveis, com foto, descrição, empresa e custo, para
escolher o que trocar.

**US11 — Resgatar vantagem** (UC10, UC13)
Como **aluno**, quero trocar minhas moedas por uma vantagem e receber um cupom por e-mail, para
usá-lo na troca presencial.
- O resgate é recusado se meu saldo for menor que o custo (RN08).
- O custo é debitado do meu saldo, e recebo um e-mail com o código do cupom (RN10).

**US12 — Crédito semestral** (UC11)
Como **professor**, quero receber 1.000 moedas no início de cada semestre, somadas ao que sobrou do
semestre anterior (RN03).
- O crédito aparece no meu extrato e não é feito duas vezes no mesmo semestre.

**US13 — Conferência do cupom pela empresa** (UC13)
Como **empresa parceira**, quero receber um e-mail a cada resgate das minhas vantagens, com o código
do cupom e o nome do aluno, para conferir a troca presencial.

## 9. Diagrama de Classes

![Diagrama de Classes](docs/diagramas/diagrama-classes.png)

Fonte PlantUML: [`docs/diagramas/diagrama-classes.puml`](docs/diagramas/diagrama-classes.puml)

Decisões de modelagem:
- **`Usuario`** é abstrata e concentra o login (e-mail e senha); `Aluno`, `Professor` e
  `EmpresaParceira` a especializam.
- **`Transacao`** é abstrata e representa qualquer movimentação de moedas, que compõe o extrato.
  As três especializações são `EnvioMoedas` (professor → aluno), `ResgateVantagem` (aluno → vantagem)
  e `CreditoSemestral` (sistema → professor).
- `Endereco` é uma classe própria, composta em `Aluno`.

## 10. Diagrama de Componentes (Arquitetura MVC)

![Diagrama de Componentes](docs/diagramas/diagrama-componentes.png)

Fonte PlantUML: [`docs/diagramas/diagrama-componentes.puml`](docs/diagramas/diagrama-componentes.puml)

- **View:** páginas web acessadas pelo navegador.
- **Controller:** um controlador por área do sistema (autenticação, aluno, empresa, vantagem, moedas).
- **Model:** serviços com as regras de negócio, entidades de domínio e camada de persistência.
- O envio de e-mails fica isolado em `NotificacaoService`.
- As tecnologias serão definidas na Lab03S02.

## 11. Cronograma das Sprints

| Sprint | Entregas | Situação |
|---|---|---|
| Lab03S01 | Casos de uso, histórias de usuário, diagrama de classes, diagrama de componentes | Concluída |
| Lab03S02 | Modelo ER, estratégia de persistência, CRUDs de aluno e empresa (versão inicial) | A fazer |
| Lab03S03 | CRUDs finais, login, vantagens, envio de moedas, extrato, resgate, e-mails, slides de arquitetura e persistência | A fazer |

## 12. Equipe

| Nome | GitHub |
|---|---|
| Vinícius Ramos | [@viniciusoramos](https://github.com/viniciusoramos) |
| *(adicionar integrantes)* | |

# Justificativa de Escopo – TP1

**Aluno:** Gustavo Trapp
**Disciplina:** Projeto de Bloco (PB) – TP1
**Trilha escolhida:** Java – JDK 21 (LTS) + Spring Boot 4, IntelliJ IDEA Community Edition

## 1. Por que a trilha Java / Spring Boot

Escolhi a trilha Java porque o Spring Boot é o padrão de mercado para serviços web corporativos
e oferece, nas próximas etapas, tudo o que o projeto vai precisar (Spring Web para a API,
Spring Data JPA para persistência e Spring Security para autenticação) dentro do mesmo
ecossistema. Já no TP1 o projeto foi criado como uma aplicação Spring Boot (Maven + Maven Wrapper),
mas configurada sem servidor web (`spring.main.web-application-type=none`): o Spring só inicializa a
aplicação e lê a configuração. Assim, a estrutura do projeto já é a definitiva e apenas ganhará
camadas nas próximas entregas.

## 2. O que ficou DENTRO do escopo do TP1

| Item | Justificativa |
|---|---|
| Documento de requisitos completo (RF01–RF15, RNF01–RNF09) | O documento deve servir de referência para todo o bloco, por isso cobre o sistema inteiro, não só o que foi codificado agora. |
| Casos de uso UC01–UC06, com 4 detalhados (UC03 a UC06) | São os fluxos que concentram as cinco regras de negócio; UC01 e UC02 são cadastros simples e foram descritos de forma resumida. |
| Programa de console com todos os fluxos | O enunciado pede um programa simples com tipos primitivos, operadores e controle de fluxo. Implementar todas as regras de negócio com esses recursos mostra que os requisitos são viáveis e já valida a modelagem. |
| Validação das 5 regras de negócio no código | Elas são o motivo de existir do sistema (evitar duplicidade e consultas esquecidas). |
| Dados de exemplo pré-carregados (2 veterinários, 3 animais) | Facilitam a demonstração em vídeo; podem ser desligados em `application.properties` (`vidapet.dados-exemplo=false`). |

## 3. O que foi deliberadamente deixado para depois

| Item adiado | Etapa prevista | Motivo |
|---|---|---|
| Classes de domínio (Animal, Veterinario, Consulta), encapsulamento, enums | Próximo TP (OO) | O TP1 pede explicitamente um programa **sem** código orientado a objetos. Por isso usei vetores paralelos e a situação da consulta como `int` (0, 1, 2). |
| Persistência em banco de dados | TP de dados | No TP1 os dados ficam só em memória e se perdem ao fechar o programa (RNF02). |
| API REST / interface web | TP de serviços web | Não faz parte do TP1. |
| Autenticação e perfis de acesso | TP de segurança | Hoje o usuário escolhe o perfil no menu e o veterinário se identifica só pelo CRMV, sem senha. |
| Edição/exclusão de animais e veterinários, reagendamento | A combinar com o professor | Não estão nos fluxos descritos na especificação. |
| Testes automatizados (JUnit) | Próximos TPs | Com a lógica em métodos estáticos que leem do console, os testes ficam mais naturais depois da refatoração para OO. O programa foi testado manualmente com um roteiro cobrindo todos os fluxos alternativos (`docs/exemplo-execucao.txt`). |

## 4. Suposições feitas ao interpretar a especificação

| ID | Suposição | Onde aparece |
|---|---|---|
| S01 | "Das 8h às 18h" significa que a **última consulta começa às 17:30** e termina às 18:00. | RF08, `validarHorarioDeAtendimento` |
| S02 | Os horários são em **blocos fixos de 30 min** (08:00, 08:30, ..., 17:30). Assim duas consultas do mesmo veterinário só conflitam se começarem no mesmo horário, sem sobreposição parcial. | RF08, RF09 |
| S03 | O **CRMV é único**: não podem existir dois veterinários com o mesmo CRMV. Ele também serve para identificar o veterinário no menu. | RF05, UC05, UC06 |
| S04 | Consultas **canceladas liberam o horário**: a regra de conflito considera apenas consultas "agendadas". | RF09 |
| S05 | "Datas que já passaram" inclui **horários que já passaram no dia de hoje**. | RF10 |
| S06 | Feriados não foram considerados (a especificação só fala de segunda a sexta). | RN01 |
| S07 | A consulta pode ser marcada como realizada a qualquer momento, sem verificar se a hora dela já chegou (a especificação não restringe). | RF13 |
| S08 | A especificação não proíbe que o **mesmo animal** tenha duas consultas no mesmo horário com veterinários diferentes; esse caso de borda foi identificado e será levado ao professor. | Caso de borda |
| S09 | A idade do animal é em anos inteiros, aceita de 0 a 40. | RF01 |
| S10 | Apenas a recepcionista cancela consultas; o veterinário não cancela. | Atores |

## 5. Uso da IA como par de programação

A IA foi usada para revisar o código e explicar trechos, por exemplo: a conta do dia da semana
pelo algoritmo de Sakamoto (operadores `/` e `%` com inteiros), a representação de datas como
inteiro `aaaammdd` (o que permite comparar datas com `<` e `==`) e a ordenação da agenda por
seleção. As decisões de modelagem (requisitos, casos de uso e suposições acima) foram minhas.

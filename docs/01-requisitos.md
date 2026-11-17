# Documento de Requisitos – Sistema de Agendamento VidaPet

**Aluno:** Gustavo Trapp
**Disciplina:** Projeto de Bloco (PB) – TP1
**Trilha:** Java (JDK 21 + Spring Boot)

## 1. Visão geral

A VidaPet é uma clínica veterinária pequena que atende cães e gatos. A agenda hoje é mantida
em um caderno na recepção, o que gera consultas marcadas em duplicidade e consultas esquecidas.
O sistema deve organizar o cadastro dos animais, dos veterinários e a agenda de consultas,
garantindo automaticamente as regras de negócio da clínica.

## 2. Atores

| Código | Ator | Responsabilidades |
|---|---|---|
| A1 | Recepcionista | Cadastra animais e veterinários, agenda e cancela consultas. |
| A2 | Veterinário | Consulta sua agenda do dia e registra consultas como realizadas, com observações. |

## 3. Entidades principais

| Entidade | Atributos |
|---|---|
| Animal | nome, espécie (cão ou gato), idade, nome do tutor, telefone do tutor |
| Veterinário | nome, CRMV |
| Consulta | animal, veterinário, data, horário, situação (agendada, realizada, cancelada), observações |

## 4. Requisitos funcionais

Classificação de prioridade: **Essencial** (sem ele o sistema não cumpre seu objetivo),
**Importante** (agrega valor, mas o sistema funciona sem ele) e **Desejável**.

| ID | Requisito | Ator | Prioridade | Origem |
|---|---|---|---|---|
| RF01 | O sistema deve permitir cadastrar um animal com nome, espécie (cão ou gato), idade, nome do tutor e telefone do tutor. | Recepcionista | Essencial | Entidade Animal |
| RF02 | O sistema deve aceitar somente as espécies "cão" ou "gato" no cadastro de animais. | Recepcionista | Essencial | Entidade Animal |
| RF03 | O sistema deve permitir listar os animais cadastrados. | Recepcionista | Importante | Implícito |
| RF04 | O sistema deve permitir cadastrar um veterinário com nome e CRMV. | Recepcionista | Essencial | Entidade Veterinário |
| RF05 | O sistema deve impedir o cadastro de dois veterinários com o mesmo CRMV. | Recepcionista | Importante | Suposição S03 |
| RF06 | O sistema deve permitir listar os veterinários cadastrados. | Recepcionista | Importante | Implícito |
| RF07 | O sistema deve permitir agendar uma consulta informando animal, veterinário, data e horário; a consulta nasce com a situação "agendada". | Recepcionista | Essencial | Ator Recepcionista |
| RF08 | O sistema deve aceitar agendamentos apenas de segunda a sexta-feira, com início entre 08:00 e 17:30, em intervalos de 30 minutos. | Recepcionista | Essencial | Regra de negócio RN01 |
| RF09 | O sistema deve recusar o agendamento quando o veterinário já possuir consulta agendada na mesma data e horário. | Recepcionista | Essencial | RN02 |
| RF10 | O sistema deve recusar o agendamento em datas/horários que já passaram. | Recepcionista | Essencial | RN03 |
| RF11 | O sistema deve permitir cancelar uma consulta, somente se ela estiver na situação "agendada". | Recepcionista | Essencial | RN04 |
| RF12 | O sistema deve permitir que o veterinário consulte sua agenda de um dia, ordenada por horário. | Veterinário | Essencial | Ator Veterinário |
| RF13 | O sistema deve permitir que o veterinário registre uma consulta como "realizada", informando as observações do atendimento. | Veterinário | Essencial | Ator Veterinário |
| RF14 | O sistema deve permitir marcar como realizada somente consultas "agendadas" e somente pelo veterinário responsável pela consulta. | Veterinário | Essencial | RN05 |
| RF15 | O sistema deve permitir listar todas as consultas com sua situação. | Recepcionista | Desejável | Implícito |

## 5. Requisitos não funcionais

| ID | Categoria | Requisito | Prioridade |
|---|---|---|---|
| RNF01 | Restrição técnica | O sistema deve ser desenvolvido em Java 21 (LTS) com Spring Boot, mantendo-se nessa trilha até a apresentação final. | Essencial |
| RNF02 | Restrição técnica | Nesta primeira versão (TP1) o sistema é um programa standalone de console, sem banco de dados; os dados ficam apenas em memória durante a execução. | Essencial |
| RNF03 | Usabilidade | A interação deve ser feita por menu numerado, em português, com mensagens claras de sucesso e de erro (indicando qual regra foi violada). | Importante |
| RNF04 | Usabilidade | Datas devem ser digitadas no formato `dd/mm/aaaa` e horários no formato `hh:mm`, padrão usado no Brasil. | Importante |
| RNF05 | Confiabilidade | Entradas inválidas (texto onde se espera número, data inexistente como 31/02, etc.) não podem encerrar o programa; o sistema deve exibir erro e voltar ao menu. | Essencial |
| RNF06 | Desempenho | Toda operação de cadastro, agendamento ou consulta deve responder em menos de 1 segundo para até 100 animais, 100 veterinários e 500 consultas. | Importante |
| RNF07 | Portabilidade | O programa deve compilar e executar em Windows, macOS e Linux com apenas o JDK 21 instalado (Maven Wrapper incluso no projeto). | Importante |
| RNF08 | Manutenibilidade | O código-fonte deve ser versionado em repositório GitHub, com a documentação em `docs/` e o código em `src/`. | Essencial |
| RNF09 | Segurança (futuro) | Nas próximas etapas, o acesso deverá exigir autenticação, diferenciando os perfis Recepcionista e Veterinário. No TP1 o perfil é escolhido no menu. | Desejável |

## 6. Regras de negócio (referência)

| ID | Regra |
|---|---|
| RN01 | Consultas duram 30 minutos e só podem ser marcadas de segunda a sexta, das 8h às 18h. |
| RN02 | Um veterinário não pode ter duas consultas agendadas no mesmo horário. |
| RN03 | Não é possível agendar consultas em datas que já passaram. |
| RN04 | Uma consulta só pode ser cancelada se ainda estiver como agendada. |
| RN05 | Somente consultas agendadas podem ser marcadas como realizadas, e apenas pelo veterinário da consulta. |

## 7. Matriz de rastreabilidade (Requisito × Caso de uso)

| Caso de uso | Requisitos atendidos |
|---|---|
| UC01 – Cadastrar animal | RF01, RF02, RF03 |
| UC02 – Cadastrar veterinário | RF04, RF05, RF06 |
| UC03 – Agendar consulta | RF07, RF08, RF09, RF10 |
| UC04 – Cancelar consulta | RF11, RF15 |
| UC05 – Consultar agenda do dia | RF12 |
| UC06 – Registrar consulta realizada | RF13, RF14 |

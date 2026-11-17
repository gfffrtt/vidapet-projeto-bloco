# Roteiro do vídeo – TP1 VidaPet (máx. 5 min)

**Aluno:** Gustavo Trapp · **Disciplina:** Projeto de Bloco (PB) – TP1

## Antes de gravar (checklist)

- [ ] Abrir o Loom (ou OBS) com **tela + webcam**, local silencioso, boa iluminação.
- [ ] Deixar abertos: o PDF `docs/TP1_VidaPet_Gustavo_Trapp_PB.pdf`, o repositório no GitHub e o projeto no IntelliJ.
- [ ] Rodar o programa uma vez antes para "aquecer" (o primeiro start é mais lento).
- [ ] Escolher uma **data futura de dia útil** (ex.: próxima quarta-feira) e anotar a data do **próximo sábado**.
- [ ] Aumentar a fonte do terminal do IntelliJ (Settings → Editor → Font / Console Font) para ficar legível.

---

## 0:00 – 0:20 · Abertura (webcam em destaque)

> "Olá, eu sou o Gustavo Trapp, este é o TP1 do Projeto de Bloco. O sistema é o agendamento
> da clínica veterinária VidaPet, e a trilha que eu escolhi foi **Java 21 com Spring Boot**."

## 0:20 – 1:20 · Requisitos (mostrar o PDF, páginas 2 a 4)

> "Comecei pela análise, antes de abrir a IDE. Separei **15 requisitos funcionais** e
> **9 não funcionais**, todos numerados e com prioridade: essencial, importante ou desejável."

- Apontar RF07 a RF10: "o coração do sistema é o agendamento, e cada regra de negócio virou um requisito, por exemplo o RF09, que impede o veterinário de ter duas consultas no mesmo horário".
- Apontar RNF02 e RNF05: "nesta etapa os dados ficam em memória, e uma entrada inválida nunca derruba o programa".
- Mostrar a **matriz de rastreabilidade**: "cada caso de uso aponta os requisitos que atende".

## 1:20 – 2:40 · Casos de uso (PDF, diagrama e UC03)

> "Identifiquei dois atores: a **Recepcionista**, que cadastra e agenda, e o **Veterinário**,
> que consulta a agenda do dia e registra a consulta como realizada."

- No diagrama: "são 6 casos de uso. Usei `include` para as validações que se repetem: o *Agendar* inclui *Validar horário de atendimento*, e o *Cancelar* e o *Registrar realizada* incluem *Verificar situação agendada*, porque as duas regras dependem disso".
- Abrir o **UC03 – Agendar consulta**: ler rapidamente o fluxo principal e mostrar os 5 fluxos alternativos: "sem animal cadastrado, data inválida ou fim de semana, data no passado e conflito de horário".
- Citar que UC04, UC05 e UC06 seguem o mesmo formato.

## 2:40 – 4:20 · Demonstração (IntelliJ, rodar `VidaPetApplication`)

> "O projeto já é Spring Boot, mas sem servidor web por enquanto. O código é propositalmente
> procedural, sem classes de domínio, só com vetores, tipos primitivos e controle de fluxo, como o TP1 pede."

Mostrar rapidamente no código: os vetores paralelos no topo e o método `validarHorarioDeAtendimento`.

Rodar e digitar:

| Passo | Digitar | O que falar |
|---|---|---|
| 1 | `1` (Recepcionista) → `5` → animal `1` → vet `1` → **quarta futura** → `09:00` | "Agendei o Rex com a Dra. Ana." |
| 2 | `5` → animal `2` → vet `1` → mesma data → `09:00` | "Mesmo veterinário, mesmo horário: **bloqueado** (RN02)." |
| 3 | `5` → `2` → `1` → **sábado** → `10:00` | "Fim de semana: bloqueado (RN01)." |
| 4 | `5` → `2` → `1` → data de ontem → `10:00` | "Data no passado: bloqueado (RN03)." |
| 5 | `5` → `2` → `1` → quarta → `18:00` | "Fora do expediente: a última consulta começa às 17:30." |
| 6 | `5` → animal `3` → vet `2` → quarta → `10:00` | "Agendei o Thor com o Dr. Bruno." |
| 7 | `6` → consulta `2` → depois `6` → `2` de novo | "Cancelei. Na segunda vez o sistema recusa, porque só cancela agendada (RN04)." |
| 8 | `0` → `2` (Veterinário) → CRMV `CRMV-SP 67890` → `2` → consulta `1` | "O Dr. Bruno tenta concluir a consulta da Dra. Ana: **recusado** (RN05)." |
| 9 | `0` → `2` → CRMV `CRMV-SP 12345` → `1` → quarta | "A Dra. Ana vê a agenda do dia, ordenada por horário." |
| 10 | `2` → consulta `1` → "Vacina em dia, otite leve" → `1` → quarta | "Registrei como realizada, com as observações." |

## 4:20 – 4:55 · Escopo e encerramento (webcam em destaque)

> "No escopo do TP1 entraram os requisitos de todo o sistema, a modelagem e um programa que
> já valida as cinco regras. Ficaram para depois a orientação a objetos, o banco de dados,
> a API REST e a autenticação. Também registrei algumas suposições, por exemplo que a última consulta
> começa às 17:30 e que uma consulta cancelada libera o horário. Identifiquei ainda um caso
> de borda que vou levar ao professor: a especificação não impede o mesmo animal de ter duas
> consultas no mesmo horário com veterinários diferentes. Obrigado!"

---

**Dica:** se passar de 5 minutos, encurte a parte de requisitos (fale só do RF09 e da matriz) e pule os passos 4 e 5 da demonstração.

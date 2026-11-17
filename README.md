# VidaPet – Sistema de Agendamento (Projeto de Bloco – TP1)

**Aluno:** Gustavo Trapp · **Disciplina:** Projeto de Bloco (PB) · **Trilha:** Java 21 + Spring Boot

Sistema da clínica veterinária VidaPet para cadastrar animais e veterinários e organizar a agenda de consultas.
Nesta primeira entrega é um programa standalone de console, sem orientação a objetos.

## Estrutura

```
docs/
  01-requisitos.md              requisitos funcionais e não funcionais
  02-casos-de-uso.md            especificação dos casos de uso (fluxos principal e alternativos)
  03-justificativa-escopo.md    escopo, itens adiados e suposições
  diagrama-casos-de-uso.svg     diagrama UML de casos de uso (também .png e .puml)
  exemplo-execucao.txt          saída de uma execução cobrindo todos os fluxos
  roteiro-video.md              roteiro da defesa em vídeo
  TP1_VidaPet_Gustavo_Trapp_PB.pdf   documento consolidado para o Moodle
src/main/java/br/edu/vidapet/VidaPetApplication.java   programa principal
src/main/resources/application.properties              configuração
pom.xml, mvnw, mvnw.cmd                                  build Maven (com wrapper)
```

## Como executar

Pré-requisito: JDK 21.

```bash
./mvnw spring-boot:run          # macOS / Linux
mvnw.cmd spring-boot:run        # Windows
```

Ou abra a pasta no IntelliJ IDEA e execute `VidaPetApplication`.

Para começar sem os dados de exemplo, altere `vidapet.dados-exemplo=false` em `application.properties`.
Login do veterinário de exemplo: CRMV `CRMV-SP 12345` (Dra. Ana Souza) ou `CRMV-SP 67890` (Dr. Bruno Lima).

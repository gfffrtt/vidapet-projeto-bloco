package vidapet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Sistema de Agendamento da Clínica Veterinária VidaPet - TP1.
 *
 * Aluno: Gustavo Trapp - Disciplina: Projeto de Bloco (PB) - TP1
 *
 * Programa standalone de console, propositalmente SEM orientação a objetos:
 * não há classes de domínio (Animal, Veterinario, Consulta). Os dados ficam em
 * vetores paralelos de tipos primitivos/String e toda a lógica está em métodos
 * estáticos, usando apenas tipos primitivos, operadores e controle de fluxo.
 * O Spring Boot é usado só para inicializar a aplicação e ler a configuração
 * (application.properties). Nos próximos TPs esse código será refatorado para OO.
 */
@SpringBootApplication
public class VidaPetApplication {

    // ---------------------------------------------------------------- constantes
    static final int MAX_ANIMAIS = 100;
    static final int MAX_VETERINARIOS = 100;
    static final int MAX_CONSULTAS = 500;

    static final int DURACAO_CONSULTA_MIN = 30;           // RN01
    static final int INICIO_EXPEDIENTE_MIN = 8 * 60;      // 08:00
    static final int FIM_EXPEDIENTE_MIN = 18 * 60;        // 18:00 (última consulta começa 17:30)

    static final int AGENDADA = 0;
    static final int REALIZADA = 1;
    static final int CANCELADA = 2;
    static final String[] NOMES_SITUACAO = {"AGENDADA", "REALIZADA", "CANCELADA"};

    // ---------------------------------------------------------------- animais
    static String[] animalNome = new String[MAX_ANIMAIS];
    static String[] animalEspecie = new String[MAX_ANIMAIS];
    static int[] animalIdade = new int[MAX_ANIMAIS];
    static String[] animalTutor = new String[MAX_ANIMAIS];
    static String[] animalTelefone = new String[MAX_ANIMAIS];
    static int totalAnimais = 0;

    // ---------------------------------------------------------------- veterinários
    static String[] vetNome = new String[MAX_VETERINARIOS];
    static String[] vetCrmv = new String[MAX_VETERINARIOS];
    static int totalVeterinarios = 0;

    // ---------------------------------------------------------------- consultas
    static int[] consultaAnimal = new int[MAX_CONSULTAS];      // índice no vetor de animais
    static int[] consultaVet = new int[MAX_CONSULTAS];         // índice no vetor de veterinários
    static int[] consultaData = new int[MAX_CONSULTAS];        // formato aaaammdd (ex.: 20261118)
    static int[] consultaHorario = new int[MAX_CONSULTAS];     // minutos desde 00:00 (ex.: 570 = 09:30)
    static int[] consultaSituacao = new int[MAX_CONSULTAS];    // AGENDADA, REALIZADA ou CANCELADA
    static String[] consultaObs = new String[MAX_CONSULTAS];
    static int totalConsultas = 0;

    static Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        ConfigurableApplicationContext contexto = SpringApplication.run(VidaPetApplication.class, args);
        boolean dadosExemplo = contexto.getEnvironment()
                .getProperty("vidapet.dados-exemplo", Boolean.class, Boolean.FALSE);

        if (dadosExemplo) {
            carregarDadosExemplo();
        }
        executarMenuPrincipal();
        contexto.close();
    }

    // ======================================================================= MENUS

    static void executarMenuPrincipal() {
        System.out.println("==============================================");
        System.out.println("   VidaPet - Sistema de Agendamento (TP1)");
        System.out.println("==============================================");
        int opcao;
        do {
            System.out.println();
            System.out.println("Quem está usando o sistema?");
            System.out.println("  1 - Recepcionista");
            System.out.println("  2 - Veterinário");
            System.out.println("  0 - Sair");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> menuRecepcionista();
                case 2 -> menuVeterinario();
                case 0 -> System.out.println("Até logo!");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    static void menuRecepcionista() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- RECEPCIONISTA ---");
            System.out.println("  1 - Cadastrar animal");
            System.out.println("  2 - Listar animais");
            System.out.println("  3 - Cadastrar veterinário");
            System.out.println("  4 - Listar veterinários");
            System.out.println("  5 - Agendar consulta");
            System.out.println("  6 - Cancelar consulta");
            System.out.println("  7 - Listar todas as consultas");
            System.out.println("  0 - Voltar");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> cadastrarAnimal();
                case 2 -> listarAnimais();
                case 3 -> cadastrarVeterinario();
                case 4 -> listarVeterinarios();
                case 5 -> agendarConsulta();
                case 6 -> cancelarConsulta();
                case 7 -> listarConsultas();
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    static void menuVeterinario() {
        String crmv = lerTexto("Informe seu CRMV: ");
        int vet = buscarVeterinarioPorCrmv(crmv);
        if (vet == -1) {
            System.out.println("Veterinário não encontrado.");
            return;
        }
        System.out.println("Olá, " + vetNome[vet] + "!");
        int opcao;
        do {
            System.out.println();
            System.out.println("--- VETERINÁRIO: " + vetNome[vet] + " ---");
            System.out.println("  1 - Minha agenda do dia");
            System.out.println("  2 - Registrar consulta realizada");
            System.out.println("  0 - Voltar");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> consultarAgendaDoDia(vet);
                case 2 -> registrarConsultaRealizada(vet);
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    // ======================================================================= UC01 / UC02

    static void cadastrarAnimal() {
        if (totalAnimais >= MAX_ANIMAIS) {
            System.out.println("Limite de animais atingido.");
            return;
        }
        String nome = lerTexto("Nome do animal: ");
        String especie = normalizarEspecie(lerTexto("Espécie (cão/gato): "));
        int idade = lerInteiro("Idade (anos): ");
        String tutor = lerTexto("Nome do tutor: ");
        String telefone = lerTexto("Telefone do tutor: ");

        if (nome.isEmpty() || tutor.isEmpty() || telefone.isEmpty()) {
            System.out.println("Erro: nome, tutor e telefone são obrigatórios.");
            return;
        }
        if (especie == null) {
            System.out.println("Erro: a clínica só atende cães e gatos.");
            return;
        }
        if (idade < 0 || idade > 40) {
            System.out.println("Erro: idade inválida.");
            return;
        }
        animalNome[totalAnimais] = nome;
        animalEspecie[totalAnimais] = especie;
        animalIdade[totalAnimais] = idade;
        animalTutor[totalAnimais] = tutor;
        animalTelefone[totalAnimais] = telefone;
        totalAnimais++;
        System.out.println("Animal cadastrado com o número " + totalAnimais + ".");
    }

    static void listarAnimais() {
        if (totalAnimais == 0) {
            System.out.println("Nenhum animal cadastrado.");
            return;
        }
        System.out.println("Nº  | Nome         | Espécie | Idade | Tutor              | Telefone");
        for (int i = 0; i < totalAnimais; i++) {
            System.out.printf("%-3d | %-12s | %-7s | %5d | %-18s | %s%n",
                    i + 1, animalNome[i], animalEspecie[i], animalIdade[i], animalTutor[i], animalTelefone[i]);
        }
    }

    static void cadastrarVeterinario() {
        if (totalVeterinarios >= MAX_VETERINARIOS) {
            System.out.println("Limite de veterinários atingido.");
            return;
        }
        String nome = lerTexto("Nome do veterinário: ");
        String crmv = lerTexto("CRMV: ").toUpperCase();
        if (nome.isEmpty() || crmv.isEmpty()) {
            System.out.println("Erro: nome e CRMV são obrigatórios.");
            return;
        }
        if (buscarVeterinarioPorCrmv(crmv) != -1) {
            System.out.println("Erro: já existe veterinário com o CRMV " + crmv + ".");
            return;
        }
        vetNome[totalVeterinarios] = nome;
        vetCrmv[totalVeterinarios] = crmv;
        totalVeterinarios++;
        System.out.println("Veterinário cadastrado com o número " + totalVeterinarios + ".");
    }

    static void listarVeterinarios() {
        if (totalVeterinarios == 0) {
            System.out.println("Nenhum veterinário cadastrado.");
            return;
        }
        System.out.println("Nº  | Nome                 | CRMV");
        for (int i = 0; i < totalVeterinarios; i++) {
            System.out.printf("%-3d | %-20s | %s%n", i + 1, vetNome[i], vetCrmv[i]);
        }
    }

    // ======================================================================= UC03 Agendar consulta

    static void agendarConsulta() {
        // FA1: pré-condição
        if (totalAnimais == 0 || totalVeterinarios == 0) {
            System.out.println("Cadastre ao menos um animal e um veterinário antes de agendar.");
            return;
        }
        if (totalConsultas >= MAX_CONSULTAS) {
            System.out.println("Limite de consultas atingido.");
            return;
        }
        listarAnimais();
        int animal = lerInteiro("Número do animal: ") - 1;
        if (animal < 0 || animal >= totalAnimais) {           // FA2
            System.out.println("Opção inválida.");
            return;
        }
        listarVeterinarios();
        int vet = lerInteiro("Número do veterinário: ") - 1;
        if (vet < 0 || vet >= totalVeterinarios) {           // FA2
            System.out.println("Opção inválida.");
            return;
        }
        int data = converterData(lerTexto("Data (dd/mm/aaaa): "));
        int horario = converterHorario(lerTexto("Horário (hh:mm): "));

        // FA3 / FA4: validar horário de atendimento (include)
        String erro = validarHorarioDeAtendimento(data, horario);
        if (erro != null) {
            System.out.println("Erro: " + erro);
            return;
        }
        // FA5: conflito de horário do veterinário (RN02)
        if (veterinarioOcupado(vet, data, horario)) {
            System.out.println("Erro: o veterinário já possui consulta agendada neste horário.");
            return;
        }
        consultaAnimal[totalConsultas] = animal;
        consultaVet[totalConsultas] = vet;
        consultaData[totalConsultas] = data;
        consultaHorario[totalConsultas] = horario;
        consultaSituacao[totalConsultas] = AGENDADA;
        consultaObs[totalConsultas] = "";
        totalConsultas++;
        System.out.println("Consulta nº " + totalConsultas + " agendada: " + animalNome[animal]
                + " com " + vetNome[vet] + " em " + formatarData(data) + " às "
                + formatarHorario(horario) + " até " + formatarHorario(horario + DURACAO_CONSULTA_MIN) + ".");
    }

    /**
     * Caso de uso incluído "Validar horário de atendimento" (RN01 e RN03).
     * Retorna null se estiver tudo certo ou a mensagem de erro.
     */
    static String validarHorarioDeAtendimento(int data, int horario) {
        if (data == -1) {
            return "data inexistente ou fora do formato dd/mm/aaaa.";
        }
        if (horario == -1) {
            return "horário fora do formato hh:mm.";
        }
        int ano = data / 10000;
        int mes = (data / 100) % 100;
        int dia = data % 100;
        int diaSemana = diaDaSemana(ano, mes, dia);          // 0 = domingo ... 6 = sábado
        if (diaSemana == 0 || diaSemana == 6) {
            return "consultas só podem ser marcadas de segunda a sexta.";
        }
        boolean dentroDoExpediente = horario >= INICIO_EXPEDIENTE_MIN
                && horario + DURACAO_CONSULTA_MIN <= FIM_EXPEDIENTE_MIN;
        if (!dentroDoExpediente || horario % DURACAO_CONSULTA_MIN != 0) {
            return "o horário deve ser entre 08:00 e 17:30, de 30 em 30 minutos.";
        }
        int hoje = dataDeHoje();
        int agora = horarioAgora();
        if (data < hoje || (data == hoje && horario <= agora)) {
            return "não é possível agendar em data/horário que já passou.";
        }
        return null;
    }

    static boolean veterinarioOcupado(int vet, int data, int horario) {
        for (int i = 0; i < totalConsultas; i++) {
            if (consultaVet[i] == vet && consultaData[i] == data
                    && consultaHorario[i] == horario && consultaSituacao[i] == AGENDADA) {
                return true;
            }
        }
        return false;
    }

    // ======================================================================= UC04 Cancelar consulta

    static void cancelarConsulta() {
        if (totalConsultas == 0) {                           // FA1
            System.out.println("Nenhuma consulta cadastrada.");
            return;
        }
        listarConsultas();
        int c = lerInteiro("Número da consulta a cancelar: ") - 1;
        if (c < 0 || c >= totalConsultas) {                  // FA2
            System.out.println("Consulta não encontrada.");
            return;
        }
        if (consultaSituacao[c] != AGENDADA) {               // FA3 (RN04)
            System.out.println("Erro: só é possível cancelar consultas com situação AGENDADA (situação atual: "
                    + NOMES_SITUACAO[consultaSituacao[c]] + ").");
            return;
        }
        consultaSituacao[c] = CANCELADA;
        System.out.println("Consulta nº " + (c + 1) + " cancelada.");
    }

    static void listarConsultas() {
        if (totalConsultas == 0) {
            System.out.println("Nenhuma consulta cadastrada.");
            return;
        }
        System.out.println("Nº  | Data       | Hora  | Animal       | Veterinário          | Situação");
        for (int i = 0; i < totalConsultas; i++) {
            imprimirConsulta(i);
        }
    }

    static void imprimirConsulta(int i) {
        System.out.printf("%-3d | %s | %s | %-12s | %-20s | %s%n",
                i + 1, formatarData(consultaData[i]), formatarHorario(consultaHorario[i]),
                animalNome[consultaAnimal[i]], vetNome[consultaVet[i]], NOMES_SITUACAO[consultaSituacao[i]]);
    }

    // ======================================================================= UC05 Agenda do dia

    static void consultarAgendaDoDia(int vet) {
        String texto = lerTexto("Data (dd/mm/aaaa) ou Enter para hoje: ");
        int data = texto.isEmpty() ? dataDeHoje() : converterData(texto);
        if (data == -1) {                                    // FA3
            System.out.println("Data inválida.");
            return;
        }
        // separa os índices das consultas do veterinário naquela data
        int[] indices = new int[totalConsultas];
        int quantidade = 0;
        for (int i = 0; i < totalConsultas; i++) {
            if (consultaVet[i] == vet && consultaData[i] == data) {
                indices[quantidade] = i;
                quantidade++;
            }
        }
        if (quantidade == 0) {                               // FA2
            System.out.println("Nenhuma consulta para " + formatarData(data) + ".");
            return;
        }
        // ordenação por horário (selection sort)
        for (int i = 0; i < quantidade - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < quantidade; j++) {
                if (consultaHorario[indices[j]] < consultaHorario[indices[menor]]) {
                    menor = j;
                }
            }
            int temp = indices[i];
            indices[i] = indices[menor];
            indices[menor] = temp;
        }
        System.out.println("Agenda de " + vetNome[vet] + " em " + formatarData(data) + ":");
        for (int k = 0; k < quantidade; k++) {
            int c = indices[k];
            int a = consultaAnimal[c];
            System.out.printf("  [%d] %s-%s | %s (%s, %d ano(s)) | tutor: %s %s | %s%n",
                    c + 1, formatarHorario(consultaHorario[c]),
                    formatarHorario(consultaHorario[c] + DURACAO_CONSULTA_MIN),
                    animalNome[a], animalEspecie[a], animalIdade[a], animalTutor[a], animalTelefone[a],
                    NOMES_SITUACAO[consultaSituacao[c]]);
            if (consultaSituacao[c] == REALIZADA) {
                System.out.println("      Observações: " + consultaObs[c]);
            }
        }
    }

    // ======================================================================= UC06 Registrar realizada

    static void registrarConsultaRealizada(int vet) {
        int pendentes = 0;
        for (int i = 0; i < totalConsultas; i++) {
            if (consultaVet[i] == vet && consultaSituacao[i] == AGENDADA) {
                if (pendentes == 0) {
                    System.out.println("Suas consultas agendadas:");
                }
                imprimirConsulta(i);
                pendentes++;
            }
        }
        if (pendentes == 0) {
            System.out.println("Você não tem consultas agendadas.");
            return;
        }
        int c = lerInteiro("Número da consulta realizada: ") - 1;
        if (c < 0 || c >= totalConsultas) {
            System.out.println("Consulta não encontrada.");
            return;
        }
        if (consultaVet[c] != vet) {                         // FA2 (RN05)
            System.out.println("Erro: apenas o veterinário da consulta pode registrá-la como realizada.");
            return;
        }
        if (consultaSituacao[c] != AGENDADA) {               // FA3 (RN05)
            System.out.println("Erro: só consultas AGENDADAS podem ser marcadas como realizadas (situação atual: "
                    + NOMES_SITUACAO[consultaSituacao[c]] + ").");
            return;
        }
        String obs = lerTexto("Observações do atendimento: ");
        consultaObs[c] = obs.isEmpty() ? "Sem observações" : obs;   // FA4
        consultaSituacao[c] = REALIZADA;
        System.out.println("Consulta nº " + (c + 1) + " registrada como REALIZADA.");
    }

    // ======================================================================= utilitários

    static int buscarVeterinarioPorCrmv(String crmv) {
        for (int i = 0; i < totalVeterinarios; i++) {
            if (vetCrmv[i].equalsIgnoreCase(crmv.trim())) {
                return i;
            }
        }
        return -1;
    }

    /** Aceita "cão", "cao", "cachorro" ou "gato"; retorna null para qualquer outra espécie. */
    static String normalizarEspecie(String texto) {
        return switch (texto.toLowerCase()) {
            case "cão", "cao", "cachorro", "c" -> "cão";
            case "gato", "g" -> "gato";
            default -> null;
        };
    }

    /** Converte "dd/mm/aaaa" em um int aaaammdd. Retorna -1 se a data não existir. */
    static int converterData(String texto) {
        String[] partes = texto.trim().split("/");
        if (partes.length != 3) {
            return -1;
        }
        int dia, mes, ano;
        try {
            dia = Integer.parseInt(partes[0]);
            mes = Integer.parseInt(partes[1]);
            ano = Integer.parseInt(partes[2]);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (ano < 1900 || ano > 2999 || mes < 1 || mes > 12 || dia < 1 || dia > diasNoMes(mes, ano)) {
            return -1;
        }
        return ano * 10000 + mes * 100 + dia;
    }

    /** Converte "hh:mm" em minutos desde a meia-noite. Retorna -1 se inválido. */
    static int converterHorario(String texto) {
        String[] partes = texto.trim().split(":");
        if (partes.length != 2) {
            return -1;
        }
        int hora, minuto;
        try {
            hora = Integer.parseInt(partes[0]);
            minuto = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
            return -1;
        }
        return hora * 60 + minuto;
    }

    static boolean anoBissexto(int ano) {
        return (ano % 4 == 0 && ano % 100 != 0) || ano % 400 == 0;
    }

    static int diasNoMes(int mes, int ano) {
        return switch (mes) {
            case 2 -> anoBissexto(ano) ? 29 : 28;
            case 4, 6, 9, 11 -> 30;
            default -> 31;
        };
    }

    /** Algoritmo de Sakamoto: 0 = domingo, 1 = segunda, ..., 6 = sábado. */
    static int diaDaSemana(int ano, int mes, int dia) {
        int[] t = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
        if (mes < 3) {
            ano -= 1;
        }
        return (ano + ano / 4 - ano / 100 + ano / 400 + t[mes - 1] + dia) % 7;
    }

    static int dataDeHoje() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.getYear() * 10000 + agora.getMonthValue() * 100 + agora.getDayOfMonth();
    }

    static int horarioAgora() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.getHour() * 60 + agora.getMinute();
    }

    static String formatarData(int data) {
        return String.format("%02d/%02d/%04d", data % 100, (data / 100) % 100, data / 10000);
    }

    static String formatarHorario(int minutos) {
        return String.format("%02d:%02d", minutos / 60, minutos % 60);
    }

    static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        if (!entrada.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada encerrada. Até logo!");
            System.exit(0);
        }
        return entrada.nextLine().trim();
    }

    /** Lê um inteiro; devolve -1 se o usuário digitar algo que não é número. */
    static int lerInteiro(String mensagem) {
        String texto = lerTexto(mensagem);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static void carregarDadosExemplo() {
        vetNome[0] = "Dra. Ana Souza";     vetCrmv[0] = "CRMV-SP 12345";
        vetNome[1] = "Dr. Bruno Lima";     vetCrmv[1] = "CRMV-SP 67890";
        totalVeterinarios = 2;

        animalNome[0] = "Rex";   animalEspecie[0] = "cão";  animalIdade[0] = 5;
        animalTutor[0] = "Carla Mendes";  animalTelefone[0] = "(11) 98888-1111";
        animalNome[1] = "Mimi";  animalEspecie[1] = "gato"; animalIdade[1] = 3;
        animalTutor[1] = "Diego Alves";   animalTelefone[1] = "(11) 97777-2222";
        animalNome[2] = "Thor";  animalEspecie[2] = "cão";  animalIdade[2] = 9;
        animalTutor[2] = "Elisa Rocha";   animalTelefone[2] = "(11) 96666-3333";
        totalAnimais = 3;

        System.out.println("(Dados de exemplo carregados: 2 veterinários e 3 animais.)");
    }
}

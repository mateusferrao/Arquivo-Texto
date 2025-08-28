import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Comercio {
    /** Para inclusão de novos produtos no vetor */
    static final int MAX_NOVOS_PRODUTOS = 10;

    /**
     * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
     */
    static String nomeArquivoDados;

    /** Scanner para leitura do teclado */
    static Scanner teclado;

    /**
     * Vetor de produtos cadastrados. Sempre terá espaço para 10 novos produtos a
     * cada execução
     */
    static Produto[] produtosCadastrados;

    /** Quantidade produtos cadastrados atualmente no vetor */
    static int quantosProdutos;

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    /**
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma
     * classe Menu.
     * 
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e listar um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo texto e retorna um vetor de produtos. Arquivo no
     * formato
     * N (quantiade de produtos) <br/>
     * tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em
     * caso de problemas com o arquivo.
     * 
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de
     *         leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        Produto[] vetorProdutos;
        Scanner arqDados = null;
        try {
            arqDados = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
            String primeiraLinha = arqDados.nextLine().trim();
            String numeroProdutos = primeiraLinha.split(";")[0]; // Pega apenas a primeira parte antes do ;
            quantosProdutos = Integer.parseInt(numeroProdutos);
            vetorProdutos = new Produto[quantosProdutos + MAX_NOVOS_PRODUTOS];
            for (int i = 0; i < quantosProdutos; i++) {
                String linha = arqDados.nextLine();
                vetorProdutos[i] = Produto.criarDoTexto(linha);
            }
        } catch (IOException fne) {
            vetorProdutos = null;
        } finally {
            if (arqDados != null) {
                arqDados.close();
            }
        }
        return vetorProdutos;
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
        cabecalho();
        if(produtosCadastrados == null) {
            System.out.println("Nenhum produto cadastrado");
            return;
        }
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if (produtosCadastrados[i] != null)
                System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }

    /**
     * Localiza um produto no vetor de cadastrados, a partir do nome, e imprime seus
     * dados.
     * A busca não é sensível ao caso. Em caso de não encontrar o produto, imprime
     * mensagem padrão
     */
    static void localizarProdutos() {
        cabecalho();
        System.out.println("Digite o nome do produto a ser localizado: ");
        String nome = teclado.nextLine();
        boolean encontrado = false;
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if (produtosCadastrados[i] != null && produtosCadastrados[i].toString().contains(nome)) {
                System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
                encontrado = true;
            }
        }
        if (!encontrado) {
            System.out.println("Produto não encontrado");
        }
    }

    /**
     * Rotina de cadastro de um novo produto: pergunta ao usuário o tipo do produto,
     * lê os dados correspondentes,
     * cria o objeto adequado de acordo com o tipo, inclui no vetor. Este método
     * pode ser feito com um nível muito
     * melhor de modularização. As diversas fases da lógica poderiam ser
     * encapsuladas em outros métodos.
     * Uma sugestão de melhoria mais significativa poderia ser o uso de padrão
     * Factory Method para criação dos objetos.
     */
    static void cadastrarProduto() {
        cabecalho();
        produtosCadastrados[quantosProdutos] = retornaProdutoASerCadastrado();
        quantosProdutos++;
        System.out.println("Produto cadastrado com sucesso");
    }

    static Produto retornaProdutoASerCadastrado() {
        System.out.println("Digite o tipo do produto (1 - Não Perecível, 2 - Perecível): ");
        int tipo = Integer.parseInt(teclado.nextLine());
        System.out.println("Digite a descrição do produto: ");
        String descricao = teclado.nextLine();
        System.out.println("Digite o preço de custo do produto: ");
        double precoCusto = Double.parseDouble(teclado.nextLine());
        System.out.println("Digite a margem de lucro do produto: ");
        double margemLucro = Double.parseDouble(teclado.nextLine());
        if (tipo == 1) {
            return new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
        } else {
            System.out.println("Digite a data de validade do produto: ");
            LocalDate dataValidade = LocalDate.parse(teclado.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new ProdutoPerecivel(descricao, precoCusto, margemLucro, dataValidade);
        }
    }

    /**
     * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve
     * todo o conteúdo do arquivo.
     * 
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    public static void salvarProdutos(String nomeArquivo) {
        try {
            FileWriter arquivoSaida = new FileWriter(nomeArquivo, Charset.forName("UTF-8"));
            arquivoSaida.append(quantosProdutos + "\n");
            for (int i = 0; i < produtosCadastrados.length; i++) {
                if (produtosCadastrados[i] != null) {
                    arquivoSaida.append(produtosCadastrados[i].gerarDadosTexto() + "\n");
                }
            }
            arquivoSaida.close();
            System.out.println("Arquivo " + nomeArquivo + " salvo.");
        } catch (IOException e) {
            System.out.println("Problemas no arquivo " + nomeArquivo + ". Tente novamente.");
        }
    }

    public static void main(String[] args) throws Exception {
        teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));
        nomeArquivoDados = "Templates p alunos/dadosProdutos.csv";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        int opcao = -1;
        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
            }
            pausa();
        } while (opcao != 0);

        salvarProdutos(nomeArquivoDados);
        teclado.close();
    }

}

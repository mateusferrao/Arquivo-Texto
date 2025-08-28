import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Pedido {

	/** Quantidade máxima de produtos de um pedido */
	private static final int MAX_PRODUTOS = 10;
	
	/** Porcentagem de desconto para pagamentos à vista */
	private static final double DESCONTO_PG_A_VISTA = 0.15;
	
	/** Vetor para armazenar os produtos do pedido */
	private Produto[] produtos;
	
	/** Data de criação do pedido */
	private LocalDate dataPedido;
	
	/** Indica a quantidade total de produtos no pedido até o momento */
	private int quantProdutos = 0;
	
	/** Indica a forma de pagamento do pedido sendo: 1, pagamento à vista; 2, pagamento parcelado */
	private int formaDePagamento;
	
	/** Construtor do pedido.
	 *  Deve criar o vetor de produtos do pedido, 
	 *  armazenar a data e a forma de pagamento informadas para o pedido. 
	 */  
	public Pedido(LocalDate dataPedido, int formaDePagamento) {
		this.produtos = new Produto[MAX_PRODUTOS];
		this.dataPedido = dataPedido;
		this.formaDePagamento = formaDePagamento;
	}
	
	/**
     * Inclui um produto neste pedido e aumenta a quantidade de produtos armazenados no pedido até o momento.
     * @param novo O produto a ser incluído no pedido
     * @return true/false indicando se a inclusão do produto no pedido foi realizada com sucesso.
     */
	public boolean incluirProduto(Produto novo) {
		if (quantProdutos < MAX_PRODUTOS) {
			produtos[quantProdutos] = novo;
			quantProdutos++;
			return true;
		}
		return false;
	}
	
	/**
     * Calcula e retorna o valor final do pedido (soma do valor de venda de todos os produtos do pedido).
     * Caso a forma de pagamento do pedido seja à vista, aplica o desconto correspondente.
     * @return Valor final do pedido (double)
     */
	public double valorFinal() {
		double valorFinal = 0.0;
		for (int i = 0; i < quantProdutos; i++) {
			valorFinal += produtos[i].valorDeVenda();
		}
		if (formaDePagamento == 1) {
			valorFinal *= (1 - DESCONTO_PG_A_VISTA);
		}
		return valorFinal;
	}
	
	/**
     * Representação, em String, do pedido.
     * Contém um cabeçalho com sua data e o número de produtos no pedido.
     * Depois, em cada linha, a descrição de cada produto do pedido.
     * Ao final, mostra a forma de pagamento, o percentual de desconto (se for o caso) e o valor a ser pago pelo pedido.
     * Exemplo:
     * Data do pedido: 25/08/2025
     * Pedido com 2 produtos.
     * Produtos no pedido:
     * NOME: Iogurte: R$ 8.00
     * Válido até: 29/08/2025
     * NOME: Guardanapos: R$ 2.75
     * Pedido pago à vista. Percentual de desconto: 15,00%
     * Valor total do pedido: R$ 10.75 
     * @return Uma string contendo dados do pedido conforme especificado (cabeçalho, detalhes, forma de pagamento,
     * percentual de desconto - se for o caso - e valor a pagar)
     */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Data do pedido: " + dataPedido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
		sb.append("Pedido com " + quantProdutos + " produtos.\n");
		sb.append("Produtos no pedido:\n");
		for (int i = 0; i < quantProdutos; i++) {
			sb.append(produtos[i].toString() + "\n");
		}
		return sb.toString();
	}
	
	/**
     * Igualdade de pedidos: caso possuam a mesma data. 
     * @param obj Outro pedido a ser comparado 
     * @return booleano true/false conforme o parâmetro possua a data igual ou não a este pedido.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Pedido outro = (Pedido) obj;
        return dataPedido.equals(outro.dataPedido) && formaDePagamento == outro.formaDePagamento && quantProdutos == outro.quantProdutos && Arrays.equals(produtos, outro.produtos);
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

	static Pedido criarDoTexto(String linha){
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String[] atributos = linha.split(";");
        LocalDate dataPedido = LocalDate.parse(atributos[0], formatoData);
        int formaDePagamento = Integer.parseInt(atributos[1]);
        Pedido novoPedido = new Pedido(dataPedido, formaDePagamento);
		int quantProdutos = atributos.length - 2;
		for (int i = 0; i < quantProdutos; i++) {
			Produto produto = App.localizarProdutoPorNome(atributos[i + 2]);
			if (produto != null) {
				novoPedido.incluirProduto(produto);
			}
		}
		
        return novoPedido;
    }
}
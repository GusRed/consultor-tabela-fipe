package br.com.alura.consultor_tabela_fipe.principal;

import br.com.alura.consultor_tabela_fipe.model.Dados;
import br.com.alura.consultor_tabela_fipe.model.Modelos;
import br.com.alura.consultor_tabela_fipe.model.Veiculo;
import br.com.alura.consultor_tabela_fipe.service.ConsumoAPI;
import br.com.alura.consultor_tabela_fipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {

        var menuInicial = """
                *** VEÍCULOS ***
                - Carro
                - Moto
                - Caminhão
                ****************  \s
                Digite uma dessas opções de veículos para consultar na tabela Fipe:""";

        String veiculoEscolhido;

        do {
            System.out.println(menuInicial);
            veiculoEscolhido = leitura.nextLine().toLowerCase().replace("ã", "a");
        } while (!veiculoEscolhido.equals("carro") && !veiculoEscolhido.equals("moto") && !veiculoEscolhido.equals("caminhao"));

        String endereco = "";
        switch (veiculoEscolhido) {
            case "carro":
                endereco = URL_BASE + "carros/marcas";
                break;
            case "moto":
                endereco = URL_BASE + "motos/marcas";
                break;
            case "caminhao":
                endereco = URL_BASE + "caminhoes/marcas";
                break;
        }

        var json = consumoAPI.obterDados(endereco);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::nome))
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca que você queira consultar os modelos: ");
        var codigoMarca = leitura.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = consumoAPI.obterDados(endereco);
        var listaModelos = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        listaModelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::nome))
                .forEach(System.out::println);

        System.out.println("\nDigite o nome do modelo que você deseja que seja filtrado: ");
        var nomeModelo = leitura.nextLine();

        List<Dados> modelosFiltrados = listaModelos.modelos().stream()
                        .filter(m -> m.nome().toLowerCase().contains(nomeModelo.toLowerCase()))
                                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nInforme o código do modelo que você queira consultar os anos: ");
        var codigoModelo = leitura.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        json = consumoAPI.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();
        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoAPI.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);

    }

}

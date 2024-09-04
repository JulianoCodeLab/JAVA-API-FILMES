/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juliano.Etapa1.principal;

import com.juliano.Etapa1.model.DadosSerie;
import com.juliano.Etapa1.model.DadosTemporada;
import com.juliano.Etapa1.model.Episodio;
import com.juliano.Etapa1.service.ConsumoApi;
import com.juliano.Etapa1.service.ConverteDados;
import java.util.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {
    private static final String ENDERECO = "https://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=6585022c";
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        String nomeSerie = solicitarNomeSerie();
        DadosSerie dados = obterDadosSerie(nomeSerie);
  
        List<DadosTemporada> temporadas = obterTemporadas(nomeSerie, dados.totalTemporadas());
        exibirTemporadas(temporadas);
        exibirEpisodios(temporadas);
        exibirEstatisticas(temporadas);
    }
   

    private String solicitarNomeSerie() {
        System.out.println("Digite o nome da série para busca");
        return leitura.nextLine();
    }

    private DadosSerie obterDadosSerie(String nomeSerie) {
        String json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private List<DadosTemporada> obterTemporadas(String nomeSerie, int totalTemporadas) {
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= totalTemporadas; i++) {
            String json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        return temporadas;
    }

    private void exibirTemporadas(List<DadosTemporada> temporadas) {
        temporadas.forEach(System.out::println);
    }

    private void exibirEpisodios(List<DadosTemporada> temporadas) {
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);
    }

    private void exibirEstatisticas(List<DadosTemporada> temporadas) {
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
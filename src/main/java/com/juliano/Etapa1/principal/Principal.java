/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juliano.Etapa1.principal;

/**
 *
 * @author juliano
 */
public class Principal {
    
          private Scanner leitura = new Scanner(System.in);
          private ConsumoApi consumo = new ConsumoApi();
          private ConverteDados conversor = new ConverteDados();

          private final String ENDERECO = "https://www.omdbapi.com/?t=";
          private final String API_KEY = "&apikey=6585022c";

          public void exibeMenu(){
                   System.out.println("Digite o nome da serie para busca");
                   var nomeSerie = leitura.nextLine();
                   var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
                   DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
                   System.out.println(dados);

         List<DadosTemporada> temporadas = new ArrayList<>();

         for (int i = 1; i<=dados.totalTemporadas(); i++){
                   json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season=" + i + API_KEY);
                   DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                   temporadas.add(dadosTemporada);
          }
                   temporadas.forEach(System.out::println);

//        for(int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j< episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

          temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

          List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                   .flatMap(t -> t.episodios().stream())
                   .collect(Collectors.toList());

          List<Episodio> episodios = temporadas.stream()
                   .flatMap(t -> t.episodios().stream()
                   .map(d -> new Episodio(t.numero(), d)))
                   .collect(Collectors.toList());

          episodios.forEach(System.out::println);

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

    
}

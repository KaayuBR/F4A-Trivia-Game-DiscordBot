package triviabot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

import com.google.gson.Gson;

public class ListaPerguntas {

	private String output = "";
	
	private ArrayList<Pergunta> perguntas;
	
	private List<Integer> listaDeIds = new ArrayList<Integer>();

    public ListaPerguntas() {
    	try {
			IdsDasPerguntas();
			try (Scanner scanner = new Scanner(output)) {
				listaDeIds = new ArrayList<Integer>();
				while (scanner.hasNextInt()) {
				    listaDeIds.add(scanner.nextInt());
				}
			}
			System.out.println(listaDeIds);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	perguntas = new ArrayList<>();
        perguntas = leitorDePerguntas();
    }

    private ArrayList<Pergunta> leitorDePerguntas() {

        try {
            int numeroPerguntas = listaDeIds.size();

            for (int i = 0; i < numeroPerguntas; i++) {
            	int codigo = listaDeIds.get(i);
            	ArrayList<String> perguntaAtual = CarregarPergunta(codigo);
                ArrayList<String> alternativas = new ArrayList<>();
                alternativas.add(perguntaAtual.get(1));
                alternativas.add(perguntaAtual.get(2));
                alternativas.add(perguntaAtual.get(3));
                alternativas.add(perguntaAtual.get(4));

                perguntas.add(new Pergunta(perguntaAtual.get(0), alternativas, perguntaAtual.get(5)));
            }

            Collections.shuffle(perguntas);

        } catch (NumberFormatException | NullPointerException | IOException e) {
            Discordbot.getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()), e);
        }

        return perguntas;

    }

    private ArrayList<String> CarregarPergunta(int codigo) throws MalformedURLException, IOException {
    	 String url = "http://localhost:8080/api/questao/consultar/";

         HttpURLConnection conn = (HttpURLConnection) new URL(url+codigo).openConnection();

         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "application/json");

         if (conn.getResponseCode() != 200) {
             System.out.println("Erro " + conn.getResponseCode() + " ao obter dados da URL " + url);
         }

         BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
         String output = "";
         String line;
         while ((line = br.readLine()) != null) {
             output += line;
         }

         conn.disconnect();
         System.out.println(output);
         Gson gson = new Gson();
         Map map = gson.fromJson(new String(output.getBytes(), StandardCharsets.UTF_8), Map.class);
         ArrayList<String>perguntacarregada = new ArrayList<>();
         perguntacarregada.add((String) map.get("enunciado"));
         perguntacarregada.add((String) map.get("alternativaA"));
         perguntacarregada.add((String) map.get("alternativaB"));
         perguntacarregada.add((String) map.get("alternativaC"));
         perguntacarregada.add((String) map.get("alternativaD"));
         perguntacarregada.add((String) map.get("resposta"));
         System.out.println(perguntacarregada);
		return perguntacarregada;
		
	}

	public List<Pergunta> getPerguntas() {
        return perguntas;
    }

    public void IdsDasPerguntas() throws MalformedURLException, ProtocolException, IOException {
   	     String url = "http://localhost:8080/api/questoes";

         HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "application/json");

         if (conn.getResponseCode() != 200) {
             System.out.println("Erro " + conn.getResponseCode() + " ao obter dados da URL " + url);
         }

         BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
         output = "";
         String line;
         while ((line = br.readLine()) != null) {
             output += line;
         }

         conn.disconnect();

         System.out.println(output);
    }
}
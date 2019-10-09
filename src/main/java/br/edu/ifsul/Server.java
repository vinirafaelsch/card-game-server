package br.edu.ifsul;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server extends Thread {

    private Socket con;
    private InputStream in;
    private BufferedReader bfr;
    private InputStreamReader inr;
    private static ServerSocket server;
    private static ArrayList<BufferedWriter> clientes;
    private List<Card> deckClient = new ArrayList<>();
    private List<Card> deckServer = new ArrayList<>();

    /**
     * Método construtor
     */
    public Server(Socket con) {
        this.con = con;
        try {
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método run
     */
    public void run() {
        try {
            String msg = "";
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw);

            for(int i = 1; i <= 10; i++) {
                this.deckClient.add(Card.getRandomCard());
                this.deckServer.add(Card.getRandomCard());
            }

            Card random = this.deckClient.get(0);
            this.deckClient.remove(random);

            JSONObject jsonObject = new JSONObject()
                    .put("card", random)
                    .put("name", " A sua carta é " + random.getName())
                    .put("strength", "   [1] Força: " + random.getStrength())
                    .put("defense", "   [2] Defesa: " + random.getDefense())
                    .put("stamina", "   [3] Stamina: " + random.getStamina());

            bfw.write(jsonObject.toString() + "\r\n");
            bfw.flush();

            while (msg != null && !msg.equalsIgnoreCase("sair")) {
                msg = bfr.readLine();
                sentToOne(bfw, msg);
                /** sendToAll(bfw, msg); */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sentToOne(BufferedWriter bfw, String msg) throws IOException {
        String nome = "temp", message = "temp";
        Opcao opcao = Opcao.STRENGTH;
        Card clientCard = Card.BORUTO;

        try {
            JSONObject json = new JSONObject(msg);
            nome = json.getString("nome");
            opcao = Opcao.getById(Integer.valueOf(json.getString("msg")));
            clientCard = Card.getByName(json.getString("card"));
        } catch (JSONException err) {
            err.printStackTrace();
        }

        Card serverCard = this.deckServer.get(0);
        Integer value = null;
        Boolean res = false;
        if (opcao.equals(Opcao.STRENGTH)) {
            res = clientCard.getStrength() > serverCard.getStrength();
            value = serverCard.getStrength();
        } else if (opcao.equals(Opcao.STAMINA)) {
            res = clientCard.getStamina() > serverCard.getStamina();
            value = serverCard.getStamina();
        } else if (opcao.equals(Opcao.DEFENSE)) {
            res = clientCard.getDefense() > serverCard.getDefense();
            value = serverCard.getDefense();
        }

        JSONObject jsonObject = new JSONObject();

        if (res) {
            this.deckClient.add(serverCard);
            this.deckServer.remove(serverCard);
            jsonObject.put("resultado", "ganhou");
        } else {
            this.deckServer.add(clientCard);
            jsonObject.put("resultado", "perdeu");
        }

        Card cardClient = this.deckClient.get(0);
        this.deckClient.remove(cardClient);

        jsonObject.put("msg", "\nA carta do servidor era " + serverCard.getName() +
                "\nO atributo " + opcao.getName() + " da carta do servidor é: " + value);
        jsonObject.put("name", cardClient.getName());
        jsonObject.put("strength", "   [1] Força: " + cardClient.getStrength());
        jsonObject.put("defense", "   [2] Defesa: " + cardClient.getDefense());
        jsonObject.put("stamina", "   [3] Stamina: " + cardClient.getStamina());
        jsonObject.put("card", cardClient);

        bfw.write(jsonObject.toString() + "\r\n");
        bfw.flush();
    }

    /**
     * Método usado para enviar mensagem para todos os clients
     *
     * @param bwSaida do tipo BufferedWriter
     * @param msg     do tipo String
     * @throws IOException
     */
    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bwToRemove = null;
        for (BufferedWriter bw : clientes) {
            if (bwSaida != bw) {
                String nome = "temp";
                String message = "temp";

                try {
                    JSONObject json = new JSONObject(msg);
                    nome = json.getString("nome");
                    message = json.getString("msg");
                } catch (JSONException err) {
                    err.printStackTrace();
                }

                try {
                    bw.write(nome + ": " + message + "\r\n");
                    bw.flush();
                } catch (SocketException se) {
                    bwToRemove = bw;
                }
            }
        }
        if (bwToRemove != null) {
            clientes.remove(bwToRemove);
        }
    }

    /**
     * Método main
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Integer porta = 25565;
            server = new ServerSocket(porta);
            clientes = new ArrayList<>();

            while (true) {
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                System.out.println("Cliente conectado...");
                Thread t = new Server(con);
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
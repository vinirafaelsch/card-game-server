package br.edu.ifsul;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private Socket con;
    private InputStream in;
    private BufferedReader bfr;
    private InputStreamReader inr;
    private static ServerSocket server;
    private static ArrayList<BufferedWriter> clientes;

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

            Card random = Card.getRandomCard();
            List<Card> deck = Card.getEnumList();
            deck.remove(random);

            JSONObject jsonObject = new JSONObject()
                    .put("card", random)
                    .put("info", "A sua carta é " + random.getName());

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

        Card serverCard = Card.BORUTO; /** Inicia como boruto so pra nao da erro */

        do {
            /**
             * Sempre pegar uma carta diferente da do cliente
             */
            serverCard = Card.getRandomCard();
        } while (serverCard.equals(clientCard));

        boolean res = false;
        if (opcao.equals(Opcao.STRENGTH)) {
            res = clientCard.getStrength() > serverCard.getStrength();
        } else if (opcao.equals(Opcao.STAMINA)) {
            res = clientCard.getStamina() > serverCard.getStamina();
        } else if (opcao.equals(Opcao.DEFENSE)) {
            res = clientCard.getDefense() > serverCard.getDefense();
        }

        bfw.write("a carta do servidor era " + serverCard.getName() + "\r\n");

        if (res) {
            bfw.write("ganhou" + "\r\n");
        } else {
            bfw.write("perdeu" + "\r\n");
        }

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
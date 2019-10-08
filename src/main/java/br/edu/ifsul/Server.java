package br.edu.ifsul;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

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

            while (msg != null && !msg.equalsIgnoreCase("sair")) {
                msg = bfr.readLine();
                System.out.println(msg);
                sendToAll(bfw, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
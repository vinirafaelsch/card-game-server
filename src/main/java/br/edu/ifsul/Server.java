package br.edu.ifsul;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {

    private Socket con;
    private String nome;
    private InputStream in;
    private BufferedReader bfr;
    private InputStreamReader inr;
    private static ServerSocket server;
    private static ArrayList<BufferedWriter> clientes;

    public Server(Socket con) {
        this.con = con;
        try {
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (Exception e) {
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

            nome = msg = bfr.readLine();

//            try {
//                String jsonString = bfr.readLine();
//                JSONObject jsonObject = new JSONObject(jsonString); ///bfr
//                nome = jsonObject.getString("nome");
//            } catch (JSONException err) {
//                err.printStackTrace();
//            }

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                msg = bfr.readLine();
                sendToAll(bfw, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método usado para enviar mensagem para todos os clients
     */
    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        for (BufferedWriter bw : clientes) {
            if (!(bwSaida == bw)) {
                JSONObject json = new JSONObject()
                        .put("nome", nome)
                        .put("msg", msg);

                bw.write(json.toString());
                bw.flush();
            }
        }
    }

    /**
     * Método main
     */
    public static void main(String[] args) {
        try {
            Integer porta = 25565;
            clientes = new ArrayList<>();
            server = new ServerSocket(porta);

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
package br.edu.ifsul;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.ExportException;
import java.util.ArrayList;

public class Server extends Thread {

    private Socket con;
    private String nome;
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
            String msg;
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw);
            nome = msg = bfr.readLine();

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                try {
                    msg = bfr.readLine();
                    sendToAll(bfw, msg);
                    System.out.println(msg);
                } catch (SocketException e) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Método usado para enviar mensagem para todos os clients
     * @param bwSaida do tipo BufferedWriter
     * @param msg do tipo String
     * @throws IOException
     */
    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bwS;

        for (BufferedWriter bw : clientes) {
            bwS = (BufferedWriter) bw;
            if (!(bwSaida == bwS)) {
                bw.write(nome + " -> " + msg + "\r\n");
                bw.flush();
            }
        }
    }

    /***
     * Método main
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
package br.edu.ifsul;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    private ServerSocket serverSocket;
    int porta;

    public Server() {
    }


    public static void main(String[] args) {

        try {
            Server server = new Server();
            //1 - Criar o servidor de conexÃµes

            ServerSocket serverSocket = server.criarServerSocket(5555);
            //2 -Esperar o um pedido de conexÃ£o;
            try {
                do {

                    System.out.println("Esperando conexao...");
                    Socket socket = server.esperaConexao(); //bloqueante
                    //3 - Criar streams de enechar socket de comunicaÃ§Ã£o entre servidor/cliente
                    trataConexão(socket);
                    System.out.println("Conexão com cliente estabelecida.");

                } while (true);
            } catch (Exception e) {
                System.out.println("Erro no event loop do main(): " + e.getMessage());
                serverSocket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Erro na main do ServerSocket " + e.);
            System.exit(0);
        }
    }

    private ServerSocket criarServerSocket(int porta) {
        try {
            this.serverSocket = new ServerSocket(porta);
        } catch (Exception e) {
            System.out.println("Erro na Criação do server Socket " + e.getMessage());
        }

        return serverSocket;
    }

    private Socket esperaConexao() {
        try {
            return this.serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Erro ao criar socket do cliente " + ex.getMessage());
            return null;
        }
    }

    private static void trataConexão(Socket socket) {
        //tratamento da comunicação com um cliente (socket)
        ObjectOutputStream output = null;
        ObjectInputStream input = null;


        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            output.flush();
            System.out.println("Conexao recebida, inciando protocolo...");
            //iniciar a conversa --- SINCRONO
            String msgResposta = "";
            String operacao = "";

            boolean primeira = true;
            //event loop
            do {
                //leitura
                String msgCliente = input.readUTF(); //bloqueante
                String response = "";
                System.out.println("Mensagem recebida do cliente: " + msgCliente);
                //escrita

                String[] protocolo = msgCliente.split(";");
                operacao = protocolo[0];
                switch (operacao) {
                    case "OI":
                        try {
                            String nome = protocolo[1].split(":")[1];

                            //escrevendo a resposta
                            if (nome == null) {
                                //faltou um parâmetro
                                response += "OIRESPONSE";
                                response += "\n400";
                            } else {
                                response += "OIREPONSE";
                                response += "\n200";
                                response += "\nmensagem:Olá, " + nome + "!";
                            }
                        } catch (Exception e) {
                            response += "OIRESPONSE";
                            response += "\n400";
                        }
                        break;
                    default:
                        //mensagem inválida
                        response += operacao.toUpperCase() + "RESPONSE";
                        response += "\n400";
                        System.out.println("Parando comunicacao com cliente " + socket.getInetAddress());
                        break;
                }
                //enviar a resposta ao cliente
                output.writeUTF(response);
                output.flush();
            } while (!operacao.equals("pare"));
        } catch (Exception e) {
            System.out.println("Erro no loop de tratamento do cliente: " + socket.getInetAddress().getHostAddress());
        } finally {
            try {
                //fechar as conexões
                output.close();
                input.close();
            } catch (IOException ex) {
                System.out.println("Erro normal ao fechar conexão do cliente..." + ex.getMessage());
            }
        }
    }
}

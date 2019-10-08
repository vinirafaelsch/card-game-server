package br.edu.ifsul;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements ActionListener, KeyListener {

    private Writer ouw;
    private JLabel lblMsg;
    private Socket socket;
    private JTextArea texto;
    private JButton btnSair;
    private JButton btnSend;
    private OutputStream ou;
    private JPanel pnlContent;
    private JTextField txtMsg;
    private BufferedWriter bfw;
    private JLabel lblHistorico;

    private String ip = "127.0.0.1";
    private String nome = "Jorgin";
    private Integer porta = 25565;

    public Client() {
        pnlContent = new JPanel();
        texto = new JTextArea(10, 20);
        texto.setEditable(false);
        texto.setBackground(new Color(240, 240, 240));
        txtMsg = new JTextField(20);
        lblHistorico = new JLabel("Histórico");
        lblMsg = new JLabel("Mensagem");
        btnSend = new JButton("Enviar");
        btnSend.setToolTipText("Enviar Mensagem");
        btnSair = new JButton("Sair");
        btnSair.setToolTipText("Sair do Chat");
        btnSend.addActionListener(this);
        btnSair.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(texto);
        texto.setLineWrap(true);
        pnlContent.add(lblHistorico);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnSair);
        pnlContent.add(btnSend);
        pnlContent.setBackground(Color.LIGHT_GRAY);
        texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        setTitle(this.nome);
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /**
         * Listener para quando o usuario fechar o JFrame na marra...
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    sair();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Método usado para conectar no server socket, retorna IO Exception caso dê algum erro.
     *
     * @throws IOException
     */
    public void conectar() throws IOException {
        socket = new Socket(this.ip, this.porta);
        ou = socket.getOutputStream();
        ouw = new OutputStreamWriter(ou);
        bfw = new BufferedWriter(ouw);
    }

    /**
     * Método usado para enviar mensagem para o server socket
     *
     * @param msg do tipo String
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void enviarMensagem(String msg) throws IOException {
        if (msg.equals("Sair")) {
            bfw.write("Desconectado \r\n");
            texto.append("Desconectado \r\n");
        } else {
            JSONObject json = new JSONObject()
                    .put("nome", nome)
                    .put("msg", msg);

            bfw.write(json.toString() + "\r\n");
            texto.append(this.nome + ": " + txtMsg.getText() + "\r\n");
        }
        bfw.flush();
        txtMsg.setText("");
    }

    /**
     * Método usado para receber mensagem do servidor
     *
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void escutar() throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while (!"Sair".equalsIgnoreCase(msg))
            if (bfr.ready()) {
                msg = bfr.readLine();
                if (msg.equals("Sair")) {
                    texto.append("Servidor caiu! \r\n");
                } else {
                    texto.append(msg + "\r\n");
                }
            }
    }

    /**
     * Método usado quando o usuário clica em sair
     *
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void sair() throws IOException {
        enviarMensagem("Sair");
        bfw.close();
        ouw.close();
        ou.close();
        socket.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals(btnSend.getActionCommand())) {
                enviarMensagem(txtMsg.getText());
            } else if (e.getActionCommand().equals(btnSair.getActionCommand())) {
                sair();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                enviarMensagem(txtMsg.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) throws IOException {
        Client app = new Client();
        app.conectar();
        app.escutar();
    }
}

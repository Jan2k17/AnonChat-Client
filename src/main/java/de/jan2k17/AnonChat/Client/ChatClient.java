package de.jan2k17.AnonChat.Client;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame implements KeyListener {
    private static final long serialVersionUID = -8930734853300523706L;
    private String address = "127.0.0.1";
    private String nickname;
    private int port = 0;
    private Socket connectionToServer;
    private BufferedReader fromServerReader;
    private PrintWriter toServerWriter;
    private JTextArea outputTextArea;
    private JTextField inputTextField;
    private JScrollPane outputScrollPane;

    public ChatClient() {
        super("Chat");
        String[] options = { "Chat #1", "Chat #2", "Chat #3", "Chat #4", "Chat #5", "Chat #6", "Chat #7", "Chat #8", "Chat #9", "Chat #10" };
        int chatr = JOptionPane.showOptionDialog(null, "Select a chatroom",
                "AnonChat room selector",
                -1, 1, null, options, options[0]);
        this.port = 3140 + chatr + 1;
        JFrame jf = this;
        jf.setTitle("Chat #" + (chatr + 1));
        if (this.port != 0 && this.address != null) {
            this.nickname = JOptionPane.showInputDialog("Nickname:");
            if (this.address != null && this.port != 0 && this.nickname != null)
                receiveMessages();
        }
    }

    private void initGui() {
        this.outputTextArea = new JTextArea();
        this.outputTextArea.setEditable(false);
        this.outputTextArea.setBorder(BorderFactory.createTitledBorder("Chat"));
        this.outputScrollPane = new JScrollPane(this.outputTextArea);
        this.inputTextField = new JTextField();
        this.inputTextField.setBorder(BorderFactory.createTitledBorder("enter message"));
        this.inputTextField.addKeyListener(this);
        add(this.outputScrollPane, "Center");
        add(this.inputTextField, "South");
        setVisible(true);
        setDefaultCloseOperation(3);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void receiveMessages() {
        try {
            this.connectionToServer = new Socket(this.address, this.port);
            this.fromServerReader = new BufferedReader(new InputStreamReader(this.connectionToServer.getInputStream()));
            this.toServerWriter = new PrintWriter(new OutputStreamWriter(this.connectionToServer.getOutputStream()));
            initGui();
            while (true) {
                String message = this.fromServerReader.readLine();
                this.outputTextArea.append(message + "\n");
                this.outputScrollPane.getVerticalScrollBar().setValue(this.outputScrollPane.getVerticalScrollBar().getMaximum());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection to chatroom failed!");
            dispose();
        } finally {
            if (this.connectionToServer != null)
                try {
                    this.connectionToServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (this.fromServerReader != null)
                try {
                    this.fromServerReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (this.toServerWriter != null)
                this.toServerWriter.close();
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            String message = this.inputTextField.getText();
            if (!message.isEmpty()) {
                this.toServerWriter.println(this.nickname + " -:- " + message);
                this.toServerWriter.flush();
                this.inputTextField.setText("");
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {}
}
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.regex.*;

public class ChatUDP extends JFrame {
    private String Name;
    private JTextArea taMain;
    private JTextField tfMsg;
    private  final String FRM_TITLE = "Our Tiny Chat";
    private final int FRM_LOC_X = 100;
    private final int FRM_LOC_Y = 100;
    private final int FRM_WIDTH = 400;
    private final int FRM_HEIGHT = 400;
    private  final int PORT = 9876;
    private final String IP_BROADCAST = "10.114.6.174";

    public ChatUDP(){
    }

    private class thdReceiver extends Thread{
        @Override
        public void start(){
            super.start();
            try{
                customize();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        private void customize() throws Exception{
            DatagramSocket receiveSocket = new DatagramSocket(PORT);
            while(true){
                byte[] reciveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(reciveData, reciveData.length);
                receiveSocket.receive(receivePacket);
               // InetAddress IPAddress = receivePacket.getAddress();
                //int port = receivePacket.getPort();
                String sentence = new String(receivePacket.getData());
                taMain.append(Name+":"+sentence+"\r\n");
                taMain.setCaretPosition(taMain.getText().length());
            }
        }
    }

    private void btnSend_Handler() throws Exception{
        DatagramSocket sendSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(IP_BROADCAST);
        byte[] sendData;
        String sentence = tfMsg.getText();
        tfMsg.setText("");
        sendData = sentence.getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, IPAddress, PORT);
        sendSocket.send(sendPacket);
    }

    private void frameDraw(JFrame frame){
        tfMsg = new JTextField();
        taMain = new JTextArea(FRM_HEIGHT/19,50);
        JButton btnSend = new JButton();
        JScrollPane spMain = new JScrollPane(taMain);
        spMain.setLocation(0,0);
        taMain.setLineWrap(true);
        taMain.setEditable(false);
        btnSend.setText("Send");
        btnSend.setToolTipText("Broadcast message");
        btnSend.addActionListener(e -> {
        try {
            btnSend_Handler();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        });
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocation(FRM_LOC_X,FRM_LOC_Y);
        frame.setSize(FRM_WIDTH,FRM_HEIGHT);
        frame.setResizable(false);
        frame.getContentPane().add(BorderLayout.NORTH,spMain);
        frame.getContentPane().add(BorderLayout.CENTER, tfMsg);
        frame.getContentPane().add(BorderLayout.EAST,btnSend);
        frame.setVisible(true);
        Name = JOptionPane.showInputDialog(frame,"Введите своё имя");
    }
    private void antistatic() {
        frameDraw(new ChatUDP());
        new thdReceiver().start();
    }
    public static void main(String[] args){
        new ChatUDP().antistatic();
    }
}

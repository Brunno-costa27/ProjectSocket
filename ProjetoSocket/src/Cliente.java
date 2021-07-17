import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;


/*
 * a classe herda de JFrame, possibilitando a criação de formulários e implementação
 * das interfaces ActionListener e KeyListener para prover ações nos botões e ações
 * das teclas, respectivamente.
 * */
public class Cliente extends JFrame implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnSair;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream ou ;
	private Writer ouw;
	private BufferedWriter bfw;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField txtNome;
	
	/*
	 * mostra a declaração do método construtor, que verifica os objetos sendo instanciados
	 * para a construção da tela do chat. Lembre-se que cada cliente deverá ser uma instância
	 * independente.
	 * */
	
	public Cliente() throws IOException{
	    JLabel lblMessage = new JLabel("Verificar!");
	    txtIP = new JTextField("127.0.0.1");
	    txtPorta = new JTextField("12345");
	    txtNome = new JTextField("Cliente");
	    Object[] texts = {lblMessage, txtIP, txtPorta, txtNome };
	    JOptionPane.showMessageDialog(null, texts);
	     pnlContent = new JPanel();
	     texto              = new JTextArea(10,20);
	     texto.setEditable(false);
	     texto.setBackground(new Color(240,240,240));
	     txtMsg                       = new JTextField(20);
	     lblHistorico     = new JLabel("Histórico");
	     lblMsg        = new JLabel("Mensagem");
	     btnSend                     = new JButton("Enviar");
	     btnSend.setToolTipText("Enviar Mensagem");
	     btnSair           = new JButton("Sair");
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
	     texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE,Color.BLUE));
	     txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
	     setTitle(txtNome.getText());
	     setContentPane(pnlContent);
	     setLocationRelativeTo(null);
	     setResizable(false);
	     setSize(250,300);
	     setVisible(true);
	     setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	/*
	 * é usado para conectar o cliente com o servidor socket. 
	 * Nesse método é possível visualizar a criação do socket
	 * cliente e dos streams de comunicação.
	 * */
	
	public void conectar() throws IOException{

		  socket = new Socket(txtIP.getText(),Integer.parseInt(txtPorta.getText()));
		  ou = socket.getOutputStream();
		  ouw = new OutputStreamWriter(ou);
		  bfw = new BufferedWriter(ouw);
		  bfw.write(txtNome.getText()+"\r\n");
		  bfw.flush();
	}
	
	/*
	 * tem o método usado para enviar mensagens do cliente para o servidor socket. 
	 * Assim, toda vez que ele escrever uma mensagem e apertar o botão “Enter”, 
	 * esta será enviada para o servidor.
	 * */
	
	public void enviarMensagem(String msg) throws IOException{

	    if(msg.equals("Sair")){
	      bfw.write("Desconectado \r\n");
	      texto.append("Desconectado \r\n");
	    }else{
	      bfw.write(msg+"\r\n");
	      texto.append( txtNome.getText() + " diz -> " +         txtMsg.getText()+"\r\n");
	    }
	     bfw.flush();
	     txtMsg.setText("");
	}
	
	
	/*
	 * temos o método usado para escutar (receber) mensagens do servidor. 
	 * Toda vez que alguém enviar uma, o método será processado pelo servidor
	 *  e envia para todos os clientes conectados, por isso a necessidade do código.
	 * */
	
	public void escutar() throws IOException{

		   InputStream in = socket.getInputStream();
		   InputStreamReader inr = new InputStreamReader(in);
		   BufferedReader bfr = new BufferedReader(inr);
		   String msg = "";

		    while(!"Sair".equalsIgnoreCase(msg))

		       if(bfr.ready()){
		         msg = bfr.readLine();
		       if(msg.equals("Sair"))
		         texto.append("Servidor caiu! \r\n");
		        else
		         texto.append(msg+"\r\n");
		        }
		}
	
	
	/*
	 *  usado para desconectar do server socket. Nele o sistema apenas fecha os streams de comunicação.
	 * */
	public void sair() throws IOException{

		   enviarMensagem("Sair");
		   bfw.close();
		   ouw.close();
		   ou.close();
		   socket.close();
		}
	
	/*
	 * Nele foi feito um chaveamento: se o usuário pressionar o botão “send” 
	 * então será enviada uma mensagem, senão será encerrado o chat.
	 * */
	
	@Override
	public void actionPerformed(ActionEvent e) {

	  try {
	     if(e.getActionCommand().equals(btnSend.getActionCommand()))
	        enviarMensagem(txtMsg.getText());
	     else
	        if(e.getActionCommand().equals(btnSair.getActionCommand()))
	        sair();
	     } catch (IOException e1) {
	          // TODO Auto-generated catch block
	          e1.printStackTrace();
	     }
	}
	
	/*
	 * é acionado quando o usuário pressiona “Enter”, verificando 
	 * se o key code é o Enter. Caso seja, a mensagem é enviada para o servidor.
	 * */
	
	@Override
	public void keyPressed(KeyEvent e) {

	    if(e.getKeyCode() == KeyEvent.VK_ENTER){
	       try {
	          enviarMensagem(txtMsg.getText());
	       } catch (IOException e1) {
	           // TODO Auto-generated catch block
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
	
	
	/*
	 * mostra o método main, onde é criado apenas um cliente e são configurados os métodos conectar e escutar.
	 * */
	public static void main(String []args) throws IOException{

		   Cliente app = new Cliente();
		   app.conectar();
		   app.escutar();
		}
	
}

package ä��Ŭ���̾�Ʈ;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame implements ActionListener, KeyListener {

	//Login GUI ����
	private JFrame Login_GUI = new JFrame();
	private JPanel login_Pane;
	private JTextField ip_ft;	//ip �޴� �ؽ�Ʈ �ʵ�
	private JTextField port_ft;	//port �޴� �ؽ�Ʈ �ʵ�
	private JTextField id_ft;	//id �޴� �ؽ�Ʈ �ʵ�
	private JButton login_button = new JButton("�� �� �� ư");	//���ӹ�ư
	
	//main GUI ����
	private JPanel contentPane;
	private JTextField message_tf;
	private JButton notesend_button = new JButton("�� �� �� �� ��");
	private JButton joinroom_button = new JButton("ä �� �� �� ��");
	private JButton createroom_button = new JButton("�� �� �� ��");
	private JButton send_button = new JButton("�� ��");
	
	
	private JList User_list = new JList();	//��ü ������ list
	private JList Room_list = new JList();	//��ü �� ��� list
	
	private JTextArea Chat_area = new JTextArea();	//ä��â ����
	
	//��Ʈ��ũ�� ���� �ڿ� ����
	
	private Socket socket;
	private String ip;	//1���� �ڱ� �ڽ�
	private int port;
	private String id="";
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	//�׿� ���� ��
	Vector user_list = new Vector();
	Vector room_list = new Vector();
	StringTokenizer st;
	
	private String My_Room;	//���� �ִ� ���̸�
	
	Client(){
		
		Login_init();	//loginâ ȭ�� ���� �޼ҵ�
		Main_init();	//mainâ ȭ�� ���� �޼ҵ�
		start();
		
	}
	
	private void start() {
		login_button.addActionListener(this);		//�α��� ��ư ������
		notesend_button.addActionListener(this);	//���������� ��ư ������
		joinroom_button.addActionListener(this);	//ä�ù� ���� ��ư ������
		createroom_button.addActionListener(this);	//ä�ù� ����� ��ư ������
		send_button.addActionListener(this);		//ä�� ���� ��ư ������
		message_tf.addKeyListener(this);
	}
	
	private void Main_init() { //client ȭ�鱸��
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 586, 525);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("�� ü �� �� ��");
		lblNewLabel.setBounds(12, 10, 96, 21);
		contentPane.add(lblNewLabel);
		

		User_list.setBounds(12, 29, 104, 163);
		contentPane.add(User_list);
		//User_list.setListData(user_list);

		notesend_button.setBounds(12, 202, 104, 23);
		contentPane.add(notesend_button);
		
		JLabel lblNewLabel_1 = new JLabel("ä �� �� �� ��");
		lblNewLabel_1.setBounds(12, 236, 91, 21);
		contentPane.add(lblNewLabel_1);
		

		Room_list.setBounds(12, 254, 104, 163);
		contentPane.add(Room_list);
		//Room_list.setListData(room_list);
		
		
		joinroom_button.setBounds(12, 425, 104, 23);
		contentPane.add(joinroom_button);
		
		
		createroom_button.setBounds(12, 455, 104, 23);
		contentPane.add(createroom_button);
		
		message_tf = new JTextField();
		message_tf.setBounds(120, 456, 369, 22);
		contentPane.add(message_tf);
		message_tf.setColumns(10);
		message_tf.setEnabled(false);
		

		send_button.setBounds(495, 455, 65, 23);
		contentPane.add(send_button);
		send_button.setEnabled(false);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(120, 8, 440, 432);
		contentPane.add(scrollPane);
		

		scrollPane.setViewportView(Chat_area);
		Chat_area.setEnabled(false);
		
		this.setVisible(false);	//true��� ȭ�� ���̰�, false��� ȭ�� �Ⱥ��̰�
		
	}
	
	private void Login_init() { //login ȭ�鱸��
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Login_GUI.setBounds(100, 100, 308, 425);
		login_Pane = new JPanel();
		login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(login_Pane);
		login_Pane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(24, 196, 82, 28);
		login_Pane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Port");
		lblNewLabel_1.setBounds(24, 234, 82, 28);
		login_Pane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("ID");
		lblNewLabel_2.setBounds(24, 283, 82, 28);
		login_Pane.add(lblNewLabel_2);
		
		ip_ft = new JTextField();
		ip_ft.setBounds(109, 197, 153, 28);
		login_Pane.add(ip_ft);
		ip_ft.setColumns(10);
		
		port_ft = new JTextField();
		port_ft.setColumns(10);
		port_ft.setBounds(109, 234, 153, 28);
		login_Pane.add(port_ft);
		
		id_ft = new JTextField();
		id_ft.setColumns(10);
		id_ft.setBounds(109, 283, 153, 28);
		login_Pane.add(id_ft);
		

		login_button.setBounds(24, 343, 238, 23);
		login_Pane.add(login_button);
		
		Login_GUI.setVisible(true); //true��� ȭ�鿡 ���δ�, false��� ȭ�鿡 ������ �ʴ´�.
	}
	
	private void Network() {
		try {
			socket = new Socket(ip,port);
			
			if(socket !=null) {	//���������� ������ ����Ǿ��� ���
				Connection();
				
			}
			
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog
			(null, "���� ����","�˸�",JOptionPane.ERROR_MESSAGE);//error�޽����� �ߴµ� �ð��̰ɸ��� ��ǻ�Ͱ� ip�� ã�� �ð��� �ֱ� �����̴�.
		} catch (IOException e) {
			JOptionPane.showMessageDialog
			(null, "���� ����","�˸�",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private void Connection() {
		
		try {
		is = socket.getInputStream();
		dis = new DataInputStream(is);
		
		os= socket.getOutputStream();
		dos = new DataOutputStream(os);
		}
		catch(IOException e)//���� ó���κ�
		{
			JOptionPane.showMessageDialog
			(null, "���� ����","�˸�",JOptionPane.ERROR_MESSAGE);
		}//Stream ���� ��
		
		this.setVisible(true);	//main ui ǥ��
		this.Login_GUI.setVisible(false);	//Login gui â �ݱ�
		
		//ó�� ���ӽÿ� ID����
		send_message(id);
		
		//user_list�� ����� �߰�
		user_list.add(id);
		User_list.setListData(user_list);	//user_list_update ������ �̰� ��ߵ�
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					try {
					String msg=dis.readUTF(); // �޽��� ����
					
					System.out.println("�����κ��� ���ŵ� �޼��� : "+msg);
					
					inmessage(msg);
					
					}
					catch(IOException e) {
						try {
					os.close();
					is.close();
					dos.close();
					dis.close();
					socket.close();
					JOptionPane.showMessageDialog
					(null, "������ ���� ������","�˸�",JOptionPane.ERROR_MESSAGE);
						}catch(IOException e1){}
					break;
					}
					}
				
			}
			
		});
		
	th.start();
	
	}
	
	private void inmessage(String str) {	//�����κ��� ������ ��� �޼���
		
		st = new StringTokenizer(str,"/");
		
		String protocol = st.nextToken();
		String Message = st.nextToken();
		
		System.out.println("�������� : " + protocol);
		System.out.println("���� : " + Message);
		
		
		if(protocol.contentEquals("NewUser")) { //���ο� �����ڸ� â�� ǥ��
			user_list.add(Message);
			User_list.setListData(user_list);	//user_list_update ������ �̰� ��ߵ�

		}
		else if(protocol.contentEquals("OldUser")) {
			user_list.add(Message);
			User_list.setListData(user_list);	//user_list_update ������ �̰� ��ߵ�
		}
		else if(protocol.contentEquals("Note")) {

			String note = st.nextToken();
			
			System.out.println(Message = "����ڷκ��� �� ���� : " + note);
			
			JOptionPane.showMessageDialog
			(null, note,Message +"������ ���� ����",JOptionPane.CLOSED_OPTION);
		}
		/*else if(protocol.contentEquals("user_list_update")) {
			User_list.setListData(user_list);
		}*/ //��� �־ȵǴ��� �𸣰ڴ�.
		else if(protocol.contentEquals("CreateRoom")) {	//���� ������� ��
			joinroom_button.setEnabled(false);
			createroom_button.setEnabled(false);
			message_tf.setEnabled(true);
			send_button.setEnabled(true);
			My_Room = Message;
		}
		else if(protocol.contentEquals("CreateRoomFail")) {	//���� ��������� ��
			JOptionPane.showMessageDialog
			(null, "�� ����� ����","�˸�",JOptionPane.ERROR_MESSAGE);
		}
		else if(protocol.contentEquals("New_Room")) {
			room_list.add(Message);
			Room_list.setListData(room_list);
		}
		else if(protocol.contentEquals("OldRoom")) {
			room_list.add(Message);
			Room_list.setListData(room_list);
		}
		
		else if(protocol.contentEquals("Chatting")) {
			String msg =st.nextToken();
			Chat_area.append(Message + " : " + msg +"\n");
		}
		else if(protocol.contentEquals("JoinRoom")) {
			message_tf.setEnabled(true);
			send_button.setEnabled(true);
			joinroom_button.setEnabled(false);
			createroom_button.setEnabled(false);
			My_Room =Message;
			JOptionPane.showMessageDialog
			(null, "ä�ù濡 �����߽��ϴ�.","�˸�",JOptionPane.INFORMATION_MESSAGE);
		}
		else if(protocol.contentEquals("User_out")){
				user_list.remove(Message);
				User_list.setListData(user_list);
			}
		
	}
	
	private void send_message(String str) {	//�������� �޼����� ������ �κ�
		
		try {
			dos.writeUTF(str);	//����ڰ� �������� �޽����� ������ �κ�
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//login_button - �α��� ��ư
		if(e.getSource()==login_button) {
			System.out.println("�α��� ��ư Ŭ��");
			
			if(ip_ft.getText().length()==0) {
				ip_ft.setText("IP�� �Է����ּ���");
				ip_ft.requestFocus();
			}
			else if(port_ft.getText().length()==0) {
				port_ft.setText("PORT�� �Է����ּ���");
				port_ft.requestFocus();
			}
			else if(id_ft.getText().length()==0) {
				id_ft.setText("ID�� �Է����ּ���");
				id_ft.requestFocus();
			}
			else {
			ip=ip_ft.getText().trim();	//ip�� �޾ƿ��� �κ� //trim�� ������ �����ִ� �޼ҵ�
			port = Integer.parseInt(port_ft.getText().trim());
			
			id=id_ft.getText().trim();	//id�� �޾ƿ��� �κ�
			Network(); 
			}
			
			
		}
		else if(e.getSource()==notesend_button) {
			System.out.println("���� ������ ��ư Ŭ��");
			String user = (String)User_list.getSelectedValue();	//����Ʈ �׸� �ִ� ���� String���� ��ȯ
			
			String note = JOptionPane.showInputDialog("�����޼���"); //�޽��� ������ �ڵ� gui�⺻ ���� �ڵ���
			
			if(note != null) {
				send_message("Note/"+user+"/"+note);
				//ex) Note/User2/���� õ���
			}
			System.out.println("�޴»�� : " + user + "| ���� ���� : " + note);
		}
		else if(e.getSource()==joinroom_button) {
			
			String JoinRoom =(String)Room_list.getSelectedValue();
			send_message("JoinRoom/"+JoinRoom);
			
			System.out.println("ä�ù� ���� ��ư Ŭ��");
		}
		else if(e.getSource()==createroom_button) {
			String roomname = JOptionPane.showInputDialog("�� �̸�");
			if(roomname !=null) {
				send_message("CreateRoom/"+roomname);
				
			}
			System.out.println("ä�� �游��� ��ư Ŭ��");
		}
		else if(e.getSource()==send_button) {
			
			send_message("Chatting/" + My_Room+"/" + message_tf.getText().trim());
			message_tf.setText("");
			message_tf.requestFocus();
			
			System.out.println("ä�� ���� ��ư Ŭ��");
		}
		
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e);
		if(e.getKeyCode()==10) {
		send_message("Chatting/" + My_Room+"/" + message_tf.getText().trim());
		message_tf.setText("");
		message_tf.requestFocus();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

}


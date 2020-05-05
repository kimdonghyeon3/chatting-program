package ä�����α׷�1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener {
	//ctrl + shift + o�� �ڵ� import����
	
	private JPanel contentPane;
	private JTextField port_tf;
	private JTextArea textArea = new JTextArea();
	private JButton start_button = new JButton("���� ����");
	private JButton stop_button = new JButton("���� ����");
	
	//Network �ڿ�
	
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector user_vc = new Vector();
	private Vector room_vc = new Vector();
	
	private StringTokenizer st;

	
	Server(){	//������
		init(); //ȭ�� ���� �޼ҵ�
		start();
	}
	
	private void start() {
		start_button.addActionListener(this);
		stop_button.addActionListener(this);
		
	}
	
	private void init() {	//ȭ�鱸��
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 366);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("��Ʈ ��ȣ");
		lblNewLabel.setBounds(12, 235, 52, 24);
		contentPane.add(lblNewLabel);
		
		start_button.setBounds(12, 282, 131, 37);
		contentPane.add(start_button);
		

		stop_button.setBounds(155, 282, 139, 37);
		contentPane.add(stop_button);
		stop_button.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 282, 201);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		
		port_tf = new JTextField();
		port_tf.setBounds(66, 237, 228, 22);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		this.setVisible(true); //true ��� ȭ�鿡 ���̰� false = ȭ�鿡 ������ �ʰ�
	}

	private void Server_start() {
		try {
			server_socket = new ServerSocket(port);	//12345��Ʈ ���
		} catch (IOException e) {
			JOptionPane.showMessageDialog
			(null, "�̹� ������� ��Ʈ","�˸�",JOptionPane.ERROR_MESSAGE);
		}
		
		if(server_socket !=null) {	//���������� ��Ʈ�� ���������
			Connection();
			
		}
		
	}
	
	private void Connection() {
		//1������ �����忡���� 1������ �ϸ� ó���� �� �ֵ�.
		
		Thread th = new Thread(new Runnable(){
			
			@Override
			public void run() {//�����忡�� ó���� ���� �����Ѵ�.
				
				while(true) {
				try {
					textArea.append("����� ���� �����\n");
					socket=server_socket.accept();//����� ���� ��� ���Ѵ��
					textArea.append("����� ����!!!\n");
					
					UserInfo user=new UserInfo(socket);
					
					user.start();//��ü�� ������ �����带 �����Ų��.
					
				} catch (IOException e) {
					
					break;
				}
				}//while�� ��
				
			}
			
		});
		
		th.start();
		
	}
	
	public static void main(String[] args) {
		
		new Server();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==start_button) {
			System.out.println("��ŸƮ ��ư Ŭ��");
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			Server_start();	//���� ���� �� ����� ���Ӵ��
			
			start_button.setEnabled(false);
			port_tf.setEditable(false);
			stop_button.setEnabled(true);
		}
		else if(e.getSource()==stop_button) {
			
			stop_button.setEnabled(false);
			start_button.setEnabled(true);
			port_tf.setEditable(true);
			
			try {
				server_socket.close();
				user_vc.removeAllElements();
				room_vc.removeAllElements();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("��ž ��ư Ŭ��");
		}
		
	} // ���� �̺�Ʈ ��
	
	class UserInfo extends Thread{
		
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private Socket user_socket;
		private String Nickname="";
		
		private boolean RoomCh = true;
		
		UserInfo(Socket soc){ //������ �޼ҵ�
			this.user_socket = soc;
			UserNetwork();
			
		}
		
		private void UserNetwork() { //��Ʈ��ũ �ڿ� ����
			
			try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			
			os=user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			Nickname = dis.readUTF();	//������� �г����� �޴´�
			textArea.append(Nickname + " : ����� ����\n");
			
			//���� ����ڿ��� ���ο� ����� �˸�
			System.out.println("���� ���ӵ� ����� �� : " + user_vc.size());
			
			BroadCast("NewUser/"+Nickname);	//���� ����ڿ��� �ڽ��� �˸���.
			
			//�ڽſ��� ���� ����ڸ� �޾ƿ��� �κ�
			for(int i = 0; i<user_vc.size(); i++) {
				
				UserInfo u =(UserInfo)user_vc.elementAt(i);
				
				send_Message("OldUser/"+ u.Nickname);
			}
			
			//�ڽſ��� ���� ���� �޾ƿ��� �κ�
			for(int i = 0; i<room_vc.size(); i++) {
				Roominfo r =(Roominfo)room_vc.elementAt(i);
				send_Message("OldRoom/"+ r.Room_name);
			}
			
			//send_Message("room_list_update/");
			
			user_vc.add(this); //����ڿ��� �˸� �� Vector�� �ڽ��� �߰�
			
			//BroadCast("user_list_updata/");	//user_list_update ������ �̰� ���� �ȵ�
			
			}
			catch(IOException e) {JOptionPane.showMessageDialog
				(null, "Stream���� ����","�˸�",JOptionPane.ERROR_MESSAGE);
			}
			}
		
		public void run() { //Thread�� ó���� ����
			while(true) {
				
				try {
					String msg = dis.readUTF();
					textArea.append(Nickname + " : ����ڷκ��� ���� �޽��� : "+msg + "\n");
					InMessage(msg);
				} catch (IOException e) {
					textArea.append(Nickname + "����� ���� ������\n");
					try {
					dos.close();
					dis.close();
					user_socket.close();
					user_vc.remove(this);
					BroadCast("User_out/"+Nickname);
					}
					catch(IOException e1) {}
					break;
				}
			} //while�� ��
			
		} // RUN�޼ҵ� ��
		
		private void InMessage(String str) { //Ŭ���̾�Ʈ�� ���� ������ �޼��� ó��
			st= new StringTokenizer(str,"/");
			
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			System.out.println("�������� : " + protocol);
			System.out.println("�޼��� : " + message);
			
			if(protocol.contentEquals("Note")) {
				
				String note = st.nextToken();
				
				System.out.println("�޴� ��� : " + message + "\n������ �޽��� : " + note);
			
			//���Ϳ��� �ش� ����ڸ� ã�Ƽ� �޽��� ����
				
				for(int i =0;i<user_vc.size();i++) {
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					if(u.Nickname.contentEquals(message)) {
						u.send_Message("Note/"+Nickname+"/"+note);
						//Note/User1/~~~
					}
				}
			
			} //if�� ��
			else if(protocol.contentEquals("CreateRoom")) {
				for(int i =0;i<room_vc.size();i++) {	//���� ���� �����ϴ��� Ȯ��
					Roominfo r = (Roominfo)room_vc.elementAt(i);
					if(r.Room_name.contentEquals(message)) {	//������� �ϴ� ���� �̹� ������ ��
						send_Message("CreateRoomFail/ok");
						RoomCh = false;
					break;
					}
				}//for�� ��
			if(RoomCh) {
				Roominfo new_room = new Roominfo(message, this);	//���� ���� �� ���� ��(�������� ���� ��)
				room_vc.add(new_room);	//õü �� ���Ϳ� ���� �߰�
				
				send_Message("CreateRoom/"+message);
				
				BroadCast("New_Room/" + message);
			}
			
			RoomCh=true;
			}	//else if�� ��
			
			else if(protocol.contentEquals("Chatting")) {
				String msg = st.nextToken();
				
				for(int i =0; i< room_vc.size(); i++) {
					Roominfo r = (Roominfo)room_vc.elementAt(i);
							
				if(r.Room_name.contentEquals(message)) {
					r.BroadCast_Room("Chatting/"+Nickname+"/"+msg);
				}
				}
			}	//else if�� ��
			else if(protocol.contentEquals("JoinRoom")) {
				for(int i =0; i<room_vc.size();i++) {
					Roominfo r = (Roominfo)room_vc.elementAt(i);
					if(r.Room_name.contentEquals(message)) {
						
						//���ο� ����ڸ� �˸���.
						r.BroadCast_Room("Chatting/�˸�/****"+Nickname+"���� �����ϼ̽��ϴ�****");
						
						//����� �߰�
						r.Add_User(this);
						send_Message("JoinRoom/"+message);
					}
				}
			}
			
		}
		
		private void BroadCast(String str) {	//��ü ����ڿ��� �޼��� ������ �κ�
			for(int i =0;i<user_vc.size();i++) {
				UserInfo u = (UserInfo)user_vc.elementAt(i);
				
				u.send_Message(str);
			}
		}
		
		private void send_Message(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	} // Userinfo class ��
	
	class Roominfo{
		private String Room_name;
		private Vector Room_user_vc = new Vector();
		
		Roominfo(String str, UserInfo u){
			this.Room_name = str;
			this.Room_user_vc.add(u);
			
		}
		public void BroadCast_Room(String str){	//���� ���� ��� ������� �˸���.
			for(int i = 0; i<Room_user_vc.size();i++) {
				UserInfo u = (UserInfo)Room_user_vc.elementAt(i);
				u.send_Message(str);
			}
		}
		private void Add_User(UserInfo u) {
			this.Room_user_vc.add(u);
		}
		
	}	//Roominfo ��
	
	
}


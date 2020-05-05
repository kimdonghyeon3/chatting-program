package 채팅프로그램1;

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
	//ctrl + shift + o는 자동 import생성
	
	private JPanel contentPane;
	private JTextField port_tf;
	private JTextArea textArea = new JTextArea();
	private JButton start_button = new JButton("서버 실행");
	private JButton stop_button = new JButton("서버 중지");
	
	//Network 자원
	
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector user_vc = new Vector();
	private Vector room_vc = new Vector();
	
	private StringTokenizer st;

	
	Server(){	//생성자
		init(); //화면 생성 메소드
		start();
	}
	
	private void start() {
		start_button.addActionListener(this);
		stop_button.addActionListener(this);
		
	}
	
	private void init() {	//화면구성
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 366);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("포트 번호");
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
		
		this.setVisible(true); //true 경우 화면에 보이게 false = 화면에 보이지 않게
	}

	private void Server_start() {
		try {
			server_socket = new ServerSocket(port);	//12345포트 사용
		} catch (IOException e) {
			JOptionPane.showMessageDialog
			(null, "이미 사용중인 포트","알림",JOptionPane.ERROR_MESSAGE);
		}
		
		if(server_socket !=null) {	//정상적으로 포트가 열렸을경우
			Connection();
			
		}
		
	}
	
	private void Connection() {
		//1가지의 스레드에서는 1가지의 일만 처리할 수 있따.
		
		Thread th = new Thread(new Runnable(){
			
			@Override
			public void run() {//스레드에서 처리할 일을 기재한다.
				
				while(true) {
				try {
					textArea.append("사용자 접속 대기중\n");
					socket=server_socket.accept();//사용자 접속 대기 무한대기
					textArea.append("사용자 접속!!!\n");
					
					UserInfo user=new UserInfo(socket);
					
					user.start();//객체의 각각의 스레드를 실행시킨다.
					
				} catch (IOException e) {
					
					break;
				}
				}//while문 끝
				
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
			System.out.println("스타트 버튼 클릭");
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			Server_start();	//소켓 생성 및 사용자 접속대기
			
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
			System.out.println("스탑 버튼 클릭");
		}
		
	} // 엑션 이벤트 끝
	
	class UserInfo extends Thread{
		
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private Socket user_socket;
		private String Nickname="";
		
		private boolean RoomCh = true;
		
		UserInfo(Socket soc){ //생성자 메소드
			this.user_socket = soc;
			UserNetwork();
			
		}
		
		private void UserNetwork() { //네트워크 자원 설정
			
			try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			
			os=user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			Nickname = dis.readUTF();	//사용자의 닉네임을 받는다
			textArea.append(Nickname + " : 사용자 접속\n");
			
			//기존 사용자에게 새로운 사용자 알림
			System.out.println("현재 접속된 사용자 수 : " + user_vc.size());
			
			BroadCast("NewUser/"+Nickname);	//기존 사용자에게 자신을 알린다.
			
			//자신에게 기존 사용자를 받아오는 부분
			for(int i = 0; i<user_vc.size(); i++) {
				
				UserInfo u =(UserInfo)user_vc.elementAt(i);
				
				send_Message("OldUser/"+ u.Nickname);
			}
			
			//자신에게 기존 방을 받아오는 부분
			for(int i = 0; i<room_vc.size(); i++) {
				Roominfo r =(Roominfo)room_vc.elementAt(i);
				send_Message("OldRoom/"+ r.Room_name);
			}
			
			//send_Message("room_list_update/");
			
			user_vc.add(this); //사용자에게 알린 후 Vector에 자신을 추가
			
			//BroadCast("user_list_updata/");	//user_list_update 오류로 이거 쓰면 안됨
			
			}
			catch(IOException e) {JOptionPane.showMessageDialog
				(null, "Stream설정 에러","알림",JOptionPane.ERROR_MESSAGE);
			}
			}
		
		public void run() { //Thread로 처리할 내용
			while(true) {
				
				try {
					String msg = dis.readUTF();
					textArea.append(Nickname + " : 사용자로부터 들어온 메시지 : "+msg + "\n");
					InMessage(msg);
				} catch (IOException e) {
					textArea.append(Nickname + "사용자 접속 끊어짐\n");
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
			} //while문 끝
			
		} // RUN메소드 끝
		
		private void InMessage(String str) { //클라이언트로 부터 들어오는 메세지 처리
			st= new StringTokenizer(str,"/");
			
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			System.out.println("프로토콜 : " + protocol);
			System.out.println("메세지 : " + message);
			
			if(protocol.contentEquals("Note")) {
				
				String note = st.nextToken();
				
				System.out.println("받는 사람 : " + message + "\n보내는 메시지 : " + note);
			
			//벡터에서 해당 사용자를 찾아서 메시지 전송
				
				for(int i =0;i<user_vc.size();i++) {
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					if(u.Nickname.contentEquals(message)) {
						u.send_Message("Note/"+Nickname+"/"+note);
						//Note/User1/~~~
					}
				}
			
			} //if문 끝
			else if(protocol.contentEquals("CreateRoom")) {
				for(int i =0;i<room_vc.size();i++) {	//같은 방이 존재하는지 확인
					Roominfo r = (Roominfo)room_vc.elementAt(i);
					if(r.Room_name.contentEquals(message)) {	//만들고자 하는 방이 이미 존재할 때
						send_Message("CreateRoomFail/ok");
						RoomCh = false;
					break;
					}
				}//for문 끝
			if(RoomCh) {
				Roominfo new_room = new Roominfo(message, this);	//방을 만들 수 있을 때(같은방이 없을 때)
				room_vc.add(new_room);	//천체 방 벡터에 방을 추가
				
				send_Message("CreateRoom/"+message);
				
				BroadCast("New_Room/" + message);
			}
			
			RoomCh=true;
			}	//else if문 끝
			
			else if(protocol.contentEquals("Chatting")) {
				String msg = st.nextToken();
				
				for(int i =0; i< room_vc.size(); i++) {
					Roominfo r = (Roominfo)room_vc.elementAt(i);
							
				if(r.Room_name.contentEquals(message)) {
					r.BroadCast_Room("Chatting/"+Nickname+"/"+msg);
				}
				}
			}	//else if문 끝
			else if(protocol.contentEquals("JoinRoom")) {
				for(int i =0; i<room_vc.size();i++) {
					Roominfo r = (Roominfo)room_vc.elementAt(i);
					if(r.Room_name.contentEquals(message)) {
						
						//새로운 사용자를 알린다.
						r.BroadCast_Room("Chatting/알림/****"+Nickname+"님이 입장하셨습니다****");
						
						//사용자 추가
						r.Add_User(this);
						send_Message("JoinRoom/"+message);
					}
				}
			}
			
		}
		
		private void BroadCast(String str) {	//전체 사용자에게 메세지 보내는 부분
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
		
	} // Userinfo class 끝
	
	class Roominfo{
		private String Room_name;
		private Vector Room_user_vc = new Vector();
		
		Roominfo(String str, UserInfo u){
			this.Room_name = str;
			this.Room_user_vc.add(u);
			
		}
		public void BroadCast_Room(String str){	//현재 방의 모든 사람에게 알린다.
			for(int i = 0; i<Room_user_vc.size();i++) {
				UserInfo u = (UserInfo)Room_user_vc.elementAt(i);
				u.send_Message(str);
			}
		}
		private void Add_User(UserInfo u) {
			this.Room_user_vc.add(u);
		}
		
	}	//Roominfo 끝
	
	
}


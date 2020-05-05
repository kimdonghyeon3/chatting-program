package 채팅클라이언트;

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

	//Login GUI 변수
	private JFrame Login_GUI = new JFrame();
	private JPanel login_Pane;
	private JTextField ip_ft;	//ip 받는 텍스트 필드
	private JTextField port_ft;	//port 받는 텍스트 필드
	private JTextField id_ft;	//id 받는 텍스트 필드
	private JButton login_button = new JButton("접 속 버 튼");	//접속버튼
	
	//main GUI 변수
	private JPanel contentPane;
	private JTextField message_tf;
	private JButton notesend_button = new JButton("쪽 지 보 내 기");
	private JButton joinroom_button = new JButton("채 팅 방 참 여");
	private JButton createroom_button = new JButton("방 만 들 기");
	private JButton send_button = new JButton("전 송");
	
	
	private JList User_list = new JList();	//전체 접속자 list
	private JList Room_list = new JList();	//전체 방 목록 list
	
	private JTextArea Chat_area = new JTextArea();	//채팅창 변수
	
	//네트워크를 위한 자원 변수
	
	private Socket socket;
	private String ip;	//1번은 자기 자신
	private int port;
	private String id="";
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	//그외 변수 들
	Vector user_list = new Vector();
	Vector room_list = new Vector();
	StringTokenizer st;
	
	private String My_Room;	//내가 있는 방이름
	
	Client(){
		
		Login_init();	//login창 화면 구성 메소드
		Main_init();	//main창 화면 구성 메소드
		start();
		
	}
	
	private void start() {
		login_button.addActionListener(this);		//로그인 버튼 리스터
		notesend_button.addActionListener(this);	//쪽지보내기 버튼 리스터
		joinroom_button.addActionListener(this);	//채팅방 참여 버튼 리스터
		createroom_button.addActionListener(this);	//채팅방 만들기 버튼 리스터
		send_button.addActionListener(this);		//채팅 전송 버튼 리스터
		message_tf.addKeyListener(this);
	}
	
	private void Main_init() { //client 화면구성
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 586, 525);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("전 체 접 속 자");
		lblNewLabel.setBounds(12, 10, 96, 21);
		contentPane.add(lblNewLabel);
		

		User_list.setBounds(12, 29, 104, 163);
		contentPane.add(User_list);
		//User_list.setListData(user_list);

		notesend_button.setBounds(12, 202, 104, 23);
		contentPane.add(notesend_button);
		
		JLabel lblNewLabel_1 = new JLabel("채 팅 방 목 록");
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
		
		this.setVisible(false);	//true경우 화면 보이게, false경우 화면 안보이게
		
	}
	
	private void Login_init() { //login 화면구성
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
		
		Login_GUI.setVisible(true); //true경우 화면에 보인다, false경우 화면에 보이지 않는다.
	}
	
	private void Network() {
		try {
			socket = new Socket(ip,port);
			
			if(socket !=null) {	//정상적으로 소켓이 연결되었을 경우
				Connection();
				
			}
			
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog
			(null, "연결 실패","알림",JOptionPane.ERROR_MESSAGE);//error메시지가 뜨는데 시간이걸린다 컴퓨터가 ip를 찾는 시간이 있기 때문이다.
		} catch (IOException e) {
			JOptionPane.showMessageDialog
			(null, "연결 실패","알림",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private void Connection() {
		
		try {
		is = socket.getInputStream();
		dis = new DataInputStream(is);
		
		os= socket.getOutputStream();
		dos = new DataOutputStream(os);
		}
		catch(IOException e)//에러 처리부분
		{
			JOptionPane.showMessageDialog
			(null, "연결 실패","알림",JOptionPane.ERROR_MESSAGE);
		}//Stream 설정 끝
		
		this.setVisible(true);	//main ui 표시
		this.Login_GUI.setVisible(false);	//Login gui 창 닫기
		
		//처음 접속시에 ID전송
		send_message(id);
		
		//user_list에 사용자 추가
		user_list.add(id);
		User_list.setListData(user_list);	//user_list_update 오류로 이거 써야됨
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					try {
					String msg=dis.readUTF(); // 메시지 수신
					
					System.out.println("서버로부터 수신된 메세지 : "+msg);
					
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
					(null, "서버와 접속 끊어짐","알림",JOptionPane.ERROR_MESSAGE);
						}catch(IOException e1){}
					break;
					}
					}
				
			}
			
		});
		
	th.start();
	
	}
	
	private void inmessage(String str) {	//서버로부터 들어오는 모든 메세지
		
		st = new StringTokenizer(str,"/");
		
		String protocol = st.nextToken();
		String Message = st.nextToken();
		
		System.out.println("프로토콜 : " + protocol);
		System.out.println("내용 : " + Message);
		
		
		if(protocol.contentEquals("NewUser")) { //새로운 접속자를 창에 표시
			user_list.add(Message);
			User_list.setListData(user_list);	//user_list_update 오류로 이거 써야됨

		}
		else if(protocol.contentEquals("OldUser")) {
			user_list.add(Message);
			User_list.setListData(user_list);	//user_list_update 오류로 이거 써야됨
		}
		else if(protocol.contentEquals("Note")) {

			String note = st.nextToken();
			
			System.out.println(Message = "사용자로부터 온 쪽지 : " + note);
			
			JOptionPane.showMessageDialog
			(null, note,Message +"님으로 부터 쪽지",JOptionPane.CLOSED_OPTION);
		}
		/*else if(protocol.contentEquals("user_list_update")) {
			User_list.setListData(user_list);
		}*/ //요거 왜안되는지 모르겠다.
		else if(protocol.contentEquals("CreateRoom")) {	//방을 만들었을 때
			joinroom_button.setEnabled(false);
			createroom_button.setEnabled(false);
			message_tf.setEnabled(true);
			send_button.setEnabled(true);
			My_Room = Message;
		}
		else if(protocol.contentEquals("CreateRoomFail")) {	//방을 못만들었을 때
			JOptionPane.showMessageDialog
			(null, "방 만들기 실패","알림",JOptionPane.ERROR_MESSAGE);
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
			(null, "채팅방에 입장했습니다.","알림",JOptionPane.INFORMATION_MESSAGE);
		}
		else if(protocol.contentEquals("User_out")){
				user_list.remove(Message);
				User_list.setListData(user_list);
			}
		
	}
	
	private void send_message(String str) {	//서버에게 메세지를 보내는 부분
		
		try {
			dos.writeUTF(str);	//사용자가 서버한테 메시지를 보내는 부분
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//login_button - 로그인 버튼
		if(e.getSource()==login_button) {
			System.out.println("로그인 버튼 클릭");
			
			if(ip_ft.getText().length()==0) {
				ip_ft.setText("IP를 입력해주세요");
				ip_ft.requestFocus();
			}
			else if(port_ft.getText().length()==0) {
				port_ft.setText("PORT를 입력해주세요");
				port_ft.requestFocus();
			}
			else if(id_ft.getText().length()==0) {
				id_ft.setText("ID를 입력해주세요");
				id_ft.requestFocus();
			}
			else {
			ip=ip_ft.getText().trim();	//ip를 받아오는 부분 //trim은 공백을 없애주는 메소드
			port = Integer.parseInt(port_ft.getText().trim());
			
			id=id_ft.getText().trim();	//id를 받아오는 부분
			Network(); 
			}
			
			
		}
		else if(e.getSource()==notesend_button) {
			System.out.println("쪽지 보내기 버튼 클릭");
			String user = (String)User_list.getSelectedValue();	//리스트 항목에 있는 값을 String으로 반환
			
			String note = JOptionPane.showInputDialog("보낼메세지"); //메시지 보내는 코드 gui기본 제공 코드임
			
			if(note != null) {
				send_message("Note/"+user+"/"+note);
				//ex) Note/User2/나는 천재야
			}
			System.out.println("받는사람 : " + user + "| 보낼 내용 : " + note);
		}
		else if(e.getSource()==joinroom_button) {
			
			String JoinRoom =(String)Room_list.getSelectedValue();
			send_message("JoinRoom/"+JoinRoom);
			
			System.out.println("채팅방 참여 버튼 클릭");
		}
		else if(e.getSource()==createroom_button) {
			String roomname = JOptionPane.showInputDialog("방 이름");
			if(roomname !=null) {
				send_message("CreateRoom/"+roomname);
				
			}
			System.out.println("채팅 방만들기 버튼 클릭");
		}
		else if(e.getSource()==send_button) {
			
			send_message("Chatting/" + My_Room+"/" + message_tf.getText().trim());
			message_tf.setText("");
			message_tf.requestFocus();
			
			System.out.println("채팅 전송 버튼 클릭");
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


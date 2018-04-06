import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Locale;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;

// Made by Noah Bosman

public class Board extends JPanel implements ActionListener {


	private Dimension d;
	private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

	private Image ii;
	private final Color dotColor = new Color(192, 192, 0);
	private Color mazeColor;

	private boolean inGame = false;
	private boolean dying = false;

	private final int BLOCK_SIZE = 24;
	private final int N_BLOCKS = 15;
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
	private final int PAC_ANIM_DELAY = 2;
	private final int PACMAN_ANIM_COUNT = 4;
	private final int MAX_GHOSTS = 3;
	private final int PACMAN_SPEED = 6;

	private int pacAnimCount = PAC_ANIM_DELAY;
	private int pacAnimDir = 1;
	private int pacmanAnimPos = 0;
	private int N_GHOSTS = 3;
	private int pacsLeft, score;
	private int[] dx, dy;
	private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

	private Image ghost;
	private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
	private Image pacman3up, pacman3down, pacman3left, pacman3right;
	private Image pacman4up, pacman4down, pacman4left, pacman4right;

	private int pacman_x, pacman_y, pacmand_x, pacmand_y;
	private int req_dx, req_dy, view_dx, view_dy;

	private final short levelData[] = {
			19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
			21, 0, 0, 0, 17, 16, 24, 24, 24, 24, 24, 16, 16, 16, 20,
			21, 0, 0, 0, 17, 20, 0, 0, 0, 0, 0, 17, 16, 16, 20,
			21, 0, 0, 0, 17, 16, 18, 26, 22, 0, 19, 16, 16, 16, 20,
			17, 18, 18, 18, 16, 16, 20, 0, 21, 0, 17, 16, 16, 16, 20,
			17, 16, 16, 16, 16, 16, 20, 0, 21, 0, 17, 16, 16, 24, 20,
			25, 16, 16, 16, 24, 24, 28, 0, 25, 26, 24, 16, 20, 0, 21,
			1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
			1, 17, 16, 16, 18, 18, 22, 0, 19, 26, 26, 24, 20, 0, 21,
			1, 17, 16, 16, 16, 16, 20, 0, 21, 0, 0, 0, 21, 0, 21,
			1, 17, 16, 16, 16, 16, 20, 0, 21, 0, 23, 0, 21, 0, 21,
			1, 17, 16, 16, 16, 24, 24, 26, 28, 0, 25, 26, 20, 0, 21,
			1, 17, 16, 16, 20, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21,
			1, 25, 24, 24, 24, 26, 26, 26, 26, 26, 18, 18, 16, 18, 20,
			9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
	};

	private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
	private final int maxSpeed = 2;

	private int currentSpeed = 3;
	private short[] screenData;
	private Timer timer;

	public Board() {

		loadImages();
		initVariables();
		initBoard();
	}

	private void initBoard() {

		addKeyListener(new TAdapter());

		setFocusable(true);

		setBackground(Color.black);
		setDoubleBuffered(true);        
	}

	private void initVariables() {

		screenData = new short[N_BLOCKS * N_BLOCKS];
		mazeColor = new Color(5, 100, 5);
		d = new Dimension(400, 400);
		ghost_x = new int[MAX_GHOSTS];
		ghost_dx = new int[MAX_GHOSTS];
		ghost_y = new int[MAX_GHOSTS];
		ghost_dy = new int[MAX_GHOSTS];
		ghostSpeed = new int[MAX_GHOSTS];
		dx = new int[4];
		dy = new int[4];

		timer = new Timer(40, this);
		timer.start();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		initGame();
	}

	private void doAnim() {

		pacAnimCount--;

		if (pacAnimCount <= 0) {
			pacAnimCount = PAC_ANIM_DELAY;
			pacmanAnimPos = pacmanAnimPos + pacAnimDir;

			if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
				pacAnimDir = -pacAnimDir;
			}
		}
	}

	private void playGame(Graphics2D g2d) {

		if (dying) {

			death();

		} else {

			movePacman();
			drawPacman(g2d);
			moveGhosts(g2d);
			checkMaze();
		}
	}

	private void showIntroScreen(Graphics2D g2d) {

		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

		String s = "Press s to start.";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = this.getFontMetrics(small);

		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
	}
	

//	private void drawScore(Graphics2D g) {
//
//		int i;
//		String s;
//
//		g.setFont(smallFont);
//		g.setColor(new Color(96, 128, 255));
//		s = "Score: " + score;
//		g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
//
//		for (i = 0; i < pacsLeft; i++) {
//			g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
//		}
//	}
	
	public void drawScore(Graphics2D g) {

		int i;
		String s;

		g.setFont(smallFont);
		g.setColor(new Color(96, 128, 255));
		s = "Score: " + score;
		g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

		for (i = 0; i < pacsLeft; i++) {
			g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
			
			s.
		}
	}
	
	public void Score() {


		class Score extends JFrame {
		  public void main( String args[] ) {
		    JFrame frame = new Score();
		    frame.setContentPane( new HighScorePaneel() );
		  }
		}


		class HighScorePaneel extends JPanel {
			//private Date datum=new Date();

			//	SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

			//	System.out.println(date);

			private  final String BESTANDSNAAM=new String("HighScore.txt");
			private ArrayList<String> highScoreLijst=new ArrayList<String>(); 
			private String hoogsteScore="";//,uitvoerRegel="";
			private JLabel scoreLabel = new JLabel("score");
			private JLabel naamLabel = new JLabel("naam");
			private JLabel datumLabel = new JLabel("datum");
			//	private int posx=30,posy=20;
			private JTextField uitvoerVak = new JTextField();

			JButton vernieuwKnop = new JButton("Voeg Highscore toe");
			//private  StringBuffer hulpRegel=new StringBuffer("");
			//private  StringBuffer kopRegel=new StringBuffer("    Naam	   	        aantal PUNTEN		                datum");
			//private String hoogsteScoreNaam=new String("");
			//	private int hoogsteScorePunten=0;
			//private String hoogsteScoreDatum=new String("");
			private JTextField naamVak = new JTextField();
			private JTextField scoreVak = new JTextField();
			private JTextField datumVak = new JTextField();
			private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			private Date date = new Date();



			public HighScorePaneel() {
				setLayout(null);
				naamLabel.setBounds(60, 50, 150, 20);
				naamVak.setBounds(50,70,200,20);
				scoreLabel.setBounds(310, 50, 150, 20);
				scoreVak.setBounds(300,70,200,20);
				datumLabel.setBounds(610, 50, 150, 20);
				datumVak.setBounds(600,70,200,20);

				vernieuwKnop.setBounds(300,120,200,20);
				vernieuwKnop.addActionListener(new VernieuwKnopHandler());
				uitvoerVak.setBounds(50,80,600,20);
				//	uitvoerVak.setText(kopRegel.toString());


				//	System.out.println(dateFormat.format(date));
				//date.setDate(date);
				datumVak.setText(dateFormat.format(date));
				datumVak.setEditable(false);

				add(naamLabel);
				add(scoreLabel);
				add(datumLabel);
				add(naamVak);
				add(scoreVak);
				add(datumVak);
				//	add(scoreLabel);
				//	add(uitvoerVak);
				add(vernieuwKnop);
				setVisible(true);

			}


			public String haalHoogsteScoreOp(String bestandsNaam){
				boolean invoerOk=false;
				//String hoogsteScoreord=""; 
				int index,aantalWoorden=0;

				BufferedReader in;
				String regel;
				try {  
					in = new BufferedReader( new FileReader( bestandsNaam ) );
					while ( ( regel = in.readLine() ) != null ) {
						//System.out.println( regel );
						try {
							highScoreLijst.add(regel);
							aantalWoorden++;
						}
						catch (Exception e){
							e.printStackTrace();	   	   
						}
					}
					in.close();
					invoerOk=true;
				}
				catch( FileNotFoundException e ) {

					JOptionPane.showMessageDialog( HighScorePaneel.this,
							e.getMessage(),
							"FoutMelding Leesfout !!",
							JOptionPane.ERROR_MESSAGE
							);
					System.out.println( "Kan het bestand niet vinden" );
					invoerOk=false;
				}
				catch( IOException e ) {
					System.out.println( "Fout bij het lezen of sluiten bestand" );
					e.printStackTrace();
					invoerOk=false;
				}
				catch( Exception e ) {
					System.out.println( "Er is iest goed Fout" );
					e.printStackTrace();
					invoerOk=false;
				}
				if (invoerOk){
					index=(int) ( Math.random()*(aantalWoorden-1));
					//System.out.println(""+index+"   aantalwoorden= "+aantalWoorden);
					hoogsteScore= (String)highScoreLijst.get(0); //index);
				} else{
					hoogsteScore="niet gevonden";
				}		
				return (hoogsteScore);  
			}




			public ArrayList<String> LeesScoreBestand(String bestandsNaam){

				ArrayList<String> scoreLijst=new ArrayList<String>();

				int aantalRegels=0;

				BufferedReader in;
				String regel;
				try {  
					in = new BufferedReader( new FileReader( bestandsNaam ) );
					while ( ( regel = in.readLine() ) != null ) {
						//System.out.println( regel );
						try {
							scoreLijst.add(regel);
							aantalRegels++;
						}
						catch (Exception e){
							e.printStackTrace();	   	   
						}
					}
					in.close();
				}
				catch( FileNotFoundException e ) {			
					JOptionPane.showMessageDialog( HighScorePaneel.this,
							e.getMessage(),
							"FoutMelding Leesfout !!",
							JOptionPane.ERROR_MESSAGE
							);
					System.out.println( "Kan het bestand niet vinden" );

				}
				catch( IOException e ) {
					System.out.println( "Fout bij het lezen of sluiten bestand" );
					e.printStackTrace();

				}
				catch( Exception e ) {
					System.out.println( "Er is iest goed Fout" );
					e.printStackTrace();

				}

				return (scoreLijst);  
			}

			public ArrayList<String> voegScoreToe (ArrayList<String> oudeLijst, String nieuweNaam, String nieuweScorePunten, String nieuweDatum){
				ArrayList<String> nieuweLijst= new ArrayList<String>();

				String oudeDatum="niet bekend";
				int oudeScorePunten=0;
				String oudeNaam="";
				String oudeRegel=new String("");
				String nieuweRegel= new String();
				boolean ingevuld=false;
				int aantalRegels=0;

				int invoerScoreGetal=0;

				try {
					invoerScoreGetal=Integer.parseInt(nieuweScorePunten);
				} catch (NumberFormatException nfe){
					JOptionPane.showMessageDialog( HighScorePaneel.this,
							nfe.getMessage(),
							"verkeerde invoer !!",
							JOptionPane.ERROR_MESSAGE
							);
					System.out.println(""+nfe);    	
				}



				for (Object regel:oudeLijst){
					aantalRegels++;
					String match = ",";
					String[] veld = regel.toString().split(match);
					int aantalVelden=veld.length;

					if (aantalVelden==3){
						oudeDatum=veld[2];
						oudeScorePunten=Integer.parseInt(veld[1]);
						oudeNaam=veld[0];	
					} else if (aantalVelden==2){
						oudeDatum="niet bekend";
						oudeScorePunten=Integer.parseInt(veld[1]);
						oudeNaam=veld[0];	
					} else{
						oudeDatum="niet bekend";
						oudeScorePunten=0;
						oudeNaam=veld[0];				
					}
					nieuweRegel=(nieuweNaam+","+nieuweScorePunten+","+nieuweDatum);
					oudeRegel=(oudeNaam+","+oudeScorePunten+","+oudeDatum);
					//System.out.println("invoer:  "+invoerScoreGetal+"    oude score "+oudeScorePunten+"  "+ingevuld);

					if ((invoerScoreGetal > oudeScorePunten)&&!(ingevuld)){
						if(aantalRegels<20){

							nieuweLijst.add(nieuweRegel);
							nieuweLijst.add(regel.toString());
							ingevuld=true;

						}
					} else{
						if(aantalRegels<20){
							nieuweLijst.add(regel.toString());
						}
					}

				}

				if ((!ingevuld)&&(aantalRegels<20)){
					ingevuld=true;
					nieuweLijst.add(nieuweRegel);

				}
				return nieuweLijst;
			}


			public void schrijfScoreBestand(String bestandsNaam, ArrayList<String> highScoreLijst){

				try {  

					BufferedWriter uit;
					uit = new BufferedWriter( new FileWriter( bestandsNaam ) );
					for (Object regel:highScoreLijst){
						uit.write(regel.toString());
						uit.newLine();
					}
					uit.close();
				}
				catch( IOException e ) {
					System.out.println( "Fout bij het lezen of sluiten bestand" );
					e.printStackTrace();
				}
				catch( Exception e ) {
					System.out.println( "Er is iest goed Fout" );
					e.printStackTrace();
				} 
			}




			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				g.setFont( new Font( "Courier", Font.BOLD, 16 ) );
				g.setColor(Color.RED);

				highScoreLijst=LeesScoreBestand(BESTANDSNAAM);

				int posx=10;
				int posy=180;
				String kopRegel=new String(String.format("%25s %15s %30s", "NAAM","PUNTEN" ,"DATUM"));			
				String hulpRegel=new String("");			
				String naam;
				String punten;
				String datum;
				g.drawString(kopRegel.toString(),posx,posy);
				posy+=20;
				datumVak.setText(dateFormat.format(new Date()));


				for (Object regel:highScoreLijst){
					posy+=20;

					String match = ",";
					String[] veld = regel.toString().split(match);
					int aantalVelden=veld.length;



					if (aantalVelden==3){
						//	System.out.println("   "+aantalVelden+"   Voegtoe aantal punten    "+ nieuweScorePunten);

						datum=veld[2];
						punten=veld[1];
						naam=veld[0];	


					} else if (aantalVelden==2){
						datum="niet bekend";
						punten=veld[1];
						naam=veld[0];	
					} else{
						datum="niet bekend";
						punten="000";
						naam=veld[0];				
					}

					hulpRegel=String.format("%25s %15s %30s", naam,punten,datum); 
					g.drawString(hulpRegel.toString(), posx,posy);
				}	
			}



			class VernieuwKnopHandler implements ActionListener  {
				public void actionPerformed( ActionEvent e ) {
					ArrayList<String> scoreLijst=LeesScoreBestand(BESTANDSNAAM);
					ArrayList<String> nieuwsteScoreLijst=LeesScoreBestand(BESTANDSNAAM);

					String naam=new String("");
					String score=new String("");
					String datum=new String("");
					try {
						naam=naamVak.getText();
						score=scoreVak.getText();
						datum=datumVak.getText();
					} catch( Exception e1 ) {
						System.out.println( "Er is iest goed Fout" );
						e1.printStackTrace();
					}
					nieuwsteScoreLijst=voegScoreToe(scoreLijst, naam, score, datum);
					schrijfScoreBestand(BESTANDSNAAM,nieuwsteScoreLijst);

					repaint();
				}
			}


		}




	}


	private void checkMaze() {

		short i = 0;
		boolean finished = true;

		while (i < N_BLOCKS * N_BLOCKS && finished) {

			if ((screenData[i] & 48) != 0) {
				finished = false;
			}

			i++;
		}

		if (finished) {

			score += 50;

			if (N_GHOSTS < MAX_GHOSTS) {
				N_GHOSTS++;
			}

			if (currentSpeed < maxSpeed) {
				currentSpeed++;
			}

			initLevel();
		}
	}

	private void death() {

		pacsLeft--;

		if (pacsLeft == 0) {
			inGame = false;
		}

		continueLevel();
	}

	private void moveGhosts(Graphics2D g2d) {

		short i;
		int pos;
		int count;

		for (i = 0; i < N_GHOSTS; i++) {
			if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
				pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

				count = 0;

				if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}

				if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}

				if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}

				if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}

				if (count == 0) {

					if ((screenData[pos] & 15) == 15) {
						ghost_dx[i] = 0;
						ghost_dy[i] = 0;
					} else {
						ghost_dx[i] = -ghost_dx[i];
						ghost_dy[i] = -ghost_dy[i];
					}

				} else {

					count = (int) (Math.random() * count);

					if (count > 3) {
						count = 3;
					}

					ghost_dx[i] = dx[count];
					ghost_dy[i] = dy[count];
				}

			}

			ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
			ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
			drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

			if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
					&& pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
					&& inGame) {

				dying = true;
			}
		}
	}

	private void drawGhost(Graphics2D g2d, int x, int y) {

		g2d.drawImage(ghost, x, y, this);
	}

	private void movePacman() {

		int pos;
		short ch;

		if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
			pacmand_x = req_dx;
			pacmand_y = req_dy;
			view_dx = pacmand_x;
			view_dy = pacmand_y;
		}

		if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
			pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
			ch = screenData[pos];

			if ((ch & 16) != 0) {
				screenData[pos] = (short) (ch & 15);
				score++;
			}

			if (req_dx != 0 || req_dy != 0) {
				if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
						|| (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
						|| (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
						|| (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
					pacmand_x = req_dx;
					pacmand_y = req_dy;
					view_dx = pacmand_x;
					view_dy = pacmand_y;
				}
			}

			// Check for standstill
			if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
					|| (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
					|| (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
					|| (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
				pacmand_x = 0;
				pacmand_y = 0;
			}
		}
		pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
		pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
	}

	private void drawPacman(Graphics2D g2d) {

		if (view_dx == -1) {
			drawPacnanLeft(g2d);
		} else if (view_dx == 1) {
			drawPacmanRight(g2d);
		} else if (view_dy == -1) {
			drawPacmanUp(g2d);
		} else {
			drawPacmanDown(g2d);
		}
	}

	private void drawPacmanUp(Graphics2D g2d) {

		switch (pacmanAnimPos) {
		case 1:
			g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
			break;
		}
	}

	private void drawPacmanDown(Graphics2D g2d) {

		switch (pacmanAnimPos) {
		case 1:
			g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
			break;
		}
	}

	private void drawPacnanLeft(Graphics2D g2d) {

		switch (pacmanAnimPos) {
		case 1:
			g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
			break;
		}
	}

	private void drawPacmanRight(Graphics2D g2d) {

		switch (pacmanAnimPos) {
		case 1:
			g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
			break;
		}
	}

	private void drawMaze(Graphics2D g2d) {

		short i = 0;
		int x, y;

		for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
			for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

				g2d.setColor(mazeColor);
				g2d.setStroke(new BasicStroke(2));

				if ((screenData[i] & 1) != 0) { 
					g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 2) != 0) { 
					g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
				}

				if ((screenData[i] & 4) != 0) { 
					g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
							y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 8) != 0) { 
					g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
							y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 16) != 0) { 
					g2d.setColor(dotColor);
					g2d.fillOval(x + 11, y + 11, 7, 7);
				}

				i++;
			}
		}
	}

	private void initGame() {

		pacsLeft = 3;
		score = 0;
		initLevel();
		N_GHOSTS = 3;
		currentSpeed = 3;
	}

	private void initLevel() {

		int i;
		for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
			screenData[i] = levelData[i];
		}

		continueLevel();
	}

	private void continueLevel() {

		short i;
		int dx = 1;
		int random;

		for (i = 0; i < N_GHOSTS; i++) {

			ghost_y[i] = 4 * BLOCK_SIZE;
			ghost_x[i] = 4 * BLOCK_SIZE;
			ghost_dy[i] = 0;
			ghost_dx[i] = dx;
			dx = -dx;
			random = (int) (Math.random() * (currentSpeed + 1));

			if (random > currentSpeed) {
				random = currentSpeed;
			}

			ghostSpeed[i] = validSpeeds[random];
		}

		pacman_x = 7 * BLOCK_SIZE;
		pacman_y = 11 * BLOCK_SIZE;
		pacmand_x = 0;
		pacmand_y = 0;
		req_dx = 0;
		req_dy = 0;
		view_dx = -1;
		view_dy = 0;
		dying = false;
	}

	private void loadImages() {

		ghost = new ImageIcon("pacpix/ghost.gif").getImage();
		pacman1 = new ImageIcon("pacpix//pacman1.gif").getImage();
		pacman2up = new ImageIcon("pacpix//pacman2up.gif").getImage();
		pacman3up = new ImageIcon("pacpix//pacman3up.gif").getImage();
		pacman4up = new ImageIcon("pacpix//pacman4up.gif").getImage();
		pacman2down = new ImageIcon("pacpix//pacman2down.gif").getImage();
		pacman3down = new ImageIcon("pacpix//pacman3down.gif").getImage();
		pacman4down = new ImageIcon("pacpix//pacman4down.gif").getImage();
		pacman2left = new ImageIcon("pacpix//pacman2left.gif").getImage();
		pacman3left = new ImageIcon("pacpix//pacman3left.gif").getImage();
		pacman4left = new ImageIcon("pacpix//pacman4left.gif").getImage();
		pacman2right = new ImageIcon("pacpix//pacman2right.gif").getImage();
		pacman3right = new ImageIcon("pacpix//pacman3right.gif").getImage();
		pacman4right = new ImageIcon("pacpix//pacman4right.gif").getImage();

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);
	}

	private void doDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);

		drawMaze(g2d);
//		drawScore(g2d);
		doAnim();

		if (inGame) {
			playGame(g2d);
		} else {
			showIntroScreen(g2d);
		}

		g2d.drawImage(ii, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}

	class TAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();

			if (inGame) {
				if (key == KeyEvent.VK_LEFT) {
					req_dx = -1;
					req_dy = 0;
				} else if (key == KeyEvent.VK_RIGHT) {
					req_dx = 1;
					req_dy = 0;
				} else if (key == KeyEvent.VK_UP) {
					req_dx = 0;
					req_dy = -1;
				} else if (key == KeyEvent.VK_DOWN) {
					req_dx = 0;
					req_dy = 1;
				} else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
					inGame = false;
				} else if (key == KeyEvent.VK_PAUSE) {
					if (timer.isRunning()) {
						timer.stop();
					} else {
						timer.start();
					}
				}
			} else {
				if (key == 's' || key == 'S') {
					inGame = true;
					initGame();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == Event.LEFT || key == Event.RIGHT
					|| key == Event.UP || key == Event.DOWN) {
				req_dx = 0;
				req_dy = 0;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		repaint();
	}
}
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import HighScorePaneel.VernieuwKnopHandler;

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
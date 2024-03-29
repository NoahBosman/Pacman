import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

//Made by Noah Bosman

public class Pacman extends JFrame {

	public Pacman() {

		initUI();
	}

	private void initUI() {

		add(new Board());

		setTitle("Pacman");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(380, 420);
		setLocationRelativeTo(null);
		setVisible(true);        
		
		
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(() -> {
			Pacman ex = new Pacman();
			ex.setVisible(true);
		});
	}
}


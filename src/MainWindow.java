import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{176, 81, 0};
		gridBagLayout.rowHeights = new int[]{119, 23, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JButton btnTileMaker = new JButton("Tile Maker");
		btnTileMaker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jTileMaker jtl = new jTileMaker();
				jtl.setVisible(true);
			}
		});
		GridBagConstraints gbc_btnTileMaker = new GridBagConstraints();
		gbc_btnTileMaker.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTileMaker.insets = new Insets(0, 0, 5, 0);
		gbc_btnTileMaker.gridx = 1;
		gbc_btnTileMaker.gridy = 1;
		frame.getContentPane().add(btnTileMaker, gbc_btnTileMaker);
		
		JButton btnMapMaker = new JButton("Map Maker");
		GridBagConstraints gbc_btnMapMaker = new GridBagConstraints();
		gbc_btnMapMaker.insets = new Insets(0, 0, 5, 0);
		gbc_btnMapMaker.gridx = 1;
		gbc_btnMapMaker.gridy = 2;
		frame.getContentPane().add(btnMapMaker, gbc_btnMapMaker);
		
		JButton btnPlay = new JButton("Play !");
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPlay.gridx = 1;
		gbc_btnPlay.gridy = 3;
		frame.getContentPane().add(btnPlay, gbc_btnPlay);
	}

}

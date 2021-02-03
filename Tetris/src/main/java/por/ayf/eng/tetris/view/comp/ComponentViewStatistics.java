package por.ayf.eng.tetris.view.comp;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.border.TitledBorder;

import por.ayf.eng.tetris.game.ControlScore;
import por.ayf.eng.tetris.game.Score;
import por.ayf.eng.tetris.util.Util;
import por.ayf.eng.tetris.view.ViewMainWindow;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *  JDialog will show the statistics of the game through a binary file.
 * 
 *  @author: Ángel Yagüe Flor.
 *  @version: 1.0 - Stable.
 *  @version: 2.1 - Refactor the project.
 */

public class ComponentViewStatistics extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JButton btnAcept;
	private JPanel panel;
	private JLabel label_0;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private JLabel label_6;
	private JLabel label_7;
	private JLabel label_8;
	private JLabel lblThird;
	private JLabel lblSecond;
	private JLabel lblFirst;
	
	public ComponentViewStatistics(ViewMainWindow window, boolean modal) {
		super(window, modal);
		initComponents();
	}
	
	private void initComponents() {
		setAutoRequestFocus(false);
		setTitle("Estad\u00EDsticas");
		setBounds(100, 100, 450, 245);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		btnAcept = new JButton("Aceptar");
		btnAcept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnAcept.setBounds(345, 182, 89, 23);
		contentPanel.add(btnAcept);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Puntuaciones más altas", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 424, 160);
		contentPanel.add(panel);
		panel.setLayout(null);
		
		lblFirst = new JLabel("1º");
		lblFirst.setHorizontalAlignment(SwingConstants.CENTER);
		lblFirst.setBounds(40, 41, 46, 14);
		panel.add(lblFirst);
		
		lblSecond = new JLabel("2º");
		lblSecond.setHorizontalAlignment(SwingConstants.CENTER);
		lblSecond.setBounds(40, 80, 46, 14);
		panel.add(lblSecond);
		
		lblThird = new JLabel("3º");
		lblThird.setHorizontalAlignment(SwingConstants.CENTER);
		lblThird.setBounds(40, 119, 46, 14);
		panel.add(lblThird);
		
		label_0 = new JLabel("----------");
		label_0.setBounds(100, 41, 90, 14);
		panel.add(label_0);
		
		label_1 = new JLabel("----------");
		label_1.setBounds(100, 80, 90, 14);
		panel.add(label_1);
		
		label_2 = new JLabel("----------");
		label_2.setBounds(100, 119, 90, 14);
		panel.add(label_2);
		
		label_3 = new JLabel("----------");
		label_3.setBounds(200, 41, 90, 14);
		panel.add(label_3);
		
		label_4 = new JLabel("----------");
		label_4.setBounds(200, 80, 90, 14);
		panel.add(label_4);
		
		label_5 = new JLabel("----------");
		label_5.setBounds(200, 119, 90, 14);
		panel.add(label_5);
		
		label_6 = new JLabel("----------");
		label_6.setBounds(300, 41, 114, 14);
		panel.add(label_6);
		
		label_7 = new JLabel("----------");
		label_7.setBounds(300, 80, 46, 14);
		panel.add(label_7);
		
		label_8 = new JLabel("----------");
		label_8.setBounds(300, 119, 114, 14);
		panel.add(label_8);
		
		setLocationRelativeTo(null); 
		setResizable(false);
		
		generateScores(); 
	}
	
	private void generateScores() {
		File fileScore = new File("score");
		ControlScore control = null;
		Score score = null;
		
		try {
			control = new ControlScore(fileScore);
		
			for(int i = 0; i < control.getLength(); i++) { 
				score = control.getValueIn(i);

				switch(i) {
					case 0:
						label_0.setText(score.getName());
						label_3.setText(String.valueOf(score.getScore()) + " pts");
						label_6.setText(String.valueOf(score.getLines()) + " líneas");
						break;
					case 1:
						label_1.setText(score.getName());
						label_4.setText(String.valueOf(score.getScore()) + " pts");
						label_7.setText(String.valueOf(score.getLines()) + " líneas");
						break;
					case 2:
						label_2.setText(score.getName());
						label_5.setText(String.valueOf(score.getScore()) + " pts");
						label_8.setText(String.valueOf(score.getLines()) + " líneas");
						break;
					default:
						break;
				}
			}
		} catch (IOException ex) {
			Util.logMessage(Util.LEVEL_ERROR, "Ha ocurrido un error al visualizar las estadísticas.", ComponentViewStatistics.class, ex);
		}
	}
}

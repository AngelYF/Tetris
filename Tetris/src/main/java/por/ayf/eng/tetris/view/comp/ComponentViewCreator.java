package por.ayf.eng.tetris.view.comp;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import por.ayf.eng.tetris.view.ViewMainWindow;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *  JDialog with information about me.
 * 
 *  @author: Ángel Yagüe Flor.
 *  @version: 1.0 - Stable.
 *  @version: 2.1 - Refactor the project.
 */

public class ComponentViewCreator extends JDialog {
	private static final long serialVersionUID = 1L; 	
	private final JPanel contentPanel = new JPanel(); 
	private JButton btnAcept;
	private JLabel lblIcon;
	private JLabel lblAngel;
	private JLabel lblVersion;
	
	public ComponentViewCreator(ViewMainWindow window, boolean modal) {
		super(window, modal);
		initComponents();
	}
	
	private void initComponents() {
		setTitle("Acerca del creador");
		setBounds(100, 100, 400, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			lblIcon = new JLabel("");
			lblIcon.setIcon(new ImageIcon(getClass().getResource("/images/icon.png")));
			lblIcon.setBounds(10, 11, 135, 145);
			contentPanel.add(lblIcon);
		}
		{
			JLabel lblProyectText = new JLabel("Proyecto de 2º DAM - Tetris");
			lblProyectText.setBounds(155, 25, 215, 14);
			contentPanel.add(lblProyectText);
		}
		
		lblAngel = new JLabel("Programado por Ángel Yagüe Flor");
		lblAngel.setBounds(155, 50, 215, 14);
		contentPanel.add(lblAngel);
		
		lblVersion = new JLabel("Versión 2.1");
		lblVersion.setBounds(155, 75, 215, 14);
		contentPanel.add(lblVersion);
		
		btnAcept = new JButton("Aceptar");
		btnAcept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnAcept.setBounds(281, 128, 89, 23);
		contentPanel.add(btnAcept);
		
		setLocationRelativeTo(null); // Center in the screen.
		setResizable(false);
	}
}

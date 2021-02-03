package por.ayf.eng.tetris.view.comp;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import por.ayf.eng.tetris.view.ViewMainWindow;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.Color;

/**
 *  JDialog will describe the controls that have been used moreover of have the possibility to change them.
 * 
 *  @author: Ángel Yagüe Flor.
 *  @version: 1.0 - Stable.
 *  @version: 2.1 - Refactor the project.
 */

public class ComponentViewControls extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField tfLeft;
	private JTextField tfRight;
	private JTextField tfDown;
	private JTextField tfRotate;
	private JTextField tfFall;
	private JTextField tfReservation;
	private JLabel lblLeft;
	private JLabel lblRight;
	private JLabel lblDown;
	private JLabel lblRotate;
	private JLabel lblFreeFall;
	private JLabel lblReservation;
	private JButton btnAcept;
	private JButton btnCancel;
	private int left;
	private int right;
	private int down;
	private int rotate;
	private int fall;
	private int reservation;
	
	public ComponentViewControls(ViewMainWindow window, boolean modal) {
		super(window, modal);
		initComponents();
	}
	
	private void initComponents() {
		this.down = ViewMainWindow.down;
		this.left = ViewMainWindow.left;
		this.right = ViewMainWindow.right;
		this.rotate = ViewMainWindow.rotate;
		this.fall = ViewMainWindow.fall;
		this.reservation = ViewMainWindow.reservation;
		
		setTitle("Controles");
		setBounds(100, 100, 500, 200);
		setLocationRelativeTo(null); 
		setResizable(false); 
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		lblLeft = new JLabel("Izquierda");
		lblLeft.setBounds(10, 30, 71, 14);
		contentPanel.add(lblLeft);
		
		lblRight = new JLabel("Derecha");
		lblRight.setBounds(10, 55, 71, 14);
		contentPanel.add(lblRight);
		
		lblDown = new JLabel("Abajo");
		lblDown.setBounds(10, 80, 71, 14);
		contentPanel.add(lblDown);
		
		tfLeft = new JTextField();
		tfLeft.setEditable(false);
		tfLeft.setBackground(Color.WHITE);
		tfLeft.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == left ||
				   arg0.getKeyCode() == right ||
				   arg0.getKeyCode() == down ||
				   arg0.getKeyCode() == rotate ||
				   arg0.getKeyCode() == fall ||
				   arg0.getKeyCode() == reservation) {
					return;
				}
					
				left = arg0.getKeyCode();
				changeControl(arg0, tfLeft);
			}
		});
		tfLeft.setHorizontalAlignment(SwingConstants.CENTER);
		tfLeft.setText(KeyEvent.getKeyText(ViewMainWindow.left));
		tfLeft.setBounds(77, 27, 160, 20);
		contentPanel.add(tfLeft);
		tfLeft.setColumns(10);
		
		tfRight = new JTextField();
		tfRight.setBackground(Color.WHITE);
		tfRight.setEditable(false);
		tfRight.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == left ||
				   arg0.getKeyCode() == right ||
				   arg0.getKeyCode() == down ||
				   arg0.getKeyCode() == rotate ||
				   arg0.getKeyCode() == fall ||
				   arg0.getKeyCode() == reservation) {
					return;
				}
			
				right = arg0.getKeyCode();
				changeControl(arg0, tfRight);
			}
		});
		tfRight.setHorizontalAlignment(SwingConstants.CENTER);
		tfRight.setText(KeyEvent.getKeyText(ViewMainWindow.right));
		tfRight.setBounds(77, 52, 160, 20);
		contentPanel.add(tfRight);
		tfRight.setColumns(10);
		
		tfDown = new JTextField();
		tfDown.setBackground(Color.WHITE);
		tfDown.setEditable(false);
		tfDown.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == left ||
				   arg0.getKeyCode() == right ||
				   arg0.getKeyCode() == down ||
				   arg0.getKeyCode() == rotate ||
				   arg0.getKeyCode() == fall ||
				   arg0.getKeyCode() == reservation) {
					return;
				}
			
				down = arg0.getKeyCode();
				changeControl(arg0, tfDown);
			}
		});
		tfDown.setHorizontalAlignment(SwingConstants.CENTER);
		tfDown.setText(KeyEvent.getKeyText(ViewMainWindow.down));
		tfDown.setBounds(77, 77, 160, 20);
		contentPanel.add(tfDown);
		tfDown.setColumns(10);
		
		lblRotate = new JLabel("Rotar");
		lblRotate.setBounds(247, 30, 71, 14);
		contentPanel.add(lblRotate);
		
		lblFreeFall = new JLabel("Caída libre");
		lblFreeFall.setBounds(247, 55, 71, 14);
		contentPanel.add(lblFreeFall);
		
		lblReservation = new JLabel("Reservar");
		lblReservation.setBounds(247, 80, 71, 14);
		contentPanel.add(lblReservation);
		
		tfRotate = new JTextField();
		tfRotate.setBackground(Color.WHITE);
		tfRotate.setEditable(false);
		tfRotate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == left ||
				   arg0.getKeyCode() == right ||
				   arg0.getKeyCode() == down ||
				   arg0.getKeyCode() == rotate ||
				   arg0.getKeyCode() == fall ||
				   arg0.getKeyCode() == reservation) {
					return;
				}
			
				rotate = arg0.getKeyCode();
				changeControl(arg0, tfRotate);
			}
		});
		tfRotate.setHorizontalAlignment(SwingConstants.CENTER);
		tfRotate.setText(KeyEvent.getKeyText(ViewMainWindow.rotate));
		tfRotate.setBounds(315, 27, 160, 20);
		contentPanel.add(tfRotate);
		tfRotate.setColumns(10);
		
		tfFall = new JTextField();
		tfFall.setBackground(Color.WHITE);
		tfFall.setEditable(false);
		tfFall.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == left ||
				   arg0.getKeyCode() == right ||
				   arg0.getKeyCode() == down ||
				   arg0.getKeyCode() == rotate ||
				   arg0.getKeyCode() == fall ||
				   arg0.getKeyCode() == reservation) {
					return;
				}
			
				fall = arg0.getKeyCode();
				changeControl(arg0, tfFall);
			}
		});
		tfFall.setHorizontalAlignment(SwingConstants.CENTER);
		tfFall.setText(KeyEvent.getKeyText(ViewMainWindow.fall));
		tfFall.setBounds(315, 52, 160, 20);
		contentPanel.add(tfFall);
		tfFall.setColumns(10);
		
		tfReservation = new JTextField();
		tfReservation.setBackground(Color.WHITE);
		tfReservation.setEditable(false);
		tfReservation.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == left ||
				   arg0.getKeyCode() == right ||
				   arg0.getKeyCode() == down ||
				   arg0.getKeyCode() == rotate ||
				   arg0.getKeyCode() == fall ||
				   arg0.getKeyCode() == reservation) { 
					return;
				}
			
				reservation = arg0.getKeyCode();
				changeControl(arg0, tfReservation);
			}
		});
		tfReservation.setHorizontalAlignment(SwingConstants.CENTER);
		tfReservation.setText(KeyEvent.getKeyText(ViewMainWindow.reservation));
		tfReservation.setBounds(315, 77, 160, 20);
		contentPanel.add(tfReservation);
		tfReservation.setColumns(10);
		
		btnAcept = new JButton("Aceptar");
		btnAcept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { // Assign the new keys.
				ViewMainWindow.down = down;
				ViewMainWindow.left = left;
				ViewMainWindow.right = right;
				ViewMainWindow.rotate = rotate;
				ViewMainWindow.fall = fall;
				ViewMainWindow.reservation = reservation;
				
				dispose();
			}
		});
		btnAcept.setBounds(287, 125, 89, 23);
		contentPanel.add(btnAcept);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { 
				dispose();
			}
		});
		btnCancel.setBounds(386, 125, 89, 23);
		contentPanel.add(btnCancel);		
	}
	
	/**
	 *  Method will change the text of JTextField that receive the key that we are pressed.
	 * 
	 *  @param evt: Event of the key pressed.
	 *  @param jtf: JTextField will modify.
	 */
	
	private void changeControl(KeyEvent evt, JTextField jtf) {
		// Get the keycode and get his text. According to the text, I do something or other
		if(KeyEvent.getKeyText(evt.getKeyCode()).equals("Desconocido keyCode: 0x0")) {
			jtf.setText("Desconocido");
		} else {
			jtf.setText(KeyEvent.getKeyText(evt.getKeyCode()));
		}
	}
}

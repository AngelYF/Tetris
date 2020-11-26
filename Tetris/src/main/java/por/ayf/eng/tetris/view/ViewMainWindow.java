package por.ayf.eng.tetris.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem; 
import javax.swing.KeyStroke;

import por.ayf.eng.tetris.game.Game;
import por.ayf.eng.tetris.view.comp.ComponentViewControls;
import por.ayf.eng.tetris.view.comp.ComponentViewCreator;
import por.ayf.eng.tetris.view.comp.ComponentViewStatistics;

/**
 *  Window of the Tetris.
 * 
 *  @author: Ángel Yagüe Flor
 *  @version: 1.0 - Stable.
 *  @version: 2.0 - Fix bugs of the menú and added new elements.
 *  @version: 2.1 - Refactor the project.
 */

public class ViewMainWindow extends JFrame {
	private static final long serialVersionUID = 1L;	// Serial.
	
	private final int WIDTH = (Game.SIZE * 3) + Game.WIDTH_PANEL + Game.WIDTH_SUBPANEL + (Game.BORDER * 4) + 10; 	
	private final int HIGH = Game.HIGH_MENU + Game.SIZE * 4 + Game.BORDER * 2 + Game.HIGH_PANEL - 5; 		
	
	private Game game; 																				
	
	public static int left;
	public static int right;
	public static int down;
	public static int rotate;
	public static int fall;
	public static int reservation;
	
	public ViewMainWindow() {
		initComponents();
	}
	
	private void initComponents() {
		// KeyCode to the controls by default:
		left = 37;
		right = 39;
		down = 40;
		rotate = 38;
		fall = 32;
		reservation = 17;
	
		this.setTitle("Tetris"); 
		this.setIconImage(new ImageIcon("src/main/resources/images/icon.png").getImage());
		this.setSize(WIDTH, HIGH);
		this.setLocationRelativeTo(null); // Center the screen.
		this.setResizable(false);
		this.setLayout(null); 
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
		
		this.addKeyListener(new KeyAdapter() {
	        public void keyPressed(KeyEvent evt) {     	
	        	if(evt.getKeyCode() == down) { 
	        		game.move(1);
	        	}
	        	else if(evt.getKeyCode() == left) { 
	        		game.move(2);
	        	}
				else if(evt.getKeyCode() == rotate) {
					game.rotate();   		
				}
				else if(evt.getKeyCode() == right) { 
					game.move(3);
				}
				else if(evt.getKeyCode() == fall) {
					game.freeFall();
				}
				else if(evt.getKeyCode() == reservation) {
					game.reservation();
				}
	        }
	    });
		
		this.game = new Game(Game.SIZE, Game.SIZE * 2, this);
		this.game.setFocusable(false); // This is important, if I click in the game, get the focus and block the keyboard.
		this.add(this.game);
		
		createMenu(this.game); 
	
		this.setVisible(true); 
	}
	
	/**
	 *  Method will create the menu of the game.
	 */
	
	private void createMenu(final Game game) {
		JMenuBar menu = new JMenuBar();
		menu.setBounds(0, 0, WIDTH, 20);
		
		JMenu file = new JMenu("Archivo");
		
		JMenuItem newJI = new JMenuItem("Nuevo juego          ");		
		newJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		newJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.newGame();
			}
		});
		
		file.add(newJI);
		
		JMenuItem pauseJI = new JMenuItem("Pausar juego          ");		
		pauseJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		pauseJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.pauseGame();
			}
		});
		
		file.add(pauseJI);
		file.addSeparator();
		
		JMenuItem statistics = new JMenuItem("Estadísticas          ");		
		statistics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		statistics.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				statistics();
			}
		});
		
		file.add(statistics);
		file.addSeparator();
		
		JMenuItem exitJI = new JMenuItem("Salir          ");
		exitJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exitJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(1);
			}
		});
		
		file.add(exitJI);
	
		menu.add(file);
		
		JMenu options = new JMenu("Opciones");

		JMenuItem controlsJI = new JMenuItem("Controles de juego          ");		
		controlsJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		controlsJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controls();
			}
		});
		
		options.add(controlsJI);
		options.addSeparator();
		
		JMenu dificult = new JMenu("Dificultad          ");		
		
		final JMenuItem normalJI = new JMenuItem("Normal          ");
		final JMenuItem hardJI = new JMenuItem("Difícil          ");
		
		normalJI.setEnabled(false);
		normalJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		normalJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.setDificult("normal");
				normalJI.setEnabled(false);
				hardJI.setEnabled(true);
			}
		});
		
		hardJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		hardJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.setDificult("dificil");
				hardJI.setEnabled(false);
				normalJI.setEnabled(true);
			}
		});
		
		dificult.add(normalJI);
		dificult.add(hardJI);
		options.add(dificult);
		
		JMenu sound = new JMenu("Sonido          ");		
		
		final JMenuItem muteJI = new JMenuItem("Silenciar          ");
		final JMenuItem NmuteJI = new JMenuItem("Desilenciar          ");
		
		NmuteJI.setEnabled(false);
		
		muteJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		muteJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.setSoundActivated(false);
				muteJI.setEnabled(false);
				NmuteJI.setEnabled(true);
			}
		});
		
		NmuteJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		NmuteJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.setSoundActivated(true);
				NmuteJI.setEnabled(false);
				muteJI.setEnabled(true);
			}
		});
		
		sound.add(muteJI);
		sound.add(NmuteJI);
		options.add(sound);
		
		menu.add(options);
		
		JMenu help = new JMenu("Ayuda");
	
		JMenuItem creatorJI = new JMenuItem("Acerca del creador          ");
		creatorJI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		creatorJI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				creator();
			}
		});
		
		help.add(creatorJI);
		menu.add(help);

		this.add(menu);
	}
	
	/**
	 *  Method will create the JDialog of the creator.
	 */
	
	private void creator() {
		new ComponentViewCreator(this, true).setVisible(true);
	}
	
	/**
	 *  Method will create the JDialog of the controls.
	 */
	
	private void controls() {
		new ComponentViewControls(this, true).setVisible(true);
	}
	
	/**
	 *  Method will create the JDialog of the statistics.
	 */
	
	private void statistics() {
		new ComponentViewStatistics(this, true).setVisible(true);
	}
	
	
	/**
	 * 	Method will show the statistics.
	 */
	
	public void showStatistics() {
		 statistics();
	}
}

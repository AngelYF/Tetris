package por.ayf.eng.tetris.game.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *  Class will define the use of sounds in the game.
 * 
 *  @author: Ángel Yagüe Flor.
 *  @version: 1.0 - Stable.
 */

public class Audio {
	
	/**
	 *  Method that play a sound of the game.
	 * 
	 *  @param sound: clip that store the sound.
	 *  @param name of the sound.
	 */
	
	public static Clip loadSound(Clip sound, String name) {
		try {
			sound = AudioSystem.getClip();
			sound.open(AudioSystem.getAudioInputStream(Audio.class.getResource("/sounds/" + name)));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return sound;
	}
}

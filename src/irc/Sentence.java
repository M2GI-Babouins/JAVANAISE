/***
 * Sentence class : used for keeping the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.io.Serializable;

public class Sentence implements Serializable, ISentence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String 	data;
  
	public Sentence() {
		data = "";
	}

	@Action(name = "write")
	public void write(String text) {
		data = text;
	}

	@Action(name = "read")
	public String read() {
		return data;	
	}
	
}
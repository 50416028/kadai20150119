package reservation;

import java.awt.Choice;

public class ChoiceHour extends Choice{
	
	ChoiceHour(){
		//
		resetRange(9,21);
	}

	public void resetRange( int start, int end){
		// 
		int number = end - start +1;
		// 
		removeAll();
		// 
		while (start<=end){
			String h = String.valueOf(start);
			//ˆêŒ…‚Ìê‡,‘O‚É0‚ð•t‚¯‚é
			if ( h.length()==1){
				h = "0" + h;
			}
			// 
			add(h);
			// start‚ð1‘‚â‚·
			start++;
		}
	}

	// ÅŒã‚ÉÝ’è‚³‚ê‚Ä‚¢‚éŽž‚ð•Ô‚·
	public String getLast(){
		return getItem( getItemCount()-1 );
	}


}

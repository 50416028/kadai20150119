package reservation;

import java.awt.Choice;

public class ChoiceMinute extends Choice{
	
	public ChoiceMinute(){
		// 00•ª‚Æ30•ª‚Ì‚Ý‚ð“o˜^
		add("00");
		add("30");
	}

	//
	public int getMinute(int index){
		if ( index==0){
			return 0;
		} else {
			return 30;
		}
	}
}

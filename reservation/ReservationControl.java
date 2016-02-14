package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class ReservationControl {
	//���̗\��V�X�e��
		String reservation_userid;
		private boolean flagLogin;

		//login=true
		ReservationControl(){
			flagLogin = false;
		}


	
	public String getReservationOn( String facility, String ryear_str, String rmonth_str, String rday_str){
		
		
		String res = "";
		// 
		try {
			int ryear = Integer.parseInt( ryear_str);
			int rmonth = Integer.parseInt( rmonth_str);
			int rday = Integer.parseInt( rday_str);
		} catch(NumberFormatException e){
			res ="�N�����ɂ͐������w�肵�Ă�������";
			return res;
		}
		res = facility + " �\���\n\n";

		// 
		if (rmonth_str.length()==1) {
			rmonth_str = "0" + rmonth_str;
		}
		if ( rday_str.length()==1){
			rday_str = "0" + rday_str;
		}
		
		String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

		//(1) MySQL ���g�p���鏀��
		//connectDB();
		MySQL mysql = new MySQL();

		
		try {
		
			ResultSet rs = mysql.getReservation(rdate, facility);
			boolean exist = false;
			while(rs.next()){
				String start = rs.getString("start_time");
				String end = rs.getString("end_time");
				res += " " + start + " -- " + end + "\n";
				exist = true;
			}

			if ( !exist){ 
				res = "�\��͂���܂���";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return res;
	}



	

	public String loginLogout( MainFrame frame){
		String res=""; //���ʂ�����ϐ�
		if ( flagLogin){ 
			flagLogin = false;
			frame.buttonLog.setLabel(" ���O�C��"); 
		} else {
			
			LoginDialog ld = new LoginDialog(frame);
			ld.setVisible(true);
			ld.setModalityType(LoginDialog.ModalityType.APPLICATION_MODAL);
			
			if ( ld.canceled){
				return "";
			}

			reservation_userid = ld.tfUserID.getText();
			
			String password = ld.tfPassword.getText();
			//(2) MySQL�̑���(SELECT���̎��s)
			try { // user�̏����擾����N�G��
				MySQL mysql = new MySQL();
				ResultSet rs = mysql.getLogin(reservation_userid); 
				if (rs.next()){
					rs.getString("password");
					String password_from_db = rs.getString("password");
					if ( password_from_db.equals(password)){ //�F�ؐ���
						flagLogin = true;
						frame.buttonLog.setLabel("���O�A�E�g");
						res = "";
					}else {
						//�F�؎��s
						res = "���O�C���ł��܂���.ID �p�X���[�h���Ⴂ�܂�";
					}
				} else { //�F�؎��s;
					res = "���O�C���ł��܂���.ID �p�X���[�h���Ⴂ�܂��B";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
}
		return res;
	}

	private boolean checkReservationDate( int y, int m, int d){
		// �\���
		Calendar dateR = Calendar.getInstance();
		dateR.set( y, m-1, d);	// ������1�����Ȃ���΂Ȃ�Ȃ����Ƃɒ��ӁI

		// �����̂P����
		Calendar date1 = Calendar.getInstance();
		date1.add(Calendar.DATE, 1);

		// �����̂R������i90����)
		Calendar date2 = Calendar.getInstance();
		date2.add(Calendar.DATE, 1000);

		if ( dateR.after(date1) && dateR.before(date2)){
			return true;
		}
		return false;
	}
	public String makeReservation(MainFrame frame){

		String res="";		//���ʂ�����ϐ�

		if ( flagLogin){ // ���O�C�����Ă����ꍇ
			//�V�K�\���ʍ쐬
			ReservationDialog rd = new ReservationDialog(frame);

			// �V�K�\���ʂ̗\����ɁC���C����ʂɐݒ肳��Ă���N������ݒ肷��
			rd.tfYear.setText(frame.tfYear.getText());
			rd.tfMonth.setText(frame.tfMonth.getText());
			rd.tfDay.setText(frame.tfDay.getText());

			// �V�K�\���ʂ�����
			rd.setVisible(true);
			if ( rd.canceled){
				return res;
			}
			
			try {
				//�V�K�\���ʂ���N�������擾
				String ryear_str = rd.tfYear.getText();
				String rmonth_str = rd.tfMonth.getText();
				String rday_str = rd.tfDay.getText();

				// �N�������������ǂ��������`�F�b�N���鏈��
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);

				if ( checkReservationDate( ryear, rmonth, rday)){	// ���Ԃ̏����𖞂����Ă���ꍇ
					// �V�K�\���ʂ���{�ݖ��C�J�n�����C�I���������擾
					String facility = rd.choiceFacility.getSelectedItem();
					String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
					String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

					if( st.equals(et)){		//�J�n�����ƏI��������������
						res = "�J�n�����ƏI�������������ł�";
					} else {

						try {
							// ���Ɠ����ꌅ��������C�O��0�����鏈��
							if (rmonth_str.length()==1) {
								rmonth_str = "0" + rmonth_str;
							}
							if ( rday_str.length()==1){
								rday_str = "0" + rday_str;
							}
							//(2) MySQL�̑���(SELECT���̎��s)
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
			
							MySQL mysql = new MySQL();
							ResultSet rs = mysql.selectReservation(rdate, facility);
						      // �������ʂɑ΂��ďd�Ȃ�`�F�b�N�̏���
						      boolean ng = false;	//�d�Ȃ�`�F�b�N�̌��ʂ̏����l�i�d�Ȃ��Ă��Ȃ�=false�j��ݒ�
							  // �擾�������R�[�h���ɑ΂��Ċm�F
						      while(rs.next()){
							  		//���R�[�h�̊J�n�����ƏI�����������ꂼ��start��end�ɐݒ�
							        String start = rs.getString("start_time");
							        String end = rs.getString("end_time");

							        if ( (start.compareTo(st)<0 && st.compareTo(start)<0) ||		//���R�[�h�̊J�n�������V�K�̊J�n�����@AND�@�V�K�̊J�n���������R�[�h�̏I������
							        	 (st.compareTo(end)<0 && start.compareTo(et)<0)){		//�V�K�̊J�n���������R�[�h�̊J�n�����@AND�@���R�[�h�̊J�n�������V�K�̊J�n����
										 	// �d���L��̏ꍇ�� ng ��true�ɐݒ�
							        	ng = true; break;
							        }
						      }
							  /// �d�Ȃ�`�F�b�N�̏����@�����܂�  ///////

						      if (!ng){	//�d�Ȃ��Ă��Ȃ��ꍇ
			
						    	  int rs_int = mysql.setReservation(rdate, st, et, res, facility);
						    	  res ="�\�񂳂�܂���";
						      } else {	//�d�Ȃ��Ă����ꍇ
						    	  res = "���ɂ���\��ɏd�Ȃ��Ă��܂�";
						      }
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					res = "�\����������ł��D";
				}
			} catch(NumberFormatException e){
				res ="�\����ɂ͐������w�肵�Ă�������";
			}
		} else { // ���O�C�����Ă��Ȃ��ꍇ
			res = "���O�C�����Ă�������";
		}
		return res;
	}
	public String ReservationConfirm(MainFrame frame){
		String res =""; 
		MySQL mysql =new MySQL();
		try {
			
			ResultSet rs = mysql.showReservation();
			boolean exist = false;
			while(rs.next()){
				String date = rs.getString("date");
				String start = rs.getString("start_time");
				String end = rs.getString("end_time");
				String facility = rs.getString("facility_name");
				res += date+ start + " -- " + end + facility+"\n";
				exist = true;
			}

			if ( !exist){ 
				res = "�\��͂���܂���";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return res;
	}
public String Explanation(String facility){
		String res =""; 
		MySQL mysql =new MySQL();
		try {
			
			ResultSet rs = mysql.facility_ex(facility);
			boolean exist = false;
			while(rs.next()){
				String oimo = rs.getString("explanation");
				res +=   oimo +"\n";
				exist = true;
			}

			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return res;
	}
public String deleteReservation(MainFrame frame){

	String res="";		//���ʂ�����ϐ�

	if ( flagLogin){ // ���O�C�����Ă����ꍇ
		//�V�K�\���ʍ쐬
		CancelDialog cd = new CancelDialog(frame);

		// �V�K�\���ʂ̗\����ɁC���C����ʂɐݒ肳��Ă���N������ݒ肷��
		cd.tfYear.setText(frame.tfYear.getText());
		cd.tfMonth.setText(frame.tfMonth.getText());
		cd.tfDay.setText(frame.tfDay.getText());

		// �V�K�\���ʂ�����
		cd.setVisible(true);
		if ( cd.canceled){
			return res;
		}
		
		try {
			//�V�K�\���ʂ���N�������擾
			String ryear_str = cd.tfYear.getText();
			String rmonth_str = cd.tfMonth.getText();
			String rday_str = cd.tfDay.getText();

			// �N�������������ǂ��������`�F�b�N���鏈��
			int ryear = Integer.parseInt( ryear_str);
			int rmonth = Integer.parseInt( rmonth_str);
			int rday = Integer.parseInt( rday_str);

			if ( checkReservationDate( ryear, rmonth, rday)){	// ���Ԃ̏����𖞂����Ă���ꍇ
				// �V�K�\���ʂ���{�ݖ��C�J�n�����C�I���������擾
				String facility = cd.choiceFacility.getSelectedItem();
				String st = cd.startHour.getSelectedItem()+":" + cd.startMinute.getSelectedItem() +":00";
				String et = cd.endHour.getSelectedItem() + ":" + cd.endMinute.getSelectedItem() +":00";

				if( st.equals(et)){		//�J�n�����ƏI��������������
					res = "�J�n�����ƏI�������������ł�";
				} else {

					try {
						// ���Ɠ����ꌅ��������C�O��0�����鏈��
						if (rmonth_str.length()==1) {
							rmonth_str = "0" + rmonth_str;
						}
						if ( rday_str.length()==1){
							rday_str = "0" + rday_str;
						}
						//(2) MySQL�̑���(SELECT���̎��s)
						String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
		
						MySQL mysql = new MySQL();
						ResultSet rs = mysql.selectReservation(rdate, facility);
					      // �������ʂɑ΂��ďd�Ȃ�`�F�b�N�̏���
					      boolean ng = false;	//�d�Ȃ�`�F�b�N�̌��ʂ̏����l�i�d�Ȃ��Ă��Ȃ�=false�j��ݒ�
						  // �擾�������R�[�h���ɑ΂��Ċm�F
					      while(rs.next()){
						  		//���R�[�h�̊J�n�����ƏI�����������ꂼ��start��end�ɐݒ�
						        String start = rs.getString("start_time");
						        String end = rs.getString("end_time");

						        if ( (start.compareTo(st)<0 && st.compareTo(start)<0) ||		//���R�[�h�̊J�n�������V�K�̊J�n�����@AND�@�V�K�̊J�n���������R�[�h�̏I������
						        	 (st.compareTo(end)<0 && start.compareTo(et)<0)){		//�V�K�̊J�n���������R�[�h�̊J�n�����@AND�@���R�[�h�̊J�n�������V�K�̊J�n����
									 	// �d���L��̏ꍇ�� ng ��true�ɐݒ�
						        	ng = true; break;
						        }
					      }
						  /// �d�Ȃ�`�F�b�N�̏����@�����܂�  ///////

					      if (!ng){	//�d�Ȃ��Ă��Ȃ��ꍇ
		
					    	  res = "�\�񂪂���܂���D";
					      } else {	//�d�Ȃ��Ă����ꍇ
					    	  int rs_int = mysql.OutReservation(rdate, st, et, res, facility);
					    	  res ="�������܂���";
					      }
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				res = "�\�񂪂���܂���D";
			}
		} catch(NumberFormatException e){
			res ="�\����ɂ͐������w�肵�Ă�������";
		}
	} else { // ���O�C�����Ă��Ȃ��ꍇ
		res = "���O�C�����Ă�������";
	}
	return res;
}
}



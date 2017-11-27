import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/21
 * Time: 15:09
 * Description:Main entrance
 */
public class Main {

	public static void main(String[] args) {
		try {
			final DialogMain mainForm = new DialogMain();
			mainForm.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					int result = JOptionPane.showConfirmDialog(null, ConfigToolProperties.getString("String_DialogMainClose"), "Information", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						mainForm.commitLocal();
						System.exit(0);
					} else if (result==JOptionPane.NO_OPTION){
						return;
					}
				}
			});
			mainForm.setVisible(true);
		} catch (Exception e) {
			System.out.println(e);
		}
	}



}

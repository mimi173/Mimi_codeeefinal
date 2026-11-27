
package Utilidades;
import Ventanas.Login;
import java.awt.Window;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/**
 *
 * @author Mimi
 */
public class CuentaRegresiva {
    private static int segundos = 10;
    
    public static void TR() {
        Timer timer = new Timer();
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                if (segundos == 0) {                   
                    SwingUtilities.invokeLater(() -> {                                            
                    Window[] windows = Window.getWindows();
                        for (Window window : windows) {
                            if (window instanceof JFrame) {
                                JFrame frame = (JFrame) window;
                                frame.dispose();
                            }
                        }
                    });
                    
                    Login Inicio = new Login();
                    Inicio.setVisible(true);
                    timer.cancel();
                } 
                else {                  
                    segundos--;
                }
            }
        };          
        timer.scheduleAtFixedRate(tarea, 0, 200);
    }
}

import javax.swing.*;
import java.awt.*;

public class PauseScreen extends JPanel {
    PauseScreen() {
        setBackground(new Color(0, 0, 0, 150));
        setLayout(new GridBagLayout());
        JLabel pauseLabel = new JLabel("Paused");
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setFont(new Font("Montserrat", Font.PLAIN, 40));
        add(pauseLabel);
    }
}
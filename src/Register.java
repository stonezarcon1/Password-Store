import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class Register extends JFrame {
    private String usernameInTextArea;
    private String passwordInTextArea;
    private String confirmPasswordInTextArea;

    public Register() {
    }

    public void createAndShowGUI() { //creating GUI for register area
            setTitle("New User");
            setPreferredSize(new Dimension(400, 250));
            setResizable(false); //prevents strange layouts
            setLayout(null); //allows custom placement
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            Border border = new LineBorder(Color.BLACK);

            JLabel loginLabel = new JLabel("Enter your new account credentials.");
            loginLabel.setBounds(5, 5, 250, 20);

            JLabel username = new JLabel("Username: ");
            username.setBounds(5, 40, 100, 20);
            JTextArea usernameArea = new JTextArea();
            usernameArea.setBounds(125, 40, 150, 20);
            usernameArea.setBorder(border);

            JLabel password = new JLabel("Password: ");
            password.setBounds(5, 80, 100, 20);
            JTextArea passwordArea = new JTextArea();
            passwordArea.setBounds(125, 80, 150, 20);
            passwordArea.setBorder(border);

            JLabel confirmPassword = new JLabel("Confirm Password: ");
            confirmPassword.setBounds(5, 120, 150, 20);
            JTextArea confirmPasswordArea = new JTextArea();
            confirmPasswordArea.setBounds(125, 120, 150, 20);
            confirmPasswordArea.setBorder(border);

            JButton registerButton = new JButton("Create account");
            registerButton.setBounds(30, 160, 150, 20);

            JButton goBackButton = new JButton("Go back");
            goBackButton.setBounds(200, 160, 150, 20);

            usernameArea.addKeyListener(new KeyAdapter() { //key listener for tab functionality
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        passwordArea.requestFocus();
                        e.consume();
                    }
                }
            });
            passwordArea.addKeyListener(new KeyAdapter() { //key listener for tab functionality
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        confirmPasswordArea.requestFocus();
                        e.consume();
                    }
                }
            });
            confirmPasswordArea.addKeyListener(new KeyAdapter() { //key listener for tab functionality
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        usernameArea.requestFocus();
                        e.consume();
                    }
                }
            });

            registerButton.addActionListener(new ActionListener() { //used to enter user information
                @Override
                public void actionPerformed(ActionEvent ae) {
                    usernameInTextArea = usernameArea.getText().toLowerCase(Locale.ROOT);
                    passwordInTextArea = passwordArea.getText();
                    confirmPasswordInTextArea = confirmPasswordArea.getText();
                    registerUser();
                    passwordArea.setText("");
                    confirmPasswordArea.setText("");
                }
            });

            goBackButton.addActionListener(new ActionListener() { //user to return to login screen
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    Login login = new Login();
                    login.createAndShowGUI();
                }
            });

            add(username); //adding all components to the JFrame
            add(usernameArea);
            add(password);
            add(passwordArea);
            add(loginLabel);
            add(registerButton);
            add(goBackButton);
            add(confirmPassword);
            add(confirmPasswordArea);
            pack();
            setLocationRelativeTo(null); //open centered
            setVisible(true);
    }
    public void registerUser() { //used to register a new user
        Database db = new Database();
        if (!usernameIsNotTaken() || !passwordMatches()) { //checking methods
            if (!passwordMatches()) { //checking that password and confirm password are equal
                JOptionPane.showMessageDialog(this, "Your passwords don't match"); //error message
            }
            if (!usernameIsNotTaken()) { //checking that username is available
                JOptionPane.showMessageDialog(this, "That username is taken"); //error message
            }
        }
        if (usernameIsNotTaken() && passwordMatches()) { //checking methods
            User user = new User(); //creating a user to pass through methods
            user.setUsername(usernameInTextArea);
            user.setPassword(passwordInTextArea);
            JOptionPane.showMessageDialog(this, "Registration successful."); //success message
            db.addUser(user); //adding user to db
        }
    }
    public boolean usernameIsNotTaken() { //checking username
        Database db = new Database();
        if (db.checkUsername(usernameInTextArea)) {
            return true;
        } else {
            return false;
        }
    }
    public boolean passwordMatches() { //checking passwords are equal
        if (passwordInTextArea.equals(confirmPasswordInTextArea)) {
            return true;
        } else {
            return false;
        }
    }
}
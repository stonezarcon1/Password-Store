import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login extends JFrame {
    private boolean isValidated = false; //can only be altered internally. Used to allow display to be loaded
    Login() {
    }
    public void createAndShowGUI() { //display the login GUI
        setTitle("Welcome");
        setPreferredSize(new Dimension(300, 200));
        setResizable(false); //prevents unintended layouts
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Border border = new LineBorder(Color.BLACK);

        JLabel loginLabel = new JLabel("Please enter your login credentials.");
        loginLabel.setBounds(5, 5, 200, 20);

        JLabel username = new JLabel("Username: ");
        username.setBounds(5, 40, 100, 20);
        JTextArea usernameArea = new JTextArea();
        usernameArea.setBounds(80, 40, 150, 20);
        usernameArea.setBorder(border);

        JLabel password = new JLabel("Password: ");
        password.setBounds(5, 80, 100, 20);
        JPasswordField passwordArea = new JPasswordField(30);
        passwordArea.setBounds(80, 80, 150, 20);
        passwordArea.setBorder(border);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(150, 120, 100, 20);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(30, 120, 100, 20);

        usernameArea.addKeyListener(new KeyAdapter() { //allows tab to next component
            @Override
            public void keyPressed(KeyEvent e) { //tab functionality
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    passwordArea.requestFocus();
                    e.consume();
                }
            }
        });

        passwordArea.addKeyListener(new KeyAdapter() { //allows tab to next component
            @Override
            public void keyPressed(KeyEvent e) { //tab functionality
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    usernameArea.requestFocus();
                    e.consume();
                }
            }
        });

        registerButton.addActionListener(new ActionListener() { //opens register frame on click
            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
                Register register = new Register();
                register.createAndShowGUI(); //create register object to display frame.
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = new User(); //creating user object that will be passed to display and database
                Database db = new Database(); //used for connecting to database
                if (userExists(usernameArea.getText())) { //checking that login is a valid username
                    user.setUsername(usernameArea.getText()); //if the username is valid, set user object username
                    user.setPlainPassword(String.valueOf(passwordArea.getPassword())); //setting plaintext password
                    //plaintext is set in order to avoid double hashing when verifying login information
                    if (db.loginUser(user)) { //used to check if username and pass match
                        Login login = new Login();
                        login.setValid(); //setting login to valid, allowing display to be opened.
                        dispose();
                        Display display = new Display();
                        display.createAndShowGUI(user, login); //creates gui for display area
                    } else {
                        errorMessage();
                    }
                }
            }
        });
        add(username);
        add(usernameArea);
        add(password);
        add(passwordArea);
        add(loginLabel);
        add(loginButton);
        add(registerButton);
        pack();
        setLocationRelativeTo(null); //open centered
        setVisible(true);
    }
    public boolean userExists(String username) { //checking if user exists
        Database db = new Database();
        if (!db.checkUsername(username)) {
            return true;
        } else {
            return false;
        }
    }
    public void errorMessage() { //sends an error message to JOptionPane
        JOptionPane.showMessageDialog(this, "Username or password is invalid.");
    }
    public boolean validStatus() { //used to validate a login
        return isValidated;
    }
    private void setValid() { //used to set a login instance to valid
        this.isValidated = true;
    }
}
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Display extends JFrame {
    private final Border border = BorderFactory.createLineBorder(Color.BLACK);
    private final String[] column ={"WEBSITE","USERNAME","PASSWORD"};
    private JTable jt;

    Display() {
    }
    public void createAndShowGUI(User user, Login login) {
        if (login.validStatus()) {
            Database db = new Database();
            setTitle("Secure Password Storage");
            setLayout(null);
            int WINDOW_WIDTH = 800;
            int WINDOW_HEIGHT = 500;
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setBounds(0, 100, 800, 400);
            add(panel);

            String infoString = "Type the name of the website, the username," +
                    "and the password that you use for the website. Press add when you're finished." +
                    " If you would like to delete a selection, click the row, and press 'Remove'.";
            JLabel infoLabel = new JLabel("<html>" + infoString + "</html>");
            infoLabel.setBounds(5,2,600, 40);

            //creating the website area
            JLabel websiteLabel = new JLabel("Website: ");
            websiteLabel.setBounds(50,40,100,18);
            JTextArea websiteInfo = new JTextArea();
            websiteInfo.setBorder(border);
            websiteInfo.setBounds(120, 40, 200, 18);

            JLabel usernameLabel = new JLabel("Username: ");
            usernameLabel.setBounds(50,60,100,18);
            JTextArea usernameInfo = new JTextArea();
            usernameInfo.setBorder(border);
            usernameInfo.setBounds(120, 60, 200, 18);

            JLabel passwordLabel = new JLabel("Password: ");
            passwordLabel.setBounds(50,80,100,18);
            JTextArea passwordInfo = new JTextArea();
            passwordInfo.setBorder(border);
            passwordInfo.setBounds(120, 80, 200, 18);

            jt = new JTable(db.getData(user),column);
            jt.setPreferredSize(new Dimension(800, 350));
            setActionMap();
            DefaultTableModel tableModel = new DefaultTableModel(db.getData(user), column) {
                @Override
                public boolean isCellEditable (int row, int column) {
                    //all cells uneditable
                    return false;
                }
            };

            jt.setModel(tableModel);
            JScrollPane sp=new JScrollPane(jt);
            sp.setBorder(border);

            panel.add(sp, BorderLayout.PAGE_END);

            JButton addButton = new JButton("Add");
            addButton.setBounds(350, 50, 100, 20);

            JButton removeButton = new JButton("Remove");
            removeButton.setBounds(500, 50, 100, 20);

            websiteInfo.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        usernameInfo.requestFocus();
                        e.consume();
                    }
                }
            });
            usernameInfo.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        passwordInfo.requestFocus();
                        e.consume();
                    }
                }
            });

            removeButton.addActionListener((new ActionListener() { //listener to remove entries
                @Override
                public void actionPerformed(ActionEvent ae) {
                    int row = jt.getSelectedRow();
                    String valueWebsite = (String) jt.getValueAt(row, 0);
                    String valueUsername = (String) jt.getValueAt(row, 1);
                    String valuePassword = (String) jt.getValueAt(row, 2);
                    db.removeFromUser(user, login, valueWebsite, valueUsername, valuePassword);
                    DefaultTableModel tableModel1 = new DefaultTableModel(db.getData(user), column) {
                        //here tableModel is overwritten with new tableMode l
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            //all cells uneditable
                            return false;
                        }
                    };
                    jt.setModel(tableModel1); //resetting table model. This shows the updated table
                }
            }));

            addButton.addActionListener(new ActionListener() { //listener for adding new entries to sql table
                @Override
                public void actionPerformed(ActionEvent ae) {
                    String website = websiteInfo.getText();
                    String userinfo = usernameInfo.getText();
                    String pass = passwordInfo.getText();
                    db.addToUser(user, login, website, userinfo, pass);
                    websiteInfo.setText("");
                    usernameInfo.setText("");
                    passwordInfo.setText("");
                    DefaultTableModel tableModel1 = new DefaultTableModel(db.getData(user), column) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            //all cells uneditable
                            return false;
                        }
                    };
                    jt.setModel(tableModel1); //resetting table model. This shows the updated table
                }
            });

            add(infoLabel);
            add(websiteLabel);
            add(websiteInfo);
            add(usernameLabel);
            add(usernameInfo);
            add(passwordLabel);
            add(passwordInfo);
            add(addButton);
            add(removeButton);
            pack(); //packing contents
            setLocationRelativeTo(null); //open centered
            setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(this, "Something went wrong");
            System.exit(1);
        }

    }
    public void setActionMap() {
        jt.getActionMap().put("copy", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String cellValue = jt.getModel().getValueAt(jt.getSelectedRow(), jt.getSelectedColumn()).toString();
                StringSelection stringSelection = new StringSelection(cellValue);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
            }
        });
    }
}
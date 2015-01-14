package Funtikova.Katya.FinancialManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author fun-cat@rambler.ru
 */
public class UserView extends JFrame {
    JLabel loginLabel;
    JLabel passwordLabel;
    JComboBox<String> loginComboBox;
    JTextField passwordTextField;
    JButton okButton;
    FinancialManager fm;

    public boolean checkPassword(String password, String realPassword){
        String encodedPassword = new Encryption(new Md5Strategy()).encode(password);
        return encodedPassword.equalsIgnoreCase(realPassword);
    }

    UserView(String titul){
        super(titul);
        fm = new FinancialManager();

        loginLabel = new JLabel("login");
        passwordLabel = new JLabel("password");
        loginComboBox = new JComboBox<String>();
        passwordTextField = new JPasswordField("password");
        okButton = new JButton("OK");

        for (String login : fm.getUserNames())
            loginComboBox.addItem(login);

        loginComboBox.setEditable(true);

        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        add(loginLabel);
        add(loginComboBox);
        add(passwordLabel);
        add(passwordTextField);
        add(okButton);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userLogin = (String) loginComboBox.getSelectedItem();
                User user = UserView.this.fm.getUser(userLogin);
                if (user == null) {
                    String userPassword = JOptionPane.showInputDialog(UserView.this, "<html><h2>Пользователя " + userLogin + " нет в базе." + "\n" + "Добавить?", "Придумайте пароль");
                    if (userPassword == null)
                        return;
                    User newUser = new User(userLogin, userPassword, userLogin);
                    fm.addUser(newUser);
                    return;
                } else if (!checkPassword(passwordTextField.getText(), user.password)) {
                    JOptionPane.showMessageDialog(UserView.this, "<html><h2>Не верный пароль");
                } else {
                    fm.user = user;
                    MainView mainView = new MainView("Financial manager: " + user.login, UserView.this.fm);
                    mainView.pack();
                    mainView.setSize(700, 500);
                    mainView.setLocation(400, 200);
                    mainView.setVisible(true);
                    //Funtikova.Katya.FinancialManager.UserView.this.setVisible(false);
                    UserView.this.dispose();
                }
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setSize(250,140);
        setLocation(400, 200);
        //setResizable(false);
        setVisible(true);

    }
}

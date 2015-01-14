package Funtikova.Katya.FinancialManager;

/**
 * Created by user on 03.08.14.
 */
public class User {
    String login;
    String name;
    String password;

    public User(String login, String password, String name) {
        if (login == null) login = "";
        if (password == null) password = "";
        if (name == null) name = login;

        this.login = login;
        this.password = password;
        this.name = name;
    }

    public void print(){
        System.out.printf("login: %s, password: %s, name: %s", login, password, name);
    }

}

package Funtikova.Katya.FinancialManager;

import java.sql.*;
import java.util.*;

/**
 * @author fun-cat@rambler.ru
 */


public class FinancialManager implements DataStore {

    DbHelper dbHelper = DbHelper.getInstance();
    User user;
    List<Account> accounts;
    List<Category> categories;

    //конструктор
    FinancialManager(String userLogin){
        user = getUser(userLogin);
    }

    //конструктор
    FinancialManager(){
    }

    // return null if no such user
    public User getUser(String name){
        try (PreparedStatement pStmt = dbHelper.connection.prepareStatement("SELECT * FROM User WHERE login=?")){
            pStmt.setString(1, name);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()){
                User user = new User(rs.getString("login"), rs.getString("password"), rs.getString("name"));
                return user;
            }
        }catch (SQLException e){
            System.out.println("getUser: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
        return null;
    }
    // If no users, return empty collection (not null)
    public Set<String> getUserNames(){
        Set<String> result = new HashSet<String>();
        try {
            Statement st = dbHelper.connection.createStatement();
            ResultSet rs = st.executeQuery("select login from user");
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
        return result;

    }
    // If no accounts, return empty collection (not null)
    public Set<Account> getAccounts(User owner){
        Set<Account> result = new HashSet<>();
        try {
            PreparedStatement pst = dbHelper.connection.prepareStatement("select accountID, name, rest from account where userLogin=?");
            pst.setString(1, owner.login);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                Account account = new Account(rs.getInt(1), rs.getString(2), rs.getFloat(3));
                for (Record record: getRecords(account)) {
                    account.records.add(record);
                }
                result.add(account);
            }
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
        return result;
    }
    // If no categories, return empty collection (not null)
    public Set<Category> getCategories(){
        Set<Category> result = new HashSet<>();
        try {
            Statement st = dbHelper.connection.createStatement();
            ResultSet rs = st.executeQuery("select categoryID, name from category");
            while (rs.next()){
                result.add(new Category(rs.getInt(1), rs.getString(2)));
            }
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
        return result;
    }


    // If no records, return empty collection (not null)
    public Set<Record> getRecords(Account account){
        Set<Record> result = new HashSet<>();
        PreparedStatement pst;
        try {
            pst = dbHelper.connection.prepareStatement(
                        "select r.recordID, r.date, r.amount, r.description, r.categoryID, c.name " +
                          "from record r " +
                          "left join category c " +
                           " on c.categoryID=r.categoryID " +
                           "where r.accountID=? " +
                           "order by 2"
            );
            pst.setInt(1, account.id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                Category category = new Category(rs.getInt("categoryID"), rs.getString("name"));
                result.add(new Record(rs.getInt("recordID"), rs.getDate("date"), rs.getFloat("amount"), category, rs.getString("description")));
            }
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
        return result;
    }

    // If no records, return empty collection (not null)
    public Set<Record> getRecords(Account account, Category category){
        Set<Record> result = new HashSet<>();
        if (category==null) {
            return getRecords(account);
        } else try {
                    PreparedStatement pst = dbHelper.connection.prepareStatement(
                        "select r.recordID, r.date, r.amount, r.description " +
                                "from record r " +
                                "join category c " +
                                "on r.accountID=? " +
                                "and r.categoryID=? " +
                                "and c.categoryID=r.categoryID"
                    );
                    pst.setInt(1, account.id);
                    pst.setInt(2, category.id);
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()){
                        result.add(new Record(rs.getInt("recordID"), rs.getDate("date"), rs.getFloat("amount"), category, rs.getString("description")));
                    }
                    return result;
            }catch (SQLException e){
                System.out.println(e.getClass().getName() + " " + e.getMessage());
            }
        return result;
    }

    public void addUser(User user){
        String encodedPassword = new Encryption(new Md5Strategy()).encode(user.password);
        try {
            PreparedStatement pstInsert = dbHelper.connection.prepareStatement("insert into user(login, password, name) values (?, ?, ?)");
            pstInsert.setString(1, user.login);
            pstInsert.setString(2, encodedPassword);
            pstInsert.setString(3, user.name);
            pstInsert.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
    }

    public void addAccount(User user, Account account) throws SQLException {
        try {
            ResultSet rs;
            PreparedStatement pstInsert = dbHelper.connection.prepareStatement("insert into account(name, userLogin, rest) values (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pstInsert.setString(1, account.name);
            pstInsert.setString(2, user.login);
            pstInsert.setFloat(3, account.rest);

            pstInsert.executeUpdate();
            rs = pstInsert.getGeneratedKeys();

            if (rs.next())
                account.id = rs.getInt(1);

        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void addCategory(Category category) throws SQLException{
        try {
            ResultSet rs;
            PreparedStatement pstInsert = dbHelper.connection.prepareStatement("insert into category(name) values (?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pstInsert.setString(1, category.name);

            pstInsert.executeUpdate();
            rs = pstInsert.getGeneratedKeys();

            if (rs.next())
                category.id = rs.getInt(1);

        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void addRecord(Account account, Record record) throws SQLException {
        if (account.id > 0 && record.category.id>0)
            try {
                ResultSet rs;
                PreparedStatement pstInsert = dbHelper.connection.prepareStatement("insert into record(accountID, date, amount, categoryID, description ) values (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                pstInsert.setDouble(1, account.id);
                pstInsert.setDate(2, record.date);
                pstInsert.setFloat(3, record.amount);
                pstInsert.setDouble(4, record.category.id);
                pstInsert.setString(5, record.description);
                pstInsert.executeUpdate();
                rs = pstInsert.getGeneratedKeys();
                if (rs.next()) {
                    record.recordID = rs.getInt(1);
                    // добавить запись к счёту
                    account.records.add(record);
                    // изменить остаток на счёте
                    float rest = account.rest + record.amount;
                    PreparedStatement pstUpdate = dbHelper.connection.prepareStatement("update account set rest=? where accountID=?");
                    pstUpdate.setFloat(1, rest);
                    pstUpdate.setDouble(2, account.id);
                    pstUpdate.executeUpdate();
                    account.rest = rest;
                }
            } catch (SQLException e) {
                System.out.println(e.getClass().getName() + " " + e.getMessage());
                throw e;
            }
    }

    // Изменить счёт
    public boolean editAccount(Account account) throws SQLException {
        if (account.id == 0)
            return false;
        try{
            PreparedStatement pstUpdate = dbHelper.connection.prepareStatement("update account set name=? where accountID=?");
            pstUpdate.setString(1, account.name);
            pstUpdate.setInt(2, account.id);
            if (pstUpdate.executeUpdate()>0)
                return true;
        } catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    // Изменить категорию
    public boolean editCategory(Category category) throws SQLException {
        if (category.id == 0)
            return false;
        try{
            PreparedStatement pstUpdate = dbHelper.connection.prepareStatement("update category set name=? where categoryID=?");
            pstUpdate.setString(1, category.name);
            pstUpdate.setInt(2, category.id);
            if (pstUpdate.executeUpdate()>0)
                return true;
        } catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    // Изменить запись
    public boolean editRecord(Account account, Record record) throws SQLException {
        if (record.recordID==0)
            return false;
        try {
            // Найти старую сумму
            float oldRest = 0;
            PreparedStatement pstSelect = dbHelper.connection.prepareStatement("select Amount from record where recordID=?");
            pstSelect.setInt(1, record.recordID);
            ResultSet rs = pstSelect.executeQuery();
            if (rs.next()){
                oldRest = rs.getFloat("Amount");
            } else return false;

            PreparedStatement pstUpdate = dbHelper.connection.prepareStatement(
                    "update record " +
                       "set date = ?, " +
                           "amount = ?, " +
                           "categoryID = ?, " +
                           "description= ? " +
                     "where recordID = ?", PreparedStatement.RETURN_GENERATED_KEYS);
            pstUpdate.setDate(1, record.date);
            pstUpdate.setFloat(2, record.amount);
            pstUpdate.setInt(3, record.category.id);
            pstUpdate.setString(4, record.description);
            pstUpdate.setInt(5, record.recordID);
            if (pstUpdate.executeUpdate()>0) {
                // изменить остаток на счёте
                float rest = account.rest - oldRest + record.amount;
                PreparedStatement pstUpdate1 = dbHelper.connection.prepareStatement("update account set rest=? where accountID=?");
                pstUpdate1.setFloat(1, rest);
                pstUpdate1.setDouble(2, account.id);
                pstUpdate1.executeUpdate();
                account.rest = rest;
                // изменить запись у account
                for (Record rec: account.records) {
                    if (rec.recordID==record.recordID) {
                        rec.date = record.date;
                        rec.amount = record.amount;
                        rec.stringDate = record.stringDate;
                        rec.category = record.category;
                        rec.description = record.description;
                        break;
                    }
                }
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
            //return false;
        }
        return false;
    }

    // return removed User or null if no such user
    public User removeUser(String name){
        User user = getUser(name);
        if (user==null)
            return null;
        try {
            PreparedStatement pstDelete = dbHelper.connection.prepareStatement("delete user where login=?");
            pstDelete.setString(1, name);
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
            return null;
        }
        return user;
    }

    // return null if no such account
    public Account removeAccount(User owner, Account account){
        try {
            PreparedStatement pstDeleteAccount = dbHelper.connection.prepareStatement("delete from account where userLogin=? and accountID=?");
            pstDeleteAccount.setString(1, owner.login);
            pstDeleteAccount.setInt(2, account.id);
            int rowCount = pstDeleteAccount.executeUpdate();
            if (rowCount == 0) return null;
            //удалить все записи с этим счётом
            PreparedStatement pstDeleteRecords = dbHelper.connection.prepareStatement("delete from record where accountID=?");
            pstDeleteRecords.setInt(1, account.id);
            pstDeleteRecords.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
            return null;
        }
        return account;
    }

    // return null if no such category
    public Category removeCategory(Category category) {
        try {
            PreparedStatement pstDeleteCategory = dbHelper.connection.prepareStatement(
                    "delete from category " +
                    "where categoryID=? " +
                      "and not exists(select r.recordID " +
                                         "from record r " +
                                          "where r.categoryID = category.CategoryID)");
            pstDeleteCategory.setInt(1, category.id);
            int rowCount = pstDeleteCategory.executeUpdate();
            if (rowCount == 0) return null;
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
            return null;
        }
        return category;
    }

    // return null if no such record
    public Record removeRecord(Account from, Record record){
        try {
            PreparedStatement pstDelete = dbHelper.connection.prepareStatement("delete from record where accountID=? and recordID=?");
            pstDelete.setInt(1, from.id);
            pstDelete.setInt(2, record.recordID);
            int rowCount = pstDelete.executeUpdate();
            if (rowCount==0) return null;
            //обновить счёт
            from.records.remove(record);
            // изменить остаток на счёте
            float rest = from.rest - record.amount;
            PreparedStatement pstUpdate = dbHelper.connection.prepareStatement("update account set rest=? where accountID=?");
            pstUpdate.setFloat(1, rest);
            pstUpdate.setDouble(2, from.id);
            pstUpdate.executeUpdate();
            from.rest = rest;
        }catch (SQLException e){
            System.out.println(e.getClass().getName() + " " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return record;

    }

//-----------------------------------------------------------------------------------------------------------


}

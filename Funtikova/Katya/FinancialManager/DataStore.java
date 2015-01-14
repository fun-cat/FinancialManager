package Funtikova.Katya.FinancialManager; /**
 * Created by user on 03.08.14.
 */

import java.sql.SQLException;
import java.util.*;

public interface DataStore {
    // return null if no such user
    User getUser(String name);
    // If no users, return empty collection (not null)
    Set<String> getUserNames();
    // If no accounts, return empty collection (not null)
    Set<Account> getAccounts(User owner);
    // If no records, return empty collection (not null)
    Set<Record> getRecords(Account account);
    // If no categories, return empty collection (not null)
    public Set<Category> getCategories();
    void addUser(User user);
    void addAccount(User user, Account account) throws SQLException;
    void addRecord(Account account, Record record) throws SQLException;
    void addCategory(Category category) throws SQLException;
    // return removed Funtikova.Katya.FinancialManager.User or null if no such user
    User removeUser(String name);
    // return null if no such account
    Account removeAccount(User owner, Account account);
    // return null if no such record
    Record removeRecord(Account from, Record record);
}

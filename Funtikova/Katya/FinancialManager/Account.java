package Funtikova.Katya.FinancialManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 03.08.14.
 */
public class Account {
    Integer id;
    String name;
    float rest;
    List<Record> records;

    public static final Account emptyAccount = new Account();

    Account(){
        id = 0;
        name ="";
        rest = 0;
        records = new ArrayList<Record>();
    }

    Account(Integer ID, String name, float rest){
        this.id = ID;
        this.name = name;
        this.rest = rest;
        records = new ArrayList<Record>();
    }
    public void print(){
        System.out.printf("Accont: %d, %s, rest: %f", id, name, rest);
        System.out.println();
        if (records!=null) {
            for (Record r : records) {
                r.print();
            }
        }
    }
}

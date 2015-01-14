package Funtikova.Katya.FinancialManager;

import java.util.Comparator;

/**
 * @author fun-cat@rambler.ru
 */

public class SortRecordByDate implements Comparator <Record>{

    @Override
    public int compare(Record obj1, Record obj2) {
        System.out.printf("Date1 =  %", obj1.date.toString());
        System.out.printf("Date2 =  %", obj2.date.toString());
        System.out.println("!!!!");
        return obj1.date.toString().compareTo(obj2.date.toString());
    }

    public boolean equals(String obj) {
        return false;
    }
}
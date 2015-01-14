package Funtikova.Katya.FinancialManager;

import java.util.Comparator;

/**
 * @author fun-cat@rambler.ru
 */
public class SortRecordByAmount implements Comparator<Record> {

    @Override
    public int compare(Record obj1, Record obj2) {
        return (int) (obj1.amount - obj2.amount);
    }

    public boolean equals(String obj) {
        return false;
    }
}


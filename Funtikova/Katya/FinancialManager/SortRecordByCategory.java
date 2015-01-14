package Funtikova.Katya.FinancialManager;

import java.util.Comparator;

/**
 * @author fun-cat@rambler.ru
 */
public class SortRecordByCategory implements Comparator<Record> {

    @Override
    public int compare(Record obj1, Record obj2) {
        return obj1.category.name.compareTo(obj2.category.name);
    }

    public boolean equals(Record obj) {
        return false;
    }
}

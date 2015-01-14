package Funtikova.Katya.FinancialManager;

import java.util.Comparator;

/**
 * @author fun-cat@rambler.ru
 */
public class SortRecordByDescription implements Comparator<Record> {

    @Override
    public int compare(Record obj1, Record obj2) {
        return obj1.description.compareTo(obj2.description);
    }

    public boolean equals(Record obj) {
        return false;
    }
}


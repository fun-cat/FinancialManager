package Funtikova.Katya.FinancialManager;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author fun-cat@rambler.ru
 */
public class Record {
    Integer recordID;
    Date date; // дата и время
    String stringDate; //дата и время как строка
    float amount; // сумма
    Category category;
    String description;
    // Формат даты и времени
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    //пустая запись
    public static final Record emptyRecord = new Record();

    Record(){
        category = new Category(0, "");
        stringDate = "";
        recordID = 0;
        amount = 0;
        description = "";
        date = new Date(0);
    }

    Record(Integer recordID, Date date, float amount, Category category, String description) {
        this.recordID = recordID;
        this.date = date;
        this.stringDate = dateFormat.format(date);
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    Record(Integer recordID, String date, float amount, Category category, String description) throws ParseException {
        try {
            this.recordID = recordID;
            this.date = new java.sql.Date(dateFormat.parse(date).getTime());
            this.stringDate = date;
            this.amount = amount;
            this.category = category;
            this.description = description;
        } catch (ParseException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void setDate(String stringDate) throws ParseException {
        try {
            this.stringDate = stringDate;
            this.date.setTime(dateFormat.parse(stringDate).getTime());
        } catch (ParseException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void setDate(Date date) {
        this.date = date;
        this.stringDate = dateFormat.format(date);
    }

    public void print(){
        System.out.printf("date: %s, amount: %f, category: %s, description: %s", date, amount, category.name, description);
    }

}

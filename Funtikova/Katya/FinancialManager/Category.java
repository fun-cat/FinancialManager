package Funtikova.Katya.FinancialManager;

/**
 * Created by user on 05.08.14.
 */
public class Category {
    int id;
    String name;
    public static final Category emptyCategory = new Category();

    Category(){
        id = 0;
        name = "";
    }
    Category(int id, String category){
        this.id = id;
        this.name = category;
    }
}

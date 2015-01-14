package Funtikova.Katya.FinancialManager;

import java.security.NoSuchAlgorithmException;

/**
 * Created on 13/08/14.
 */
public class Encryption {
    private Strategy strategy;

    public Encryption(Strategy strategy) {
        this.strategy = strategy;
    }

    public String encode(String str)  {
        try {
            return strategy.algorithm(str);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return "error occurred";
        }

    }
}

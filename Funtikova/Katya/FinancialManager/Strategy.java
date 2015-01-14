package Funtikova.Katya.FinancialManager;

import java.security.NoSuchAlgorithmException;

/**
 * Created on 13/08/14.
 */
public interface Strategy {
    public String algorithm(String str) throws NoSuchAlgorithmException;
}

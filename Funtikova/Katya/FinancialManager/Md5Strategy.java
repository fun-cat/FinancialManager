package Funtikova.Katya.FinancialManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created on 13/08/14.
 */
public class Md5Strategy implements Strategy {
    @Override
    public String algorithm(String str) throws NoSuchAlgorithmException {
        byte hash[];
        StringBuffer buf;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hash = md.digest(str.getBytes());
            buf = new StringBuffer(hash.length * 2);
            int i;
            for (i = 0; i < hash.length; i++) {
                if ((hash[i] & 0xff) < 0x10)
                    buf.append("0");
                buf.append(Long.toString(hash[i] & 0xff, 16));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
    }
}

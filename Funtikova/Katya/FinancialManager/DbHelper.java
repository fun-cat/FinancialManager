package Funtikova.Katya.FinancialManager;

import java.io.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {
    public Connection connection;
    private static final DbHelper instance = new DbHelper();

    private DbHelper(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:fm.db");
            if (!isTablesExist()) {
                createTables();
                insertTables();
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static DbHelper getInstance(){
        return instance;
    }

    public String convertStreamToString(InputStream is)
            throws IOException {
            /*
             * To convert the InputStream to String we use the
             * Reader.read(char[] buffer) method. We iterate until the
    35.         * Reader return -1 which means there's no more data to
    36.         * read. We use the StringWriter class to produce the string.
    37.         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try
            {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1)
                {
                    writer.write(buffer, 0, n);
                }
            }
            finally
            {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    String readResource(Class cpHolder, String path) throws Exception {
        String result;
        java.net.URL url = cpHolder.getResource(path);
//       System.out.println(url.toString());
         try {
            //java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
            //result = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
             result = convertStreamToString(cpHolder.getResourceAsStream(path));//IOUtils. getClass().getResourceAsStream(path);
            return result;
//        } catch (URISyntaxException e) {
//         //   System.out.println("URISyntaxException");
//            System.out.println(e.getReason());
//            e.printStackTrace();
//            throw e;
//        } catch (IOException e) {
//        //    System.out.println("IOException");
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//             throw e;
//        }
         } catch (Exception e) {
             System.out.println(e.getMessage());
             e.printStackTrace();
             throw e;
         }
        //return "";
    }

    boolean isTablesExist(){
        String   catalog          = null;
        String   schemaPattern    = null;
        String   tableNamePattern = null;
        //String[] types            = null;
        ResultSet tablesResultSet;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            tablesResultSet = metaData.getTables(catalog, schemaPattern, tableNamePattern,
                    new String[]{"TABLE"});

            if (!tablesResultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return true;
    }

    void createTables() {
//        System.out.println(getClass().getProtectionDomain().getCodeSource().getLocation());

        try  {
            Statement statement = connection.createStatement();
//            System.out.println(Funtikova.Katya.FinancialManager.DbHelper.class);
            String createSql = readResource(DbHelper.class, new String("/resources/fm_create.sql"));
            System.out.println(createSql);
            statement.executeUpdate(createSql);
        } catch (SQLException e) {
            System.out.println("createTables sqlex");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("createTables ex");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
    }
}

    void insertTables() {
        try {
            Statement statement = connection.createStatement();
            String insertSql = readResource(DbHelper.class, "/resources/fm_insert.sql");
            statement.executeUpdate(insertSql);
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

}

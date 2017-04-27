package util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import entity.Gold;
import trigger.CoinThresholdTrigger;
import trigger.TriggerCaller;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;


/**
 * Created by neshati on 1/24/2017.
 * Behpardaz
 */
public class DBHelper {

    private static  int MIN_POOL_SIZE = 5;
    private static  int Acquire_Increment = 5;
    private static  int MAX_POOL_SIZE = 20;
    ComboPooledDataSource cpds = new ComboPooledDataSource();
    Properties prop = new Properties();



    private String user;// = "sa";
    private String password;//
    private String dbName;// = "BPJ_SDP_MS_Currency";
    private String host;// = "172.16.4.199";
    private String port;// = "1433";
    private String driverName;// = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private String connectionString;// = "jdbc:sqlserver://" +
            //host +
            //"\\SQLEXPRESS:" +
            //port +
            //";databaseName=" +
            //dbName +
            //";" ;
/*
            +
            "user=" +
            user +
            ";" +
            "password=" +
            password;
*/

    private void init(){
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            MIN_POOL_SIZE = Integer.parseInt(prop.getProperty("MIN_POOL_SIZE"));
            Acquire_Increment = Integer.parseInt(prop.getProperty("Acquire_Increment"));
            MAX_POOL_SIZE = Integer.parseInt(prop.getProperty("MAX_POOL_SIZE"));
            user= prop.getProperty("user");
            password= prop.getProperty("password");
            dbName = prop.getProperty("dbName");
            host = prop.getProperty("host");
            port = prop.getProperty("port");
            driverName = prop.getProperty("driverName");

            connectionString = "jdbc:sqlserver://" +
                    host +
                    "\\SQLEXPRESS:" +
                    port +
                    ";databaseName=" +
                    dbName +
                    ";" ;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initConnectionPooling(){
        try {
            cpds.setDriverClass(driverName); //loads the jdbc driver
            cpds.setJdbcUrl(connectionString);
            cpds.setUser(user);
            cpds.setPassword(password);
            cpds.setMinPoolSize(MIN_POOL_SIZE);
            cpds.setAcquireIncrement(Acquire_Increment);
            cpds.setMaxPoolSize(MAX_POOL_SIZE);
        }
        catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }
    private Connection getConnection() {
        try {

            return cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static DBHelper dbHelper;

    public static DBHelper getInstance() {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
            dbHelper.init();
            dbHelper.initConnectionPooling();
        }
        return dbHelper;
    }

    private DBHelper() {
    }






    public int insertCoinTreshhold(CoinThresholdTrigger coinThresholdTrigger) {
        Connection conn = getConnection();
        try {
            assert conn != null;
            PreparedStatement statement
                    = conn.prepareStatement("INSERT INTO cointhreshold  VALUES(?,?,?,?)");
            Calendar cal = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
            statement.setString(1, coinThresholdTrigger.coinType);
            statement.setDouble(2, coinThresholdTrigger.treshold);
            statement.setInt(3, coinThresholdTrigger.goUpper);
            statement.setTimestamp(4, timestamp);
            int result = statement.executeUpdate();
            statement.close();
            conn.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Map<String,Gold> loadLastCoinPrice() {

        HashMap<String, Gold> out = new HashMap();
        String sql = "SELECT id, coinName, value FROM coinValue " +
                "WHERE (id IN (SELECT MAX(id) FROM coinValue GROUP BY coinName))";
        Connection connection = getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String coinName = resultSet.getString("coinName");
                double value = resultSet.getDouble("value");
                Gold gold = new Gold(null, coinName, value,null);
                out.put(coinName, gold);
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }




    public ArrayList<TriggerCaller> generateLowerCoinThresholdTriggers(String englishName, Double oldPrice, Double newPrice) {
        Connection conn = getConnection();
        ArrayList<TriggerCaller> out = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT " +
                    "      [coinType]\n" +
                    "      ,[VALUE]\n" +
                    "      ,[goUpper]\n" +
                    "  FROM [cointhreshold]\n" +
                    "  WHERE [goUpper] =  "+ CoinThresholdTrigger.GODOWN + " AND (VALUE BETWEEN ? AND ?) AND [coinType] = ?";
            PreparedStatement statement
                    = conn.prepareStatement(sql);

            statement.setDouble(1, newPrice);
            statement.setDouble(2, oldPrice);
            statement.setString(3, englishName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CoinThresholdTrigger ct = new CoinThresholdTrigger(
                        resultSet.getString("coinType"),
                        resultSet.getDouble("value"),
                        resultSet.getInt("goUpper"),null);
                out.add(ct);
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    public ArrayList<TriggerCaller> generateUpperCoinThresholdTriggers(String englishName, Double oldPrice, Double newPrice) {
        Connection conn = getConnection();
        ArrayList<TriggerCaller> out = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT " +
                    "      [coinType]\n" +
                    "      ,[VALUE]\n" +
                    "      ,[goUpper]\n" +
                    "  FROM [cointhreshold]\n" +
                    "  WHERE [goUpper] =  "+ CoinThresholdTrigger.GOUP + " AND (VALUE BETWEEN ? AND ?) AND [coinType] = ?";
            PreparedStatement statement
                    = conn.prepareStatement(sql);

            statement.setDouble(1, oldPrice);
            statement.setDouble(2, newPrice);
            statement.setString(3, englishName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CoinThresholdTrigger ct = new CoinThresholdTrigger(
                        resultSet.getString("coinType"),
                        resultSet.getDouble("value"),
                        resultSet.getInt("goUpper"),null);
                out.add(ct);
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }



    public int insertCoin(Gold gold) {
        if (gold.englishName == null)
            return -1;
        Connection conn = getConnection();

        try {
            PreparedStatement statement
                    = conn.prepareStatement("INSERT INTO coinValue  VALUES(?,?,?,?)");
            Calendar cal = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
            statement.setString(1, gold.englishName);
            statement.setDouble(2, gold.price);
            statement.setDouble(3, gold.realPrice);
            statement.setTimestamp(4, timestamp);
            int result = statement.executeUpdate();
            statement.close();
            conn.close();
            return result;
        } catch (Exception e) {
            try {
                assert conn != null;
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }
}

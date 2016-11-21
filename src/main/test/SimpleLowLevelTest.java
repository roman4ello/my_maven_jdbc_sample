import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.*;

public class SimpleLowLevelTest {

    final static String url = "jdbc:mysql://192.168.0.102:6603/warehouse?useSSL=false&";
    final static String password = "12345";
    final static String login = "root";

    @Test
    public void testItemCRUD() {
        String testData = "test address";
        Integer itemId = insertItem(testData, 1l);

        //TODO use findBy to get item from DB
        // add assert to check that "testData" is present in response
        String answerData = getItemByID(itemId);
        System.out.println("answerData = "+answerData);
        Assert.assertEquals(testData,answerData);

        System.out.printf("ItemID: " + itemId);
        delete(itemId);
    }


    private String getItemByID(Integer itemId) {
        Connection connection = null;
        final String MY_SQL_SELECT = "SELECT * FROM items WHERE id=?";
        String answerData = null;

        try {
            connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement(MY_SQL_SELECT);
            statement.setInt(1,itemId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            answerData = resultSet.getString( "name" );

        } catch (SQLException e) { e.printStackTrace(); }
        finally {
            try { connection.close(); }
            catch (SQLException e) {  e.printStackTrace(); }
        }

     return answerData ;
    }


    public Integer insertItem(String itemName, Long wireHouseId) {
        Connection connection = null;
        final String SQL_INSERT = "insert into " + "items" + " (" + "name" + ", " + "warehouse_id" + ") values (?, ?)";
        Integer itemId = null;

        try {
            connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, itemName);
            statement.setLong(2, wireHouseId);
            statement.execute();

            ResultSet generatedkeys = statement.getGeneratedKeys();
            if (generatedkeys.next()) {
                itemId = generatedkeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return itemId;
    }

    public void delete(Integer itemId) {
        Connection connection = null;
        final String SQL_DELETE = "delete from " + "items" + " where " + "id" + " = ?";

        try {
            connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
            statement.setLong(1, itemId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

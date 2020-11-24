package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class CoolJDBC {

    public static Connection connect() {
        System.out.println(Main.url);
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(Main.url);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createTable() {
            try (Statement statement = connect().createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void insertNewCard(String cardNumber, String pinCode) {
        String insert = "INSERT INTO card (number, pin) VALUES (?, ?)";

            try (PreparedStatement insertStatement = connect().prepareStatement(insert)) {
                insertStatement.setString(1, cardNumber);
                insertStatement.setString(2, pinCode);
                insertStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void addIncome(int income, String cardNumber) {
        String addIncome = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (PreparedStatement addIncomeStatement = connect().prepareStatement(addIncome)) {
            addIncomeStatement.setInt(1, income);
            addIncomeStatement.setString(2, cardNumber);
            addIncomeStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Card getAccountDetails(String cardNumber) {
        Card accountDetails = new Card();
        try (Statement statement = connect().createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM card WHERE " +
                    "number = " + cardNumber)) {
                     accountDetails.setCardNumber(resultSet.getString("number"));
                     accountDetails.setPinCode(resultSet.getString("pin"));
                     accountDetails.setAccountBalance(resultSet.getInt("balance"));
            } catch (SQLException e) {
                accountDetails.setCardNumber("1");
                return accountDetails;
            } } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountDetails;
    }

    public static void deleteAccount(String cardNumber) {
        String deleteAccount = "DELETE FROM card WHERE number = ?";

        try (PreparedStatement deleteStatement = connect().prepareStatement(deleteAccount)) {
            deleteStatement.setString(1, cardNumber);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getBalance(String cardNumber) {
        String getBalance = "SELECT balance FROM card WHERE number = ?";
        int cardBalance = 0;
        try (PreparedStatement balance = connect().prepareStatement(getBalance)) {
            balance.setString(1, cardNumber);
            balance.executeQuery();
            ResultSet resultSet = balance.executeQuery();
            cardBalance = resultSet.getInt("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cardBalance;
    }

    public static void doTransfer(String cardNumber, String beneficiaryCardNumber, int amount) {
        String subtractAmount = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String addIncome = "UPDATE card SET balance = balance + ? WHERE number = ?";


        try (Connection connection = connect()) {
            connection.setAutoCommit(false);

            try (PreparedStatement subtract = connection.prepareStatement(subtractAmount);
                 PreparedStatement addIncomeStatement = connection.prepareStatement(addIncome)) {

                subtract.setInt(1, amount);
                subtract.setString(2, cardNumber);
                subtract.executeUpdate();

                addIncomeStatement.setInt(1, amount);
                addIncomeStatement.setString(2, beneficiaryCardNumber);
                addIncomeStatement.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        connection.rollback();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}

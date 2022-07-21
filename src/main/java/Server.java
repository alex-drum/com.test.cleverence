import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.ServerSocket;
import java.sql.*;

public class Server {
    private final int PORT = 8189;

    private static final String url = "jdbc:mysql://localhost/products";
    private static final String dbUser = "root";
    private static final String dbPassword = "haizi2011";
    private static final int COUNTS = 10;
    private static int counter = COUNTS;
    public static String fetchUserNames = "SELECT name FROM testDB.user";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public Server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started.");

            while (true) {
                System.out.println("Server is waiting for connection.");
                try (
                        Handler handler = new Handler(server);
                ) {
                    while (true) {
                        String request = handler.read();
                        if (request != null) {
                            System.out.println(request);
                            switch (request) {
                                case "/getProductName":
                                    String productName = getProductName(request, handler);
                                    handler.write(productName);
                                    break;
                                case "/createProductCard":
                                    createProductCard(handler);
                            }
                        }

                        /*switch (request) {
                            case "/signIn":
                                checkUser(handler);
                                signIn(handler);
                                break;
                            case "/logIn":
                                logIn(handler);
                                break;
                            case "/getPetsArray":
                                getPetsArray(handler);
                                break;
                            case "/getPetOnID":
                                getPetOnID(handler);
                                break;
                            case "/createNewPet":
                                createNewPet(handler);
                            case "/editPetInfo":
                                editPetInfo(handler);
                            case "/deletePet":
                                deletePet(handler);
                            case "/closeConnection":
                                System.out.println("Client closed connection");
                                break;
                        }*/
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createProductCard(Handler handler) {
        String jsonString = handler.read();
        System.out.println(jsonString);
        JsonObject productCard = new JsonParser().parse(jsonString).getAsJsonObject();
//        System.out.println(productCard.get("product_name"));
//        System.out.println(productCard.get("product_description"));
//        System.out.println(productCard.get("product_quantity"));
        String query = "INSERT INTO `products`.`stock` (`product_name`, `product_description`, `product_quantity`) VALUES ('"
                + productCard.get("product_name").getAsString() + "', '"
                + productCard.get("product_description").getAsString() + "', '"
                + productCard.get("product_quantity") + "')";
        System.out.println(query);
        insertQuery(query, handler);


}

    private void insertQuery(String query, Handler handler) {

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if (i == 1) {
                System.out.println("New product card is successfully created!");
//                handler.write("New pet successfully created!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getProductName(String request, Handler handler) {
        String productName = null;
        String barcode = handler.read();
        System.out.println(barcode);

        String query = "SELECT product_name FROM products.product_list WHERE (`barcode` = '" + barcode + "')";

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword );
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                productName = resultSet.getString("product_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productName;
    }
/*

    private void deletePet(Handler handler) {
        String petOwner = handler.read();
        JsonObject user = new JsonParser().parse(petOwner).getAsJsonObject();

        String query = "SELECT * FROM testDB.pet WHERE (`petOwner` = '" + user.get("petOwner").getAsString() + "')";
        JSONArray petsArray = getJSONArray(query);
        handler.write(petsArray.toString());

        String petJSONString = handler.read();
        JsonObject pet = new JsonParser().parse(petJSONString).getAsJsonObject();

        query = "DELETE FROM `testDB`.`pet` WHERE (`idPet` = '"
                + pet.get("idPet").getAsString() + "')";

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);

            if (i == 1) {
                System.out.println("Pet info is successfully deleted!");
                handler.write("Pet info is successfully deleted!");
            }
        } catch (SQLException e) {
            new MyException(e).print();
        }

    }

    private void editPetInfo(Handler handler) {
        String petOwner = handler.read();
        JsonObject user = new JsonParser().parse(petOwner).getAsJsonObject();

        String query = "SELECT * FROM testDB.pet WHERE (`petOwner` = '" + user.get("petOwner").getAsString() + "')";
        JSONArray petsArray = getJSONArray(query);
        handler.write(petsArray.toString());

        String editedPetString = handler.read();
        JsonObject editedPet = new JsonParser().parse(editedPetString).getAsJsonObject();

        query = "UPDATE `testDB`.`pet` SET `birthday` = '" +
                editedPet.get("birthday").getAsString() + "', `sex` = '" +
                editedPet.get("sex").getAsString() + "', `petName` = '" +
                editedPet.get("petName").getAsString() + "' WHERE (`idPet` = '" +
                editedPet.get("idPet").getAsString() + "')";

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);

            if (i == 1) {
                System.out.println("Pet info if successfully edited!");
                handler.write("Pet info if successfully edited!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNewPet(Handler handler) {
        String newPetString = handler.read();
        JsonObject newPetJSON = new JsonParser().parse(newPetString).getAsJsonObject();

        String query = "INSERT INTO `testDB`.`pet` " +
                "(`birthday`, `sex`, `petName`, `petOwner`) VALUES ('" +
                newPetJSON.get("birthday").getAsString() + "', '"
                + newPetJSON.get("sex").getAsString() +
                "', '" + newPetJSON.get("petName").getAsString() + "', '" +
                newPetJSON.get("petOwner").getAsString() + "')";

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if (i == 1) {
                System.out.println("New pet successfully created!");
                handler.write("New pet successfully created!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getPetOnID(Handler handler) {
        String petOwnerString = handler.read();
        JsonObject pet = new JsonParser().parse(petOwnerString).getAsJsonObject();

        String query = "SELECT * FROM testDB.pet WHERE (`idPet` = '" + pet.get("idPet").getAsString() + "')";
        JSONArray petsArray = getJSONArray(query);
        handler.write(petsArray.toString());
    }

    private void getPetsArray(Handler handler) {
        String petOwnerString = handler.read();
        JsonObject user = new JsonParser().parse(petOwnerString).getAsJsonObject();

        String query = "SELECT * FROM testDB.pet WHERE (`petOwner` = '" + user.get("petOwner").getAsString() + "')";
        JSONArray petsArray = getJSONArray(query);
        handler.write(petsArray.toString());
    }

    private static void logIn(Handler handler){
        String name = getUserName(handler);

        boolean isUserNameValid = validateOldUserName(name);
        if (isUserNameValid) {
            boolean isUserNotRestricted = checkIfUserIsNotRestricted(name);
            if (!isUserNotRestricted) {
                int minutesLeft = calcMinutesLeft(name);
                if (minutesLeft > 0) {
                    System.out.println("You have no attempts left. Please try again in " + minutesLeft + " minutes.");
                    handler.write("You have no attempts left. Please try again in " + minutesLeft + " minutes.");
                }
            }
        }

        boolean isPasswordValid = false;

        if (isUserNameValid) {
            System.out.println("Username is valid, please proceed with password.");
            handler.write("Username is valid, please proceed with password.");
            String password = getPassword(handler);
            long firstAttemptTime = System.currentTimeMillis();
            updateLogInTime(name, firstAttemptTime);

            while (!isPasswordValid && counter > 1) {
                isPasswordValid = checkPassword(name, password);
                if (!isPasswordValid) {
                    counter--;
                    System.out.println("Password is invalid, please try again.");
                    handler.write("Password is invalid, please try again (Attempts left: " + counter + ").");
                    password = getPassword(handler);
                }
            }
        } else {
            System.out.println("User nickname is invalid, please try again.");
            handler.write("User nickname is invalid, please try again.");
            logIn(handler);
        }

        if (isPasswordValid && counter > 0) {
            String query = "UPDATE `testDB`.`user` SET `isLogged` = '1', `LogInFirstAttemptTime` = NULL WHERE (`name` = '"
                    + name + "')";

            try {
                connection = DriverManager.getConnection(url, dbUser, dbPassword);
                statement = connection.createStatement();
                int i = statement.executeUpdate(query);
                if (i > 0) {
                    System.out.println("ROW UPDATED");
                    handler.write("Log-in successful");
                    counter = COUNTS;
                } else {
                    System.out.println("ROW NOT UPDATED");
                    handler.write("Log-in failed");
                }
            } catch (SQLException e) {
                new MyException(e).print();
            }
        } else {
            System.out.println("You have already used all attempts. Please try in 1 hour.");
            handler.write("You have already used all attempts. Please try in 1 hour.");
        }
    }

    private static boolean checkPassword(String name, String password) {
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT password FROM testDB.user WHERE (`name` = '" + name + "')");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            JSONObject user = new JSONObject();
            if (resultSet.next()) {
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    user.put(key, resultSet.getObject(key));
                }
            }
            if (password.equals(user.getString("password"))) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            new MyException(e).print();
        }
        return false;
    }

    private static int calcMinutesLeft(String name) {
        long minutesLeft = 0;
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT LogInFirstAttemptTime FROM testDB.user WHERE (`name` = '" + name + "')");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            JSONObject user = new JSONObject();
            if (resultSet.next()) {
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    user.put(key, resultSet.getObject(key));
                }
            }

            long firstAttemptTime = user.getLong("LogInFirstAttemptTime");
            long currentTime = System.currentTimeMillis();
            if (currentTime - firstAttemptTime < 3600000) {
                minutesLeft = 60 - TimeUnit.MILLISECONDS.toMinutes(currentTime - firstAttemptTime);
            }
        } catch (SQLException e) {
            new MyException(e).print();
        }
        Long l = new Long(minutesLeft);
        int intMinutesLeft = l.intValue();
        return intMinutesLeft;
    }

    private static boolean checkIfUserIsNotRestricted(String name) {
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT LogInFirstAttemptTime FROM testDB.user WHERE (`name` = '" + name + "')");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            JSONObject user = new JSONObject();
            if (resultSet.next()) {
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    user.put(key, resultSet.getObject(key));
                }
            }
            if (user.has("LogInFirstAttemptTime")) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            new MyException(e).print();
        }
        return false;
    }

    private static void updateLogInTime(String name, long firstAttemptTime) {
        String query = "UPDATE `testDB`.`user` SET `LogInFirstAttemptTime` = '" + firstAttemptTime + "' WHERE (`name` = '"
                + name + "')";
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
        } catch (SQLException e) {
            new MyException(e).print();
        }
    }

    private static String getPassword(Handler handler) {
        String userJSONString = handler.read();
        JsonObject user = new JsonParser().parse(userJSONString).getAsJsonObject();
        String password = user.get("password").getAsString();
        return password;
    }

    private static String getUserName(Handler handler) {
        String userJSONString = handler.read();
        JsonObject user = new JsonParser().parse(userJSONString).getAsJsonObject();
        String name = user.get("name").getAsString();
        return name;
    }

    private static void signIn(Handler handler) {
        String newUserJSONString = handler.read();
        JsonObject newUser = new JsonParser().parse(newUserJSONString).getAsJsonObject();

        String query = "insert into `testDB`.`user` (`name`, `password`, `isLogged`) VALUES " +
                "('" + newUser.get("name").getAsString() + "','"
                + newUser.get("password").getAsString()
                + "','" + newUser.get("isLogged") + "')";

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if (i > 0) {
                System.out.println("ROW INSERTED");
                handler.write("Check-in successful");
            } else {
                System.out.println("ROW NOT INSERTED");
                handler.write("Check-in failed");
            }
        } catch (SQLException e) {
            new MyException(e).print();
        }

    }

    private static void checkUser(Handler handler) {
        boolean isNameVacant = false;
        while (!isNameVacant) {
            String request = handler.read();
            System.out.println("Request: " + request);
            isNameVacant = validateNewUserName(request);
            String message = new Boolean(isNameVacant).toString();
            handler.write(message);
        }
    }

    private static boolean validateNewUserName(String name) {
        JSONArray users = getJSONArray(fetchUserNames);
        boolean flag = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (name.equals(user.getString("name"))) {
                flag = false;
                return flag;
            } else {
                flag = true;
            }
        }
        return flag;
    }

    private static boolean validateOldUserName(String name) {
        JSONArray users = getJSONArray(fetchUserNames);
        boolean flag = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (name.equals(user.getString("name"))) {
                flag = true;
                return flag;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    private static JSONArray getJSONArray(String query) {
        JSONArray jsonArray = new JSONArray();

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            int i = 0;
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    jsonObject.put(key, resultSet.getObject(key));
                }
                jsonArray.put(i, jsonObject);
                i++;
            }
        } catch (SQLException e) {
            new MyException(e).print();
        }
        return jsonArray;
    }
*/

}

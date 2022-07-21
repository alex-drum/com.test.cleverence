import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Client {
    private static final int PORT = 8189;
    private static final String IP = "localhost";

    public static void main(String[] args) {

        try (Handler handler = new Handler(IP, PORT))
        {
            System.out.println("Connected to server!");
            getMenu();
            String request = getInputString();
            switch (request) {
                case "1":
                    createProductCard(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createProductCard(Handler handler) {
        handler.write("/getProductName");
        System.out.println("Введите штрихкод:");
        String barcode = getInputString();
        handler.write(barcode);
        String productName = handler.read();
        System.out.println("Наименование продукта:" + productName);
        System.out.println("Введите кол-во:");
        int quantity = new Scanner(System.in).nextInt();
        System.out.println("Введите описание");
        String description = getInputString();

        JsonObject productCard = new JsonObject();
        productCard.addProperty("product_name", productName);
        productCard.addProperty("product_description", description);
        productCard.addProperty("product_quantity", quantity);

        handler.write("/createProductCard");
        handler.write(productCard.toString());



    }
    private static void getMenu () {
        System.out.println("Выберите код операции:\n" +
                            "Начать (1)\n" +
                            "Просмотр (2)\n" +
                            "Временно выйти (3)\n" +
                            "Завершить (4)\n");
    }
    private static String getInputString() {
        String input = null;
        try {
            input = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
}

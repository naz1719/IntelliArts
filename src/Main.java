import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        List<Product> list = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            System.out.print("Enter bash script: ");
            String bash = reader.readLine();
            String array[] = bash.split("\\s");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            if (array[0].equals("add")) {
                // Body
                try {
                    Date startDate = df.parse(array[1]);
                    Double amount = Double.valueOf(array[2]);
                    // Action
                    Product product = new Product(startDate, amount, array[3], array[4]);
                    list.add(product);
                } catch (ParseException e) {
                    System.out.println("Incorrect date, try like this: 'add 2017-04-27 3.44 USD Jogurt'");
                    e.printStackTrace();
                }
            } else if (array[0].equals("list")) {
                Collections.sort(list, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                list.forEach(System.out::println);
            } else if (array[0].equals("clear")) {
                Iterator<Product> iter = list.iterator();
                while (iter.hasNext()) {
                    Product product = iter.next();
                    if (product.date.equals(df.parse(array[1]))) {
                        iter.remove(); // Removes the 'current' item
                    }
                }
            } else if (array[0].equals("total")) {
                double total = 0;
                Iterator<Product> iter = list.iterator();
                while (iter.hasNext()) {
                    Product product = iter.next();
                    total = total + product.amount;
                }
                try {
                    JSONObject json = new JSONObject(readUrl("http://api.fixer.io/latest?base=USD&symbols=" + array[1]));
                    Double exchange = json.getJSONObject("rates").getDouble(array[1]);
                    System.out.println(array[1] + " Currency " + exchange);
                    System.out.println("Total " + exchange * total);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (array[0].equals("commands")) {
                System.out.println("add 2017-04-27 3.44 USD Jogurt ,   list,  clear 2017-04-27,  total EUR ");
            } else if (array[0].equals("exit")) {
                System.exit(0);
            }
        }
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    static class Product {
        private Date date;
        private Double amount;
        private String currency;
        private String name;

        Product(Date date, Double amount, String currency, String name) {
            this.date = date;
            this.amount = amount;
            this.currency = currency;
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "data='" + new SimpleDateFormat("yyyy-MM-dd").format(date) + '\'' +
                    ", amount=" + amount +
                    ", currency='" + currency + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}

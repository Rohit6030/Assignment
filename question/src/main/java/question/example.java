package question;

import java.sql.*;
import java.util.*;

public class example {
private Connection connection;

	
	public example(String dbUrl, String user, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.connection = DriverManager.getConnection(dbUrl, user, password);
			System.out.println("Database connected successfully!");
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Database connection failed: " + e.getMessage());
		}
	}

	public void createTables() {
		String productTable = "create table if not exists Product (" + "id INT AUTO_INCREMENT PRIMARY KEY, "+ "name VARCHAR(255), " + "type VARCHAR(255), " + "unit VARCHAR(255)" + ");";

		String subProductTable = "create table if not exists SubProduct (" + "id INT AUTO_INCREMENT PRIMARY KEY, "+ "product_id INT, " + "name VARCHAR(255), " + "unit VARCHAR(255), "+ "FOREIGN KEY (product_id) REFERENCES Product(id) ON DELETE CASCADE" + ");";

		try (Statement stmt = connection.createStatement()) {
			stmt.execute(productTable);
			stmt.execute(subProductTable);
			System.out.println("Tables created successfully!");
		} catch (SQLException e) {
			System.out.println("Error creating tables: " + e.getMessage());
		}
	}

	
	 public void addProduct(String name, String type, List<String> subUnits) {
	        try (Statement stmt = connection.createStatement()) {
	            // Insert product
	            String insertProduct = "insert into Product (name, type, unit) values ('" +name + "', '" + type + "', '" + subUnits.get(0) + "');";
	            stmt.executeUpdate(insertProduct);

	            // Retrieve last inserted ID
	            String getLastId = "select last_insert_ID() AS id;";
	            ResultSet rs = stmt.executeQuery(getLastId);

	            if (rs.next()) {
	                int productId = rs.getInt("id");

	                // Insert sub-products
	                for (String unit : subUnits) {
	                    String insertSubProduct = "INSERT INTO SubProduct (product_id, name, unit) VALUES (" +
	                            productId + ", '" + name + " " + unit + "', '" + unit + "');";
	                    stmt.executeUpdate(insertSubProduct);
	                }
	            }
	            System.out.println("Product and sub-products added successfully!");

	        } catch (SQLException e) {
	            System.err.println("Error adding product: " + e.getMessage());
	        }
	    }
	
	public void displayProducts() {
		String query = "select p.name AS productName, p.type AS productType, sp.name AS subProductName "+ "from Product p left join SubProduct sp on p.id = sp.product_id;";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				System.out.println();
				System.out.println("Product: " + rs.getString("productName") + ", Type: " + rs.getString("productType"));
				String subProduct = rs.getString("subProductName");
				if (subProduct != null) {
					System.out.println("  SubProduct: " + subProduct);
				}
			}
		} catch (SQLException e) {
			System.out.println("Error displaying products: " + e.getMessage());
		}
	}

	
	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
				System.out.println("Database connection closed.");
			}
		} catch (SQLException e) {
			System.out.println("Error closing connection: " + e.getMessage());
		}
	}

    public static void main(String[] args) {
    	try {
    		
			example manager = new example("jdbc:mysql://localhost:3306/testdb",  "root",  "root");

			manager.createTables();

		
			manager.addProduct("Sugar", "Solid", Arrays.asList("1Kg", "10Kg"));
			manager.addProduct("Tea", "Solid", Arrays.asList("200gm", "500gm", "1Kg"));
			manager.addProduct("Milk", "Liquid", Arrays.asList("1ltr", "500ml"));

			
			manager.displayProducts();

			
			manager.closeConnection();

		} catch (Exception e) {
			System.out.println("Unexpected error: " + e.getMessage());
		}
	}
}
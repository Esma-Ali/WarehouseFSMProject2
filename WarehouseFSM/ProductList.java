import java.util.*;
import java.io.*;

public class ProductList implements Serializable {
    private static final long serialVersionUID = 1L;
	private List<Product> products = new LinkedList<Product>();
	
	public ProductList() {
	}
	
	public boolean insertProduct(Product product) {
		products.add(product);
		return true;
	}
	
	public Iterator<Product> getProducts() {
		return products.iterator();
	}
	
	public Product findProduct(String productId) {
		Iterator<Product> i = products.iterator();
		while (i.hasNext()) {
			Product p = (Product) i.next();
			if (p.getId().equals(productId))
				return p;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return products.toString();
	}

}
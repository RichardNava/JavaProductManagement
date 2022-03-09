package labs.pm.app;

import java.math.BigDecimal;
import java.time.LocalDate;

import labs.pm.data.Drink;
import labs.pm.data.Food;
import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

public class Shop {

	public static void main(String[] args) {
		ProductManager pm = new ProductManager();
		
		Product p1 = pm.createProduct(101,"Tea",BigDecimal.valueOf(1.75),Rating.THREE_STAR);
		Product p2 = pm.createProduct(102,"Coffe",BigDecimal.valueOf(1.99),Rating.FOUR_STAR);
		Product p3 = pm.createProduct(103,"Cake",BigDecimal.valueOf(3.99),Rating.FIVE_STAR,LocalDate.now().plusDays(2));
		Product p4 = pm.createProduct(105,"Cookie",BigDecimal.valueOf(2.99),Rating.TWO_STAR,LocalDate.now().plusDays(1));
		Product p6 = pm.createProduct(104,"Chocolate", BigDecimal.valueOf(2.99),Rating.FIVE_STAR);
		Product p7 = pm.createProduct(104,"Chocolate", BigDecimal.valueOf(2.99),Rating.FIVE_STAR,LocalDate.now().plusDays(2));
		Product p5 = p3.applyRating(Rating.THREE_STAR);
		Product p8 = p4.applyRating(Rating.FIVE_STAR);
		Product p9 = p1.applyRating(Rating.TWO_STAR);
		
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		System.out.println(p5);
		System.out.println(p6.equals(p7));
		System.out.println(p8);
		System.out.println(p9);
		
		if (p3 instanceof Food) {
			System.out.println(((Food)p3).getBestBefore());
		}
		System.out.println(doubleChar("String"));
		
	}
	public static String doubleChar(String s) {
		String st = "";
		var a = s.toCharArray();
		for (int i = 0; i < a.length; i++) {
			st += (char)a[i];
			st += (char)a[i];
		}
		return st;
	}
}

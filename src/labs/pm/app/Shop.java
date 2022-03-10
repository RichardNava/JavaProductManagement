package labs.pm.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

public class Shop {

	public static void main(String[] args) {
		ProductManager pm = new ProductManager(Locale.FRANCE);
		
		Product p1 = pm.createProduct(101,"Tea",BigDecimal.valueOf(1.75),Rating.THREE_STAR);
		p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Nice hot cup of tea");
		p1 = pm.reviewProduct(p1, Rating.TWO_STAR, "Rather weak tea");
		p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Fine tea");
		p1 = pm.reviewProduct(p1, Rating.FIVE_STAR, "Perfect tea");
		p1 = pm.reviewProduct(p1, Rating.THREE_STAR, "Just add some lemon");
		p1 = pm.reviewProduct(p1, Rating.THREE_STAR, "Good tea");
		pm.printProductReport(p1);
		
		Product p2 = pm.createProduct(102,"Coffe",BigDecimal.valueOf(1.99),Rating.FOUR_STAR);
		p2 = pm.reviewProduct(p2, Rating.THREE_STAR, "Coffe was ok");
		p2 = pm.reviewProduct(p2, Rating.ONE_STAR, "Where is the milk?");
		p2 = pm.reviewProduct(p2, Rating.FIVE_STAR, "It´s perfect with ten spoons of sugar!");
		pm.printProductReport(p2);
		
		Product p3 = pm.createProduct(103,"Cake",BigDecimal.valueOf(3.99),Rating.FIVE_STAR,LocalDate.now().plusDays(2));
		p3 = pm.reviewProduct(p3, Rating.FOUR_STAR, "Very nice cake");
		p3 = pm.reviewProduct(p3, Rating.THREE_STAR, "It good, but I´ve expected more chocolate");
		p3 = pm.reviewProduct(p3, Rating.FIVE_STAR, "Perfect");
		p3 = pm.reviewProduct(p3, Rating.FIVE_STAR, "Awesome");
		pm.printProductReport(p3);
		
		Product p4 = pm.createProduct(105,"Cookie",BigDecimal.valueOf(2.99),Rating.TWO_STAR,LocalDate.now().plusDays(1));
		p4 = pm.reviewProduct(p4, Rating.FIVE_STAR, "Yummie");
		p4 = pm.reviewProduct(p4, Rating.THREE_STAR, "Ok");
		pm.printProductReport(p4);
		
		Product p5 = pm.createProduct(104,"Chocolate", BigDecimal.valueOf(2.65),Rating.FIVE_STAR);
		pm.printProductReport(p5);
		
	}

}

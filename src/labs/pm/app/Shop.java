package labs.pm.app;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;
import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.ProductManagerException;
import labs.pm.data.Rateable;
import labs.pm.data.Rating;
import labs.pm.data.Review;

@SuppressWarnings("unused")
public class Shop {

	public static void main(String[] args) {
		
		ProductManager pm = new ProductManager("es_ES");
		pm.printProductReport(101);
		pm.printProductReport(103);
		/*
		Product p1 = pm.createProduct(101,"Tea",BigDecimal.valueOf(1.75),Rating.THREE_STAR);

		p1 = pm.reviewProduct(101, Rateable.convert(5), "Rather weak tea");
		p1 = pm.reviewProduct(101, Rating.FOUR_STAR, "Fine tea");
		p1 = pm.reviewProduct(101, Rating.FIVE_STAR, "Perfect tea");
		p1 = pm.reviewProduct(101, Rating.THREE_STAR, "Just add some lemon");
		p1 = pm.reviewProduct(101, Rating.THREE_STAR, "Good tea");
		*/
		/*
		pm.parseProduct("D,101,Tea,1.75,0,2022-03-26");
		pm.printProductReport(101);
		try {
			pm.parseReview("101,4,Nice hot cup of tea");
			pm.parseReview("101,2,Rather weak tea");
			pm.parseReview("101,4,Fine tea");
			pm.parseReview("101,4,Good tea");
			pm.parseReview("101,5,Perfect tea");
			pm.parseReview("101,3,Just add some lemon");
		} catch (ProductManagerException e) {
			Throwable cause = e.getCause();
		}
		pm.parseReview("101,4,Nice hot cup of tea");
		pm.parseReview("101,2,Rather weak tea");
		pm.parseReview("101,4,Fine tea");
		pm.parseReview("101,4,Good tea");
		pm.parseReview("101,5,Perfect tea");
		pm.parseReview("101,3,Just add some lemon");
		pm.printProductReport(101);
		pm.parseProduct("F,103,Cake,3.99,0,2022-04-04");
		pm.printProductReport(103);
		*/
/*
		Product p2 = pm.createProduct(102,"Coffe",BigDecimal.valueOf(1.99),Rating.FOUR_STAR);
		p2 = pm.reviewProduct(102, Rating.THREE_STAR, "Coffe was ok");
		p2 = pm.reviewProduct(102, Rating.ONE_STAR, "Where is the milk?");
		p2 = pm.reviewProduct(102, Rating.FIVE_STAR, "It´s perfect with ten spoons of sugar!");
		pm.changeLocale("es-ES");
		//pm.printProductReport(102);
		
		Product p3 = pm.createProduct(103,"Cake",BigDecimal.valueOf(3.99),Rating.FIVE_STAR,LocalDate.now().plusDays(2));
		p3 = pm.reviewProduct(p3, Rating.FOUR_STAR, "Very nice cake");
		p3 = pm.reviewProduct(p3, Rating.THREE_STAR, "It good, but I´ve expected more chocolate");
		p3 = pm.reviewProduct(p3, Rating.FIVE_STAR, "Perfect");
		p3 = pm.reviewProduct(p3, Rating.FIVE_STAR, "Awesome");
		//pm.changeLocale("fr-FR");
		//pm.printProductReport(p3);
		
		Product p4 = pm.createProduct(104,"Cookie",BigDecimal.valueOf(2.99),Rating.TWO_STAR,LocalDate.now().plusDays(1));
		p4 = pm.reviewProduct(p4, Rating.FIVE_STAR, "Yummie");
		p4 = pm.reviewProduct(p4, Rating.THREE_STAR, "Ok");
		//pm.changeLocale("en-US");
		//pm.printProductReport(p4);
		
		Product p5 = pm.createProduct(105,"Chocolate", BigDecimal.valueOf(2.65),Rating.FIVE_STAR);
		//pm.changeLocale("it-IT");
		//pm.printProductReport(p5);
		
		pm.printProducts((pm1,pm2) -> pm2.getRating().ordinal() - pm1.getRating().ordinal());
		System.out.println("\n");
		Comparator<Product> ratingSorter = (pm1,pm2) -> pm1.getRating().ordinal() - pm2.getRating().ordinal();
		pm.printProducts(ratingSorter);
		System.out.println("\n");
		pm.printProducts((pm1,pm2) -> pm2.getPrice().compareTo(pm1.getPrice()));
		System.out.println("\n");
		Comparator<Product> priceSorter = (pm1,pm2) -> pm1.getPrice().compareTo(pm2.getPrice());
		pm.printProducts(priceSorter);
		System.out.println("\n");
		pm.printProducts(ratingSorter.thenComparing(priceSorter));
		System.out.println("\n");
		pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());
		System.out.println("\n");
		pm.printProducts(new Comparator<Product>(){
			@Override
			public int compare(Product o1, Product o2) {
				if (o1.getName().length()==o2.getName().length()) return 0;
				else if(o1.getName().length()>o2.getName().length()) return -1;
				else return 1;
			}		
		});
		System.out.println("\n");
		pm.printProductReport(106);
		System.out.println("\n");
		pm.printProducts((Product p) -> p.getPrice().floatValue()>2,ratingSorter);
		System.out.println("\n");
		pm.getDiscounts().forEach((rating,discount) -> System.out.println(rating+"\t"+discount));
		p2 = pm.reviewProduct(234, Rating.ONE_STAR, "Prueba");
	*/	
		pm.createProduct(164,"Kombucha", BigDecimal.valueOf(2.10), Rating.NOT_RATED);
		pm.reviewProduct(164,Rating.TWO_STAR, "Looks like tea but is it?");
		pm.reviewProduct(164,Rating.THREE_STAR, "Fine tea");
		pm.reviewProduct(164,Rating.FOUR_STAR, "Great, but this is not tea");
		pm.reviewProduct(164,Rating.FIVE_STAR, "Perfect");
		
		pm.dumpData();
		pm.restoreData();
		
		pm.printProductReport(164);
		pm.printProducts((Product p) -> p.getPrice().floatValue()>2,
				(pm1,pm2) -> pm2.getRating().ordinal() - pm1.getRating().ordinal());
		pm.getDiscounts().forEach((rating,discount) -> System.out.println(rating+"\t"+discount));
		
	}

}

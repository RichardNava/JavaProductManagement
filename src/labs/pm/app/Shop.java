package labs.pm.app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.CSVReader;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.ProductManagerException;
import labs.pm.data.Rateable;
import labs.pm.data.Rating;
import labs.pm.data.Review;

@SuppressWarnings("unused")
public class Shop {

	public static void main(String[] args){ //Throws -> propagar Throw -> Arrojar

		ProductManager pm = ProductManager.getInstance();			
		AtomicInteger clientCount = new AtomicInteger(0); 
		Callable<String> client = () -> {
			String clientId = "Client "+clientCount.incrementAndGet();
			String threadName = Thread.currentThread().getName();
			int productId = ThreadLocalRandom.current().nextInt(9)+101;
			String languageTag = ProductManager.getSupportedLocales()
											   .stream()
											   .skip(ThreadLocalRandom.current().nextInt(5))
											   .findFirst().get();
			StringBuilder log = new StringBuilder(); 
			log.append(clientId+" "+threadName+"\n-\tstart of log\t-\n");
			log.append(pm.getDiscounts(languageTag)
					     .entrySet()
					     .stream()
					     .map(entry-> entry.getKey()+"\t"+entry.getValue())
					     .collect(Collectors.joining("\n")));
			Product product = pm.reviewProduct(productId, Rating.FOUR_STAR, "Yet Another Review");
			log.append((product != null)
					? "\nProduct "+productId+" reviewed\n"
					: "\nProduct "+productId+" not reviewed\n");
			log.append("\n-\tend of log\t-\n");
			return log.toString();
		};
		List<Callable<String>> clients = Stream.generate(()->client)
											   .limit(5)
											   .collect(Collectors.toList());
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		try {
			List<Future<String>> results = executorService.invokeAll(clients);
			executorService.shutdown();
			results.stream().forEach(result -> {
				try {
					System.out.println(result.get());
				} catch (InterruptedException | ExecutionException e) {
					System.out.println(e.getClass().getName()+" "+e.getCause());
				}
			}
			);
		} catch (InterruptedException e) {
			System.out.println(e.getClass().getName()+" "+e.getCause());
		}
		
		
		//pm.printProductReport(101,"es_ES");
		//pm.printProductReport(103,"fr_FR");
		//pm.createCSV(new String[] {"F","104","Cookie","1.99","0","2022-03-30"});
		//pm.createCSV(new String[] {"D","105","Coffe","2.5","0","2022-03-28"});
		//pm.createCSV(new String[] {"D","106","Chocolate","1.75","0","2022-03-28"});
		//pm.createCSV("D 106 Chocolate 1.75 0 2022-03-28");
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
		pm.createProduct(164,"Kombucha", BigDecimal.valueOf(2.10), Rating.NOT_RATED);
		pm.reviewProduct(164,Rating.TWO_STAR, "Looks like tea but is it?");
		pm.reviewProduct(164,Rating.THREE_STAR, "Fine tea");
		pm.reviewProduct(164,Rating.FOUR_STAR, "Great, but this is not tea");
		pm.reviewProduct(164,Rating.FIVE_STAR, "Perfect");
		
		pm.dumpData();
		pm.restoreData();
		
		pm.printProductReport(164,"es_ES");
		pm.printProducts((Product p) -> p.getPrice().floatValue()>1,
				(pm1,pm2) -> pm2.getRating().ordinal() - pm1.getRating().ordinal(),"es_ES");
		pm.getDiscounts("es_ES").forEach((rating,discount) -> System.out.println(rating+"\t"+discount));
 */	
		
	}

}

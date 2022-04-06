package labs.pm.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
//import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.opencsv.CSVWriter;

public class ProductManager { // Clase CRUD -> Create Read(single/all/find) Update(find) Delete(find)
	/*
	 * private Product product; private Review[] reviews = new Review[5];
	 */
	private Map<Product, List<Review>> products = new ConcurrentHashMap<>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock writeLock = lock.writeLock();
	private final Lock readLock = lock.readLock();
	
	private static Map<String, ResourceFormatter> formatters = Map.of(
			"en_GB", new ResourceFormatter(Locale.UK),
			"en_US", new ResourceFormatter(Locale.US), 
			"fr_FR", new ResourceFormatter(Locale.FRANCE), 
			"zh_CN", new ResourceFormatter(Locale.CHINA), 
			"es_ES", new ResourceFormatter(new Locale("es", "ES")));
	//private ResourceFormatter formatter;
	// Clase contenedora para trabajar con nuestros patrones de recursos
	private final ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");
	// Formato de producto y valoraciones
	private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
	private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
	// Rutas
	private final Path reportsFolder = Path.of(config.getString("reports.folder"));
	private final Path dataFolder = Path.of(config.getString("data.folder"));
	private final Path tempFolder = Path.of(config.getString("temp.folder"));
	
	private static final ProductManager pm = new ProductManager();

	/*
	public ProductManager(Locale locale) {
		this(locale.toLanguageTag());
	}
    */
	private ProductManager() { // Añadir String languageTag si queremos volver a la configuración
		//changeLocale(languageTag);
		loadAllData();
	}
/*
	public void changeLocale(String languageTag) {
		formatter = formatters.getOrDefault(languageTag, formatters.get("en_GB"));
	}
*/
	
	public static Set<String> getSupportedLocales() {
		return formatters.keySet();
	}
	// Creamos un método getter que devuelve la instancia de ProductManager para poder tener el constructor private
	public static ProductManager getInstance() {
		return pm;
	}
	public synchronized Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
		Product product = null;
		try {
			writeLock.lock();
			product = new Food(id, name, price, rating, bestBefore);
			products.putIfAbsent(product, new ArrayList<>());
		} catch (Exception e) {
			System.out.println(e.getCause());
		} finally {
			writeLock.unlock();
		}
		return product;
	}

	public synchronized Product createProduct(int id, String name, BigDecimal price, Rating rating) {
		Product product = null;
		try {
			writeLock.lock();
			product = new Drink(id, name, price, rating);
			products.putIfAbsent(product, new ArrayList<>());
		} catch (Exception e) {
			System.out.println(e.getCause());
		} finally {
			writeLock.unlock();
		}
		return product;
	}

	private synchronized Product reviewProduct(Product product, Rating rating, String comments) { // UPDATE - MODIFICA REVIEWS
		/*
		 * // Version 1 -> Con Array if (reviews[reviews.length-1]!=null) { reviews =
		 * Arrays.copyOf(reviews, reviews.length+5); } int sum = 0; int i = 0; boolean
		 * reviewed = false; while (i<reviews.length && !reviewed) { if (reviews[i] ==
		 * null) { reviews[i] = new Review(rating,comments); reviewed = true; } sum +=
		 * reviews[i].getRating().ordinal(); i++; } this.product =
		 * product.applyRating(Rateable.convert(Math.round((float)sum/i))); return
		 * this.product;
		 */
		List<Review> reviews = products.get(product);
		products.remove(product, reviews);
		reviews.add(new Review(rating, comments));
		/*
		 * int sum = 0; for (Review review : reviews) { sum +=
		 * review.getRating().ordinal(); } product =
		 * product.applyRating(Rateable.convert(Math.round((float) sum /
		 * reviews.size())));
		 */
		product = product.applyRating(Rateable.convert(
				(int) Math.round(reviews.parallelStream().mapToInt(r -> r.getRating().ordinal()).average().orElse(0))));
		products.put(product, reviews);
		return product;
	}

	public Product reviewProduct(int id, Rating rating, String comments) {
		try {
			writeLock.lock();
			return reviewProduct(findProduct(id), rating, comments);
		} catch (ProductManagerException e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
			return null;
		} finally {
			writeLock.unlock();
		}
	}

	public synchronized Product findProduct(int id) throws ProductManagerException {
		/*
		 * Product result = null; for (Product product : products.keySet()) { if
		 * (product.getId() == id) { result = product; break; } } return result;
		 */
		try {
			readLock.lock();
			return products.keySet().stream().filter(p -> p.getId() == id).findFirst() // Finaliza el stream
				// .orElseGet(() -> null);
				.orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));
		} finally {
			readLock.unlock();
		}
	}

	private void printProductReport(Product product, String languageTag, String client) throws IOException { // READ
		/*
		 * Version 1 -> Array StringBuilder txt = new StringBuilder();
		 * txt.append(MessageFormat.format( resources.getString("product"),
		 * product.getName(), moneyFormat.format(product.getPrice()),
		 * product.getRating().getStars(), dateFormat.format(product.getBestBefore())
		 * )); txt.append("\n");
		 * 
		 * for (Review review : reviews) { if (review == null) { break; }
		 * txt.append(MessageFormat.format( resources.getString("review"),
		 * review.getRating().getStars(), review.getComments())); txt.append("\n"); } if
		 * (reviews[0]==null){ txt.append(resources.getString("no.reviews"));
		 * txt.append("\n"); } System.out.println(txt);
		 */
		List<Review> reviews = products.get(product);
		ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en_GB"));
		Collections.sort(reviews);
		if (Files.notExists(reportsFolder)) {
			Files.createDirectories(reportsFolder);
		}
		
		Path productFile = reportsFolder
				.resolve(MessageFormat.format(config.getString("report.file"), product.getId(),client));
		try (PrintWriter out = new PrintWriter(
				new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE), "UTF-8"))) {

			out.append(formatter.formatProduct(product) + System.lineSeparator());
			if (reviews.isEmpty()) {
				out.append(formatter.getText("no.reviews") + System.lineSeparator());
			} else {
				out.append(reviews.stream().map(r -> formatter.formatReview(r) + System.lineSeparator())
						.collect(Collectors.joining()));

				// reviews.stream()
				// .forEach(r -> txt.append(formatter.formatReview(r)+"\n"));
			}
		}
		// StringBuilder txt = new StringBuilder();
		// txt.append(formatter.formatProduct(product));
		// txt.append("\n");
		/*
		 * for (Review review : reviews) { txt.append(formatter.formatReview(review));
		 * txt.append("\n"); } if (reviews.isEmpty()) {
		 * txt.append(formatter.getText("no.reviews")); txt.append("\n"); } if
		 * (reviews.isEmpty()) { txt.append(formatter.getText("no.reviews")+"\n"); }
		 * else { txt.append(reviews.stream() .map(r -> formatter.formatReview(r)+"\n")
		 * .collect(Collectors.joining()));
		 * 
		 * //reviews.stream() // .forEach(r ->
		 * txt.append(formatter.formatReview(r)+"\n")); }
		 */
		// System.out.println(txt);

	}

	public void printProductReport(int id, String languageTag, String client) {
		try {
			writeLock.lock();
			printProductReport(findProduct(id),languageTag,client);
		} catch (ProductManagerException e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + " Error en la impresión");
			e.printStackTrace();
		} finally {
			writeLock.unlock();
		}
	}
	
	public synchronized void printProducts(Comparator<Product> sorter, String languageTag) {
		try {
			readLock.lock();
			StringBuilder txt = new StringBuilder();
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en_GB"));
			/*
			 * List<Product> productList = new ArrayList<>(products.keySet());
			 * productList.sort(sorter); for (Product product : productList) { // for(int i=
			 * 0;
			 * i<productList.size();i++){txt.append(formatter.formatProduct(productList.get(
			 * i));} txt.append(formatter.formatProduct(product)); txt.append("\n"); }
			 */
			products.keySet().stream().sorted(sorter).forEach(p -> txt.append(formatter.formatProduct(p) + "\n"));
			System.out.println(txt);
		} finally {
			readLock.unlock();
		}
	}

	public synchronized void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
		try {
			readLock.lock();
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en_GB"));
			StringBuilder txt = new StringBuilder();
			products.keySet().stream().sorted(sorter).filter(filter)
					.forEach(p -> txt.append(formatter.formatProduct(p) + "\n"));

			System.out.println(txt);
		} finally {
			readLock.unlock();
		}
	}

	private List<Review> loadReviews(Product product) {
		List<Review> reviews = null;
		Path file = reportsFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
		if (Files.notExists(file)) {
			reviews = new ArrayList<>();
		} else {
			try {
				reviews = Files.lines(file, Charset.forName("UTF-8")).map(text -> parseReview(text))
						.filter(review -> review != null).collect(Collectors.toList());
			} catch (IOException e) {
				System.out.println("Error cargando las valoraciones");
				e.printStackTrace();
			}
		}
		return reviews;
	}

	private Product loadProduct(Path file) {
		Product p = null;
		try {
			p = parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8")).findFirst().orElseThrow()
					.replace(';', ','));
		} catch (IOException e) {
			System.out.println("Error cargando el producto");
			e.printStackTrace();
		}
		return p;
	}
	@SuppressWarnings("unused")
	private synchronized void dumpData() {
		try {
			if (Files.notExists(tempFolder)) {
				Files.createDirectories(tempFolder);
			}
			Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"),
					Arrays.toString(Instant.now().toString().split(":"))));
			try (ObjectOutputStream out = new ObjectOutputStream(
					Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
				out.writeObject(products);
				products = new ConcurrentHashMap<>();
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": Error dumping data");
		}
	}

	public void createCSV(String[] product) {
		try {
			if (Files.notExists(dataFolder)) {
				Files.createDirectories(dataFolder);
			}
			/*
			 * try(PrintWriter writer = new PrintWriter(new File(dataFile.toString()))){
			 * writer.write(productText); }
			 */
			if (Files.notExists(dataFolder.resolve(MessageFormat.format(config.getString("product.data.file"), product[1])))) {
				Path dataFile = dataFolder.resolve(MessageFormat.format(config.getString("product.data.file"), product[1]));
				try (CSVWriter csvWriter = new CSVWriter(new FileWriter(dataFile.toString()));) {

					csvWriter.writeNext(product);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": Error creando el CSV");
		}
	}

	public void createCSV(String product) {
		String[] productArray = product.split(" ");
		try {
			if (Files.notExists(dataFolder)) {
				Files.createDirectories(dataFolder);
			}
			Path dataFile = dataFolder
					.resolve(MessageFormat.format(config.getString("product.data.file"), productArray[1]));
			/*
			 * try(PrintWriter writer = new PrintWriter(new File(dataFile.toString()))){
			 * writer.write(productText); }
			 */
			try (CSVWriter csvWriter = new CSVWriter(new FileWriter(dataFile.toString()));) {
				csvWriter.writeNext(productArray);
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": Error creando el CSV");
		}
	}

	@SuppressWarnings({"unchecked","unused"})
	private synchronized void restoreData() {
		try {
			Path tempFile = Files.list(tempFolder).filter(path -> path.getFileName().toString().endsWith("tmp"))
					.findFirst().orElseThrow();
			try (ObjectInputStream in = new ObjectInputStream(
					Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) { // Cambiar a modo READ si
																							// queremos mantener los
																							// archivos creados
				products = (Map<Product, List<Review>>) in.readObject();
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + " " + e.getCause());
		}
	}

	private synchronized void loadAllData() {
		try {
			products = Files.list(dataFolder).filter(file -> file.getFileName().toString().startsWith("product"))
					.map(file -> loadProduct(file)).filter(product -> product != null)
					.collect(Collectors.toMap(product -> product, product -> loadReviews(product)));
		} catch (IOException e) {
			System.out.println("Error en la carga de datos.");
			e.printStackTrace();
		}
	}

	private Review parseReview(String text) { // throws ProductManagerException
		Review review = null;
		try {
			Object[] values = reviewFormat.parse(text);
			review = new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);

		} catch (ParseException | NumberFormatException e) {
			System.out.println("Error parsing review " + text + " " + e.getMessage());
			// throw new ProductManagerException("No soy capaz de convertir la
			// información",e);
		}
		return review;
	}

	private Product parseProduct(String text) {
		Product p = null;
		try {		
			Object[] values = productFormat.parse(text);
			int id = Integer.parseInt((String) values[1]);
			String name = (String) values[2];
			BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
			Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
			switch ((String) values[0]) {
			case "D":
				// p = createProduct(id,name,price,rating);
				p = new Drink(id, name, price, rating);
				break;
			case "F":
				/*
				StringBuilder textToParse = new StringBuilder();
				String[] aux = ((String) values[5]).split("/"); // DD MM YYYY -> YYYY-MM-DD
				for (int i = aux.length-1; i >= 0 ; i--) {
					if (i != 0) textToParse.append(aux[i]+"-");						
					else textToParse.append(aux[i]);		
				}*/
				LocalDate bestBefore = LocalDate.parse((String) values[5]);
				// p = createProduct(id,name,price,rating,bestBefore);
				p = new Food(id, name, price, rating, bestBefore);
				break;
			}
		} catch (ParseException | NumberFormatException | DateTimeParseException e) {
			System.out.println("Error parsing product " + text + " " + e.getMessage());
			e.printStackTrace();
		}
		return p;
	}

	public Map<String, String> getDiscounts(String languageTag) {
		try {
			readLock.lock();
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
			return products.keySet().stream()
					.collect(Collectors.groupingBy((Product p) -> p.getRating().getStars(),
							Collectors.collectingAndThen(Collectors.summingDouble(p -> p.getDiscount().doubleValue()),
									d -> formatter.moneyFormat.format(d))));
		} finally {
			readLock.unlock();
		}
	}

	private static class ResourceFormatter {
		@SuppressWarnings("unused")
		private Locale locale;
		private ResourceBundle resources;
		private DateTimeFormatter dateFormat;
		private NumberFormat moneyFormat;

		private ResourceFormatter(Locale locale) {
			this.locale = locale;
			resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
			dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
			moneyFormat = NumberFormat.getCurrencyInstance(locale);
		}

		private String formatProduct(Product product) {
			return MessageFormat.format(resources.getString("product"), product.getName(),
					moneyFormat.format(product.getPrice()), product.getRating().getStars(),
					dateFormat.format(product.getBestBefore()));
		}

		private String formatReview(Review review) {
			return MessageFormat.format(resources.getString("review"), review.getRating().getStars(),
					review.getComments());
		}

		private String getText(String key) {
			return resources.getString(key);
		}
	}

}

package labs.pm.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Product implements Rateable<Product>{
	// FIELDS
	private final int id;
	private final String name;
	private final BigDecimal price;
	public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);
	private Rating rating;
	
	// CONSTRUCTORS
	Product(int id, String name, BigDecimal price, Rating rating) { //Package-private
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.rating = rating;
	}	
	/*
	public Product(int id, String name, BigDecimal price) {
		this(id,name,price,Rating.NOT_RATED);
	}

	public Product() {
		this(0,"no name",BigDecimal.ZERO);
	}
	 */
	// GETTERS&SETTER
	public int getId() {
		return id;
	}
	/*
	public void setId(final int id) {
		this.id = id;
	}*/
	public String getName() {
		return name;
	}
	/*
	public void setName(final String name) {
		this.name = name;
	}*/
	public BigDecimal getPrice() {
		return price;
	}
	/*
	public void setPrice(final BigDecimal price) {
		this.price = price;
	}*/
	public BigDecimal getDiscount() {
		return price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
	}
	
	@Override
	public Rating getRating() {
		return rating;
	}
	
	//public abstract Product applyRating(Rating newRating);
	
	public LocalDate getBestBefore() {
		return LocalDate.now();
	}
	
	//toString()
	@Override
	public String toString() {
		return id + ", " + name + ", " + price + ", " + getDiscount() + ", "+ rating.getStars()
		+ ", "+ getBestBefore();
	}

	// hashCode() & equals()
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + this.id;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this==obj) return true;
		if (obj instanceof Product) {
			final Product other = (Product)obj;
			return (this.id == other.id) && (Objects.equals(this.name, other.name));
		}
		return false;
	}
	
	
}

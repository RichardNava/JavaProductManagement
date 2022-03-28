package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalTime;

public final class Drink extends Product{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	Drink(int id, String name, BigDecimal price, Rating rating) {
		super(id, name, price, rating);
	}

	@Override
	public BigDecimal getDiscount() {
		LocalTime now = LocalTime.now();
		return (now.isAfter(LocalTime.of(11,00)) && now.isBefore(LocalTime.of(12,00))) ? 
				super.getDiscount() : BigDecimal.ZERO; 
	}

	@Override
	public Product applyRating(Rating newRating) {
		return new Drink(getId(),getName(),getPrice(),newRating);
	}

	
}

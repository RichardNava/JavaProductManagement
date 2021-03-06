package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Food extends Product{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2012587646685790465L;
	//FIELDS
	private LocalDate bestBefore;
	//CONSTRUCTORES
	Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
		super(id, name, price, rating);
		this.bestBefore = bestBefore;
	}
	
	public LocalDate getBestBefore() {
		return bestBefore;
	}
	
	/*
	//toString()
	@Override
	public String toString() {
		return super.toString();
	}*/
	
	//METODOS SOBREESCRITOS
	@Override
	public BigDecimal getDiscount() {
		return (bestBefore.isEqual(LocalDate.now())) ? super.getDiscount() : BigDecimal.ZERO;
	}

	@Override
	public Product applyRating(Rating newRating) {
		return new Food(getId(),getName(),getPrice(),newRating,bestBefore);
	}
	
	
}

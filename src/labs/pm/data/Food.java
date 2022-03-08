package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Food extends Product{
	//FIELDS
	private LocalDate bestBefore;
	//CONSTRUCTORES
	public Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
		super(id, name, price, rating);
		this.bestBefore = bestBefore;
	}
	
	public LocalDate getBestBefore() {
		return bestBefore;
	}
	
	//toString()
	@Override
	public String toString() {
		return super.toString() +", "+ bestBefore;
	}
	//METODOS SOBREESCRITOS
	@Override
	public BigDecimal getDiscount() {
		return (bestBefore.isEqual(LocalDate.now())) ? super.getDiscount() : BigDecimal.ZERO;
	}
	
	
}

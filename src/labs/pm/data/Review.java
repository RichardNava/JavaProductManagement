package labs.pm.data;

public class Review implements Comparable<Review> {
	private Rating rating;
	private String comments;
	
	public Review(Rating rating, String comments) {
		super();
		this.rating = rating;
		this.comments = comments;
	}

	public Rating getRating() {
		return rating;
	}

	public String getComments() {
		return comments;
	}

	@Override
	public String toString() {
		return "Review [rating=" + rating + ", comments=" + comments + "]";
	}

	@Override
	public int compareTo(Review o) { 
		return o.rating.ordinal() - this.rating.ordinal(); // Criterio que permite ordenar de mayor a menor
		
		//----------> {3,2,1}
		// vuelta 1.1// 2,3,1 -> Se mete en el if
		// vuelta 1.2// 2,1,3 -> Se mete en el if
		// vuelta 2.1// 1,2,3 -> Se mete en el if
		// vuelta 2.2// 1,2,3 -> No se mete en el if
	}
	
	
	
	
}

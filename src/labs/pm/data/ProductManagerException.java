package labs.pm.data;

public class ProductManagerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ProductManagerException() {
		super();
	}
	public ProductManagerException(String message) {
		super(message);
	}
	public ProductManagerException(String message, Throwable cause) {
		super(message,cause);
	}
}

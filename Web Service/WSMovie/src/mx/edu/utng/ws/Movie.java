package mx.edu.utng.ws;

public class Movie {
	private int id;
	private String name;
	private String sinopsis;
	private int type;
	private float price;
	
	public Movie(int id, String name, String sinopsis, int type, float price) {
		super();
		this.id = id;
		this.name = name;
		this.sinopsis = sinopsis;
		this.type = type;
		this.price = price;
	}
	
	public Movie (){
		this(0,"","",0,0.0f);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSinopsis() {
		return sinopsis;
	}

	public void setSinopsis(String sinopsis) {
		this.sinopsis = sinopsis;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Movie [id=" + id + ", name=" + name + ", sinopsis=" + sinopsis + ", type=" + type + ", price=" + price
				+ "]";
	}
	
	

}

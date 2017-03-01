package ds.gae.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class CarType {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    private String companyName;
    //trunk space in liters
    private float trunkSpace;
    
    /***************
	 * CONSTRUCTOR *
	 ***************/
    
    public CarType()
    {
    	
    }
    
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }
    public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

    public String getName() {
    	return name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    
    public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setNbOfSeats(int nbOfSeats) {
		this.nbOfSeats = nbOfSeats;
	}

	public void setSmokingAllowed(boolean smokingAllowed) {
		this.smokingAllowed = smokingAllowed;
	}

	public void setRentalPricePerDay(double rentalPricePerDay) {
		this.rentalPricePerDay = rentalPricePerDay;
	}

	public void setTrunkSpace(float trunkSpace) {
		this.trunkSpace = trunkSpace;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}
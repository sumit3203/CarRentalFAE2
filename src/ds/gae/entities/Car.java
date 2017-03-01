package ds.gae.entities;

//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Key;

@Entity
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	@Transient
	private CarType type;
	private int carId;	
	private String name;
	private int nbOfSeats;
	private boolean smokingAllowed;
	private double rentalPricePerDay;
	//trunk space in liters
	private float trunkSpace;
	private String companyName;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Car(int uid, CarType type) {
		this.carId = uid;
		this.type = type;
	}

	/******
	 * ID *
	 ******/

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	/************
	 * CAR TYPE *
	 ************/

	public CarType getType() {
		return type;
	}

	public void setType(CarType type) {
		this.type = type;
	}



	/****************
	 * GETTERS & SETTERS *
	 ****************/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNbOfSeats() {
		return nbOfSeats;
	}

	public void setNbOfSeats(int nbOfSeats) {
		this.nbOfSeats = nbOfSeats;
	}

	public boolean isSmokingAllowed() {
		return smokingAllowed;
	}

	public void setSmokingAllowed(boolean smokingAllowed) {
		this.smokingAllowed = smokingAllowed;
	}

	public double getRentalPricePerDay() {
		return rentalPricePerDay;
	}

	public void setRentalPricePerDay(double rentalPricePerDay) {
		this.rentalPricePerDay = rentalPricePerDay;
	}

	public float getTrunkSpace() {
		return trunkSpace;
	}

	public void setTrunkSpace(float trunkSpace) {
		this.trunkSpace = trunkSpace;
	}


	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

}
package ds.gae.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.appengine.api.datastore.Key;


@Entity
public class Reservation {

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
	@Temporal(TemporalType.DATE)
    private Date startDate;
	@Temporal(TemporalType.DATE)
    private Date endDate;
    private String carRenter;
    private String rentalCompany;
    private String carType;
    private double rentalPrice;
    private long  carId;
  
    
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getCarRenter() {
		return carRenter;
	}
	public void setCarRenter(String carRenter) {
		this.carRenter = carRenter;
	}
	public String getRentalCompany() {
		return rentalCompany;
	}
	public void setRentalCompany(String rentalCompany) {
		this.rentalCompany = rentalCompany;
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
	}
	public double getRentalPrice() {
		return rentalPrice;
	}
	public void setRentalPrice(double rentalPrice) {
		this.rentalPrice = rentalPrice;
	}
	public long getCarId() {
		return carId;
	}
	public void setCarId(long carId) {
		this.carId = carId;
	}
    

  
    
}
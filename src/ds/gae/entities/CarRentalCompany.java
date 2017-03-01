package ds.gae.entities;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Key;


@Entity
public class CarRentalCompany {

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	@Basic(optional=false)
	private String name;
	@Transient
	private Set<Car> cars;
	@Transient
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();



	/***************
	 *     ID      *
	 ***************/

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	/********
	 * NAME *
	 ********/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	/***************
	 * CONSTRUCTOR *
    */

	public CarRentalCompany(String name, Set<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		for(Car car:cars)
		carTypes.put(car.getType().getName(), car.getType());
	}

	public Set<Car> getCars() {
		return cars;
	}

	public Map<String, CarType> getCarTypes() {
		return this.carTypes;
	}

	public void setCars(Set<Car> cars) {
		this.cars = cars;
	}

	public void setCarTypes(Map<String, CarType> carTypes) {
		this.carTypes = carTypes;
	}



	
}
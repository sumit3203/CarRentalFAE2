package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.transaction.Transaction;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;

public class CarRentalModel {


	private static CarRentalModel instance;
	private EntityManager em = EMF.get().createEntityManager();

	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the car rental company
	 * @return	The list of car types (i.e. name of car type), available
	 * 			in the given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		// TODO add implementation
		Query q= em.createQuery("Select m.name from CarType m where m.companyName = :companyName");
		q.setParameter("companyName",crcName);
		List<String> carTypes = q.getResultList();
		//System.out.println(carTypes.size());
		
		return new HashSet<String>(carTypes);
	}

	/**
	 * Get all registered car rental companies
	 *
	 * @return	the list of car rental companies
	 */
	public Collection<String> getAllRentalCompanyNames() {
		// FIXME use persistence instead
		List<String> resultList = new LinkedList<String>();
		List<CarRentalCompany> company = em.createQuery("select r from CarRentalCompany r").getResultList();

		for(CarRentalCompany crc : company)
			resultList.add(crc.getName());

		return resultList;
	}

	/**
	 * Create a quote according to the given reservation constraints (tentative reservation).
	 * 
	 * @param	company
	 * 			name of the car renter company
	 * @param	renterName 
	 * 			name of the car renter 
	 * @param 	constraints
	 * 			reservation constraints for the quote
	 * @return	The newly created quote.
	 *  
	 * @throws ReservationException
	 * 			No car available that fits the given constraints.
	 */
	public Quote createQuote(String company, String renterName, ReservationConstraints constraints) throws ReservationException {
		// FIXME: use persistence instead
		em.getTransaction().begin();
		CarType carType = getCarType(constraints.getCarType(), company);
		Quote quote = new Quote(renterName, constraints.getStartDate(), constraints.getEndDate(),company, constraints.getCarType(),carType.getRentalPricePerDay());
		em.persist(quote);
		em.getTransaction().commit();
		return quote;
	}

	public CarType getCarType(String carType, String company ){

		CarType carT=null;
		Query q= em.createQuery("Select m from CarType m where m.companyName = :companyName and m.name= :carType");
		q.setParameter("companyName",company);
		q.setParameter("carType",carType);
		List<CarType> carTypes = q.getResultList();	
		if(carTypes.size()>0){
			carT=carTypes.get(0);
		}
		return carT;
	}


	/**
	 * Confirm the given quote.
	 *
	 * @param 	q
	 * 			Quote to confirm
	 * 
	 * @throws ReservationException
	 * 			Confirmation of given quote failed.	
	 */
	public Reservation confirmQuote(Quote q) throws ReservationException {
		// FIXME: use persistence instead
		Reservation reservation=null;
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			reservation= new Reservation();
			reservation.setStartDate(q.getStartDate());
			reservation.setEndDate(q.getEndDate());
			reservation.setRentalPrice(q.getRentalPrice());
			reservation.setRentalCompany(q.getRentalCompany());
			reservation.setCarRenter(q.getCarRenter());
			reservation.setRentalCompany(q.getRentalCompany());
			reservation.setCarType(q.getCarType());
			CarType carType= new CarType();
			carType.setName(q.getCarType());
			
			List<Car> carIdList=getAvailableCars(q,carType);
			if(carIdList.size()==0)
			{
				throw new ReservationException("Reservation failed, all cars of type ");
			}
			Car car = carIdList.get((int)(Math.random()*carIdList.size()));
			reservation.setCarId(car.getCarId());
			em.persist(reservation);
			tx.commit();
			em.flush();
			em.refresh(reservation); //let's avoid stale data!!
			
		}catch(Exception e)
		{
			if (tx.isActive()) {
				tx.rollback();
			}
			//throw new ReservationException("Cannot confirm!!");
		}

		return reservation;
	}
	
	/**
	 * Get available car types.
	 *
	 * @param 	quote
	 * 			Quote to confirm
	 * 			carType
	 * 			Car type to be checked
	 */

	private List<Car> getAvailableCars(Quote quote,CarType carType)
	{   
		Map<Integer,Car> carMap= new HashMap<Integer,Car>();
		List<Car> cars=getCarsByCarType(quote.getRentalCompany(),carType);
		for(Car car:cars)
		{
			carMap.put(car.getCarId(),car);
		}		
		List<Reservation> reservations = getReservationsforType(quote, carType);
		for(Reservation r: reservations)
		{
			
			if(r.getEndDate().before(quote.getStartDate()) || r.getStartDate().after(quote.getEndDate()) 
					||(r.getStartDate().equals(quote.getStartDate())||r.getEndDate().equals(quote.getEndDate())))
			{  
				
				Long carId=r.getCarId();
				carMap.remove(carId.intValue());
			}

		}
		//System.out.println(carMap.keySet().toString());
		return new ArrayList<Car>(carMap.values());
	}

	public List<Reservation> getReservationsforType(Quote quote,CarType carType)
	{	
		List<Reservation> reservationList = new LinkedList<Reservation>();
		
		reservationList = em.createQuery("select res from Reservation res where res.rentalCompany = :rentalCompany" +
				" and res.carType = :carType ")
				.setParameter("rentalCompany",quote.getRentalCompany())
				.setParameter("carType",carType.getName())
				.getResultList();
		
		return reservationList;
	}

	/**
	 * Confirm the given list of quotes
	 * 
	 * @param 	quotes 
	 * 			the quotes to confirm
	 * @return	The list of reservations, resulting from confirming all given quotes.
	 * 
	 * @throws 	ReservationException
	 * 			One of the quotes cannot be confirmed. 
	 * 			Therefore none of the given quotes is confirmed.
	 */
	public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {    	
		// TODO add implementation		
		List<Reservation> reservations= new ArrayList<Reservation>();
		Boolean unavailabilityFlag = false;
		CarType carType = new CarType();
		try{
			for(Quote quote:quotes){	
				carType.setName(quote.getCarType());
				if (getAvailableCars(quote,carType).isEmpty()){
					unavailabilityFlag = true;
					System.out.println("Cannot confirm reservation due to unavailable car type: " + carType);
				}
			}
			if(!unavailabilityFlag){
				for(Quote quote:quotes){
					Reservation r=confirmQuote(quote);
					reservations.add(r);
				}
			}
			
		}
		catch(Exception e){   
			
			for(Reservation r: reservations){
				em.getTransaction().begin();
				em.remove(r);
				em.getTransaction().commit();
			}

			throw new ReservationException("Reservation failed, all cars of type ");
		}
		return reservations;
	}

	/**
	 * Get all reservations made by the given car renter.
	 *
	 * @param 	renter
	 * 			name of the car renter
	 * @return	the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		// FIXME: use persistence instead
		List<Reservation> out = new ArrayList<Reservation>();

		Query q= em.createQuery("select  r from Reservation r where r.carRenter = :userName");
		q.setParameter("userName",renter);
		List<Reservation> reservationEntityList = q.getResultList();
		for (Reservation r : reservationEntityList ) {
			if (r.getCarRenter().equals(renter)) {
				out.add(r);
			}
		}

		return out;
	}

	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the given car rental company
	 * @return	The list of car types in the given car rental company.
	 */
	public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
		// FIXME: use persistence instead
		Collection<CarType> out = new ArrayList<CarType>();
		Query q= em.createQuery("Select m from CarType m where m.companyName = :companyName");
		q.setParameter("companyName",crcName);
		List<CarType> carTypes=q.getResultList();

		return carTypes;
	}

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	A list of car IDs of cars with the given car type.
	 */
	public Collection<Integer> getCarIdsByCarType(String crcName, CarType carType) {

		Query q= em.createQuery("Select   m.carId from Car m where m.companyName = :companyName and m.name=:type");
		q.setParameter("companyName",crcName);
		q.setParameter("type",carType.getName());
		List<Integer> carIdList= q.getResultList();
		Collection<Integer> out = new ArrayList<Integer>(carIdList);
		return out;
	}

	/**
	 * Get the amount of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	A number, representing the amount of cars of the given car type.
	 */
	public int getAmountOfCarsByCarType(String crcName, CarType carType) {

		return this.getCarsByCarType(crcName, carType).size();
	}

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	List of cars of the given car type
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {				
		// FIXME: use persistence instead

		List<Car> out = new ArrayList<Car>(); 
		Query q= em.createQuery("Select m from Car m where m.companyName = :companyName and m.name=:type");
		q.setParameter("companyName",crcName);
		q.setParameter("type",carType.getName());
		out= q.getResultList();
		return out;
	}

	/**
	 * Check whether the given car renter has reservations.
	 *
	 * @param 	renter
	 * 			the car renter
	 * @return	True if the number of reservations of the given car renter is higher than 0.
	 * 			False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;		
	}	
}
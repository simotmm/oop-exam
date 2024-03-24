package it.polito.oop.vaccination;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.*;

public class Vaccines {

    public final static int CURRENT_YEAR = java.time.LocalDate.now().getYear();

    Map<String, Person> people = new HashMap<>();
    Map<Integer, Interval> intervals = new HashMap<>();
    Map<String, Hub> hubs = new HashMap<>();
    List<Integer> hours = new ArrayList<>();
    
    // R1
    /**
     * Add a new person to the vaccination system.
     *
     * Persons are uniquely identified by SSN (italian "codice fiscale")
     *
     * @param firstName first name
     * @param last last name
     * @param ssn italian "codice fiscale"
     * @param y birth year
     * @return {@code false} if ssn is duplicate,
     */
    public boolean addPerson(String firstName, String last, String ssn, int y) {
        if(people.get(ssn)!=null)
        	return false;
        
        people.put(ssn, new Person(firstName, last, ssn, y));
    	
    	return true;
    }

    /**
     * Count the number of people added to the system
     *
     * @return person count
     */
    public int countPeople() {
        return people.size();
    }

    /**
     * Retrieves information about a person.
     * Information is formatted as ssn, last name, and first name
     * separate by {@code ','} (comma).
     *
     * @param ssn "codice fiscale" of person searched
     * @return info about the person
     */
    public String getPerson(String ssn) {
        Person p = people.get(ssn);
        if(p==null)
        	return null;
        
        return p.getSsn()+","+p.getLastName()+","+p.getFirstName();
    }

    /**
     * Retrieves of a person given their SSN (codice fiscale).
     *
     * @param ssn "codice fiscale" of person searched
     * @return age of person (in years)
     */
    public int getAge(String ssn) {
    	Person p = people.get(ssn);
        if(p==null)
        	return -1;
        
        return CURRENT_YEAR-p.getYear();
    }

    /**
     * Define the age intervals by providing the breaks between intervals.
     * The first interval always start at 0 (non included in the breaks)
     * and the last interval goes until infinity (not included in the breaks).
     * All intervals are closed on the lower boundary and open at the upper one.
     * <p>
     * For instance {@code setAgeIntervals(40,50,60)}
     * defines four intervals {@code "[0,40)", "[40,50)", "[50,60)", "[60,+)"}.
     *
     * @param breaks the array of breaks
     */
    
    //-1=+inf
    public void setAgeIntervals(int... breaks) {
    	
    	if(breaks[0]==0)
    	{
    		for(int i=0;i<breaks.length;i++)
    		{
    			Interval interval=null;
    			
    			if(i==breaks.length-1)
    			{
    				interval = new Interval (breaks[i], -1);
    				
    			}
    			else
    			{
    				interval = new Interval (breaks[i], breaks[i+1]);
    				
    			}
    			//System.out.println(interval);
    			intervals.put(i, interval);
    		}
    	}
    	else
    	{
    		List<Integer> lista = new ArrayList<>();
    		lista.add(0);
    		for(int n: breaks)
    			lista.add(n);
    		
    		for(int i=0;i<lista.size();i++)
    		{
    			Interval interval=null;
    			
    			if(i==lista.size()-1)
    			{
    				int n1=lista.get(i);
        			int n2=-1;
        			interval = new Interval(n1, n2);
    			}
    			else
    			{
    				int n1=lista.get(i);
        			int n2=lista.get(i+1);
        			interval = new Interval(n1, n2);
    				
    			}
    			//System.out.println(interval);
    			intervals.put(i, interval);
    		}
 
    	}

    }

    /**
     * Retrieves the labels of the age intervals defined.
     *
     * Interval labels are formatted as {@code "[0,10)"},
     * if the upper limit is infinity {@code '+'} is used
     * instead of the number.
     *
     * @return labels of the age intervals
     */
    public Collection<String> getAgeIntervals() {
        return intervals.values().stream()
        		.map(Interval::toString)
        		.collect(Collectors.toList());
    }

    /**
     * Retrieves people in the given interval.
     *
     * The age of the person is computed by subtracting
     * the birth year from current year.
     *
     * @param intv age interval label
     * @return collection of SSN of person in the age interval
     */
    public Collection<String> getInInterval(String intv) {
        Collection<String> lista = new ArrayList<>();
        String parti[]=intv.split("\\[");
        String parti2[]=parti[1].split(",");
        
        int inizio=Integer.parseInt(parti2[0]);
        int fine=-1;
        if(parti2[1].compareTo("+)")==0)
        	fine=-1;
        else
        {
        	String parti3[]=parti2[1].split("\\)");
        	fine=Integer.parseInt(parti3[0]);
        }
        	
       Interval interval = new Interval(inizio, fine);
       
       lista=people.values().stream()
    		   .filter(p -> interval.contains(CURRENT_YEAR-p.getYear()))
    		   .map(Person::getSsn)
    		   .collect(Collectors.toList());

        return lista;
        
    }

    // R2
    /**
     * Define a vaccination hub
     *
     * @param name name of the hub
     * @throws VaccineException in case of duplicate name
     */
    public void defineHub(String name) throws VaccineException {
    	if(hubs.get(name)!=null)
    			throw new VaccineException("");
    	
    	hubs.put(name, new Hub(name));
    }

    /**
     * Retrieves hub names
     *
     * @return hub names
     */
    public Collection<String> getHubs() {
        return hubs.values().stream()
        		.map(Hub::getName)
        		.collect(Collectors.toList());
    }

    /**
     * Define the staffing of a hub in terms of
     * doctors, nurses and other personnel.
     *
     * @param name name of the hub
     * @param doctors number of doctors
     * @param nNurses number of nurses
     * @param o number of other personnel
     * @throws VaccineException in case of undefined hub, or any number of personnel not greater than 0.
     */
    public void setStaff(String name, int doctors, int nNurses, int o) throws VaccineException {
    	Hub h =hubs.get(name);
    	if(h==null)
			throw new VaccineException("");
    	
    	h.setStaff(doctors, nNurses, o);
    }

    /**
     * Estimates the hourly vaccination capacity of a hub
     *
     * The capacity is computed as the minimum among
     * 10*number_doctor, 12*number_nurses, 20*number_other
     *
     * @param hub name of the hub
     * @return hourly vaccination capacity
     * @throws VaccineException in case of undefined or hub without staff
     */
    public int estimateHourlyCapacity(String hub) throws VaccineException {
    	Hub h =hubs.get(hub);
    	if(h==null || h.isPersonalSet()==false)
			throw new VaccineException("");
    	
        return h.estimateHourlyCapacity();
    }

    // R3
    /**
     * Load people information stored in CSV format.
     *
     * The header must start with {@code "SSN,LAST,FIRST"}.
     * All lines must have at least three elements.
     *
     * In case of error in a person line the line is skipped.
     *
     * @param people {@code Reader} for the CSV content
     * @return number of correctly added people
     * @throws IOException in case of IO error
     * @throws VaccineException in case of error in the header
     */
    public long loadPeople(Reader people) throws IOException, VaccineException {
        // Hint:
        BufferedReader br = new BufferedReader(people);
        List<String> lista = br.lines().collect(toList());
        if(lista==null)
        	throw new VaccineException("");
        	
        int i=0;
        int aggiunti=0;
        boolean a=l!=null;
        for(String riga: lista)
        {
        	i++;
        	if(i==1)
        	{
        		String parti[]=riga.split("[,]");
            	if(parti.length!=4){
            		if(a) {l.accept(i, riga); continue;}
            		else throw new VaccineException("");}
            	
            	String ssn=parti[0];
            	String last=parti[1];
            	String first=parti[2];
            	String anno=parti[3];
            	
            	if(ssn.compareTo("SSN")!=0 ||
            		last.compareTo("LAST")!=0 ||
            		first.compareTo("FIRST")!=0 ||
            		anno.compareTo("YEAR")!=0 )
            		throw new VaccineException("");
        	}
        	else
        	{
        		String parti[]=riga.split("[,]");
            	if(parti.length!=4){
            		if(a) l.accept(i, riga);
            		continue;
            	}
            	
            	String ssn=parti[0];
            	String last=parti[1];
            	String first=parti[2];
            	int anno=Integer.parseInt(parti[3]);
            	if(this.people.containsKey(ssn)&&a)l.accept(i, riga);
            	this.addPerson(first, last, ssn, anno);
            	aggiunti++;
        		
        	}
        	
        	
        }
        l=null;
        br.close();
        return aggiunti;
    }
    

    // R4
    /**
     * Define the amount of working hours for the days of the week.
     *
     * Exactly 7 elements are expected, where the first one correspond to Monday.
     *
     * @param hs workings hours for the 7 days.
     * @throws VaccineException if there are not exactly 7 elements or if the sum of all hours is less than 0 ore greater than 24*7.
     */
    public void setHours(int... hs) throws VaccineException {
    	List<Integer> hours = new ArrayList<>();
    	if(hs.length!=7)
    			throw new VaccineException("");
    	
    	for(int h: hs)
    	{
    		if(h>12)
    			throw new VaccineException("");
    		
    		hours.add(h);	
    	}
    	
    	this.hours=hours;
    		
    }

    /**
     * Returns the list of standard time slots for all the days of the week.
     *
     * Time slots start at 9:00 and occur every 15 minutes (4 per hour) and
     * they cover the number of working hours defined through method {@link #setHours}.
     * <p>
     * Times are formatted as {@code "09:00"} with both minuts and hours on two
     * digits filled with leading 0.
     * <p>
     * Returns a list with 7 elements, each with the time slots of the corresponding day of the week.
     *
     * @return the list hours for each day of the week
     */
    public List<List<String>> getHours() {
    	List<List<String>> lista = new ArrayList<>();
    	
    	for(int h: hours)
    	{
    		List<String> l = new ArrayList<>();
    		for(int i=9;i-9<h;i++){
    			l.add(String.format("%02d",i)+":00"); 
    			l.add(String.format("%02d",i)+":15");
    			l.add(String.format("%02d",i)+":30"); 
    			l.add(String.format("%02d",i)+":45");	
    		}
    		lista.add(l);
    	}
    	
    	
    	
    	
    	
        return lista;
    }

    /**
     * Compute the available vaccination slots for a given hub on a given day of the week
     * <p>
     * The availability is computed as the number of working hours of that day
     * multiplied by the hourly capacity (see {@link #estimateCapacity} of the hub.
     *
     * @return
     */
    public int getDailyAvailable(String hub, int d) {
        try { return hours.get(d)*estimateHourlyCapacity(hub); 
		} catch (VaccineException e){ e.printStackTrace(); } 
        return -1;
    }

    /**
     * Compute the available vaccination slots for each hub and for each day of the week
     * <p>
     * The method returns a map that associates the hub names (keys) to the lists
     * of number of available hours for the 7 days.
     * <p>
     * The availability is computed as the number of working hours of that day
     * multiplied by the capacity (see {@link #estimateCapacity} of the hub.
     *
     * @return
     */
    public Map<String, List<Integer>> getAvailable() {         
        return hubs.values().stream()
        		.collect(Collectors.toMap(h->h.name, h->w(h.name)));
    }

    /**
     * Computes the general allocation plan a hub on a given day.
     * Starting with the oldest age intervals 40%
     * of available places are allocated
     * to persons in that interval before moving the the next
     * interval and considering the remaining places.
     * <p>
     * The returned value is the list of SSNs (codice fiscale) of the
     * persons allocated to that day
     * <p>
     * <b>N.B.</b> no particular order of allocation is guaranteed
     *
     * @param hub name of the hub
     * @param d day of week index (0 = Monday)
     * @return the list of daily allocations
     */
    public List<String> allocate(String hub, int d) {
        List<String> l=new ArrayList<>(); 
        Hub h=hubs.get(hub); 
        int t=getDailyAvailable(hub,d); 
        List<Interval> r=new ArrayList<>(intervals.values()); 
        Collections.reverse(r);
        
        for(Interval i: r)
        {
        	int s=0, n=(int)Math.floor(0.4*t);
        	for(Person p: people.values())
        		if(i.contains(age(p)) && !p.allocated && s<n)
        		{
        			s++; t--; l.add(p.ssn); 
        			p.allocated=true;
        		} 
        }
        if(t!=0)
        {for(Interval i: r)
            	for(Person p: people.values())
            		if(i.contains(age(p))&&!p.allocated&&t!=0)
            		{	
            			l.add(p.ssn); 
            			p.allocated=true; 
            			t--;
            		}
        } 
        h.ad(d,(ArrayList<String>)l);
        return l;
    }

    /**
     * Removes all people from allocation lists and
     * clears their allocation status
     */
    public void clearAllocation() {
    	people.values().forEach(p->p.allocated=false);
    	hubs.values().forEach(h->h.a.clear());
    }

    /**
     * Computes the general allocation plan for the week.
     * For every day, starting with the oldest age intervals
     * 40% available places are allocated
     * to persons in that interval before moving the the next
     * interval and considering the remaining places.
     * <p>
     * The returned value is a list with 7 elements, one
     * for every day of the week, each element is a map that
     * links the name of each hub to the list of SSNs (codice fiscale)
     * of the persons allocated to that day in that hub
     * <p>
     * <b>N.B.</b> no particular order of allocation is guaranteed
     * but the same invocation (after {@link #clearAllocation}) must return the same
     * allocation.
     *
     * @return the list of daily allocations
     */
    public List<Map<String, List<String>>> weekAllocate(){
    	List<Map<String, List<String>>> l = new ArrayList<>();
    	hubs.values().forEach(x->{for(int i=0;i<7;i++) allocate(x.name,i);});
    	for(int d=0;d<7;d++)
    		l.add(d(d)); 
        return l;
    }

    // R5
    /**
     * Returns the proportion of allocated people
     * w.r.t. the total number of persons added
     * in the system
     *
     * @return proportion of allocated people
     */
    public double propAllocated() {
    	return (double)people.values().stream()
    			.filter(p->p.allocated)
    			.count()/people.size();
    }

    /**
     * Returns the proportion of allocated people
     * w.r.t. the total number of persons added
     * in the system, divided by age interval.
     * <p>
     * The map associates the age interval label
     * to the proportion of allocates people in that interval
     *
     * @return proportion of allocated people by age interval
     */
    public Map<String, Double> propAllocatedAge() {        
        return intervals.values().stream()
        		.collect(Collectors.toMap(i->i.toString(),
        				i->(double)people.values().stream()
        				.filter(p->i.contains(age(p))&&p.allocated)
        				.count()/people.size()));
    }

    /**
     * Retrieves the distribution of allocated persons
     * among the different age intervals.
     * <p>
     * For each age intervals the map reports the
     * proportion of allocated persons in the corresponding
     * interval w.r.t the total number of allocated persons
     *
     * @return
     */
    public Map<String, Double> distributionAllocated() {
        return intervals.values().stream()
        		.collect(Collectors.toMap(i->i.toString(), 
        				i->(double)people.values().stream()
        				.filter(p->i.contains(age(p))&&p.allocated)
        				.count()/(int)people.values().stream()
        				.filter(p->p.allocated)
        				.count()));
    }

    // R6
    /**
     * Defines a listener for the file loading method.
     * The {@ accept()} method of the listener is called
     * passing the line number and the offending line.
     * <p>
     * Lines start at 1 with the header line.
     *
     * @param lsnr the listener for load errors
     */
    public void setLoadListener(BiConsumer<Integer, String> lsnr) {
    	l=lsnr;
    }
    BiConsumer<Integer,String> l=null;
    public int age(Person p){
    	return CURRENT_YEAR-p.year;
    }
    public List<Integer> w(String h){
    	List<Integer>l=new ArrayList<>();
    	for(int i=0;i<7;i++) 
    		l.add(getDailyAvailable(h,i));
    	return l;
    }
    public Map<String, List<String>> d(int day){
    	return hubs.values().stream()
    			.filter(h->!h.a.isEmpty())
    			.collect(Collectors.toMap(h->h.name,
    					h->h.a.get(day).stream()
    					.collect(Collectors.toList())));
    }
}

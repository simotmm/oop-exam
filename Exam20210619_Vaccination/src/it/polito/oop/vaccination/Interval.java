package it.polito.oop.vaccination;

import java.util.HashMap;
import java.util.Map;

public class Interval {
	int inizio;
	int fine;
	Map<String, Person> people = new HashMap<>();
	
	public Interval(int inizio, int fine) {
		super();
		this.inizio = inizio;
		this.fine = fine;
	}
	
	public boolean contains(int x) {
		if(fine==-1) //perchè -1=+inf
		{
			if(x>=inizio)
				return true;
		}
		else
		{
			if(x>=inizio && x<fine)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String f=null;
		if(fine==-1)
			f="+";
		else
			f=""+fine;
		return "["+inizio+","+f+")";
	}
	public void addPerson(Person p) {
		people.put(p.getSsn(), p);
	}
	
	public int getInizio() {
		return inizio;
	}
	public void setInizio(int inizio) {
		this.inizio = inizio;
	}
	public int getFine() {
		return fine;
	}
	public void setFine(int fine) {
		this.fine = fine;
	}
	
	

}

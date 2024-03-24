package it.polito.oop.vaccination;
import java.util.*;
public class Hub {
	String name;
	int dottori, infermieri, altro;
	boolean personalSetted;
	
	public Hub(String name) {
		super();
		this.name = name;
		this.dottori =0;
		this.infermieri = 0;
		this.altro = 0;
		personalSetted=false;
	}
	
	public void setStaff(int doctors, int nNurses, int o) {
		dottori=doctors;
		infermieri=nNurses;
		altro=o;
		personalSetted=true;
	}
	
	public int estimateHourlyCapacity() {
		
		int d=10*dottori, i=12*infermieri, a=20*altro;
		
		return Math.min(d, Math.min(i, a));
	}
	
	public boolean isPersonalSet(){
		return personalSetted;
	}
	
	
	
	public Hub(String name, int dottori, int infermieri, int altro) {
		super();
		this.name = name;
		this.dottori = dottori;
		this.infermieri = infermieri;
		this.altro = altro;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDottori() {
		return dottori;
	}
	public void setDottori(int dottori) {
		this.dottori = dottori;
	}
	public int getInfermieri() {
		return infermieri;
	}
	public void setInfermieri(int infermieri) {
		this.infermieri = infermieri;
	}
	public int getAltro() {
		return altro;
	}
	public void setAltro(int altro) {
		this.altro = altro;
	}
	
	
	

	Map<Integer, ArrayList<String>> a = new HashMap<>();
	public void ad(int d, ArrayList<String> l){
		if(a.containsKey(d)) l.stream().forEach(s->a.get(d).add(s));	
		else a.put(d,l);	
	}	
	
}
package fr.vojtisek.adm.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "diveBudies")
public class DiveBudy {
	
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	int id;
		
	@DatabaseField
	private String firstName;
	 
	@DatabaseField
	private String lastName;
	
	public DiveBudy(){}

	public DiveBudy(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}; // needed by ormlite

}

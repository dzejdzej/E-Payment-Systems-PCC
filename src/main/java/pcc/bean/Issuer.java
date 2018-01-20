package pcc.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;



@Entity
public class Issuer implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    
    @Column(nullable = false)
    private String pan; 
    
    @Column(nullable = false, unique = true)
    private String url; 
    
    public Issuer() {
    	
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}    
}
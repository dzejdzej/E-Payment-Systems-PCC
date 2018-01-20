package pcc.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pcc.bean.Issuer;
import pcc.repository.IssuerRepository;

@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private IssuerRepository issuerRepository; 

	@RequestMapping(method = RequestMethod.GET, value = "/fill-pcc-database")
	public ResponseEntity<?> fillPCCDatabase() {
		
		Issuer issuer1 = new Issuer(); 
		issuer1.setPan("123456789");
		issuer1.setUrl("localhost:8087");
		issuerRepository.save(issuer1); 
		
		Issuer issuer2 = new Issuer(); 
		issuer2.setPan("333333333333333333");
		issuer2.setUrl("localhost:8088");
		issuerRepository.save(issuer2); 
		
		System.out.println("PCC DATABASE FILLED");
		
		return new ResponseEntity<>(HttpStatus.OK);
	}	
}

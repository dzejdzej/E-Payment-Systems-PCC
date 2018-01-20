package pcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcc.bean.Issuer;

@Repository
public interface IssuerRepository  extends JpaRepository<Issuer, Long>{
	Issuer findByPan(String pan);
}
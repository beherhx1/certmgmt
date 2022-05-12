package com.example.demo20.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo20.Certificate;

@Repository
public interface CertificateRepository<P> extends JpaRepository<Certificate, Long> {
	Certificate findByAliasAndFunction(String alias, String function);
	List<Certificate> findAllByAlias(String alias);
	List<Certificate> findAllByExpiryDate(Date expiryDate);
	List<Certificate> findAllByAliasAndExpiryDate(String alias, Date expiryDate);
}

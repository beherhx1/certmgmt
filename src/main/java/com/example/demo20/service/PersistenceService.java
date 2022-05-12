package com.example.demo20.service;

import java.util.List;
import java.util.Optional;

import com.example.demo20.Certificate;

public interface PersistenceService {
	
	Optional<Certificate> findForId(Long id);
	
	void save(Certificate certficate) throws Exception;
	
	Certificate findAllByOrderByAliasAndFunction();
	
	List<Certificate> getAllCertificates();
	
	void delete(Certificate certificate);
}

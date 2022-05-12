package com.example.demo20.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo20.Certificate;
import com.example.demo20.InvalidSourceTypeException;
import com.example.demo20.SourceType;

public interface CertificateService {
	Certificate save(
			String function, 
			String hostName, 
			String applicationName, 
			LocalDate validFrom,
			LocalDate expiryDate, 
			String alias);
	
	Certificate findCertificateByAliasAndFunction(String alias, String function);
	
	Optional<Certificate> getCertificate(long id);
	
	void updateCertificateExpiryDate(String alias, String function, LocalDate startDate, LocalDate expDate);
	
	List<Certificate> getAllCertificates();
	
	void bulk_saveFromExternalSource(SourceType sourceType, MultipartFile file) throws InvalidSourceTypeException, IOException;
}

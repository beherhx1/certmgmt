package com.example.demo20.service;

import org.springframework.stereotype.Service;

import com.example.demo20.Certificate;

@Service
public class CertificateValidationService {
	public String validate(Certificate certificate) {

		String message = "";
		try {
			if (certificate.getAlias().isEmpty() || certificate.getAlias().equals(null)) {
				message = "Alias cannot be empty or null";

			}
			if (certificate.getValidFrom() == null) {
				message = "Valid From cannot be empty or null";
			}

			if (certificate.getExpiryDate() == null) {
				message = "Expiry Date cannot be empty or null";
			}

			if (certificate.getExpiryDate() == null || certificate.getValidFrom() == null) {
				message = "Valid From Date or Expiry Date cannot be empty or null";
			}
			if (certificate.getValidFrom() != null && certificate.getExpiryDate() != null) {
				boolean isBefore = certificate.getExpiryDate().isBefore(certificate.getValidFrom());
				if (isBefore) {
					message = "Expiry Date cannot be earlier than Valid From Date ";
				}
			}

		} catch (NullPointerException npe) {
			return "Some of fields are null";
		}
		return message;
	}

}

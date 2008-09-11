package org.mariella.persistence.annotations.processing.test;

import java.sql.Blob;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="Partner")
@Table(name="PARTNER")
public class Partner extends Subject {
	private static final long serialVersionUID = 1L;

	private Address billingAddress;
	private Company ownedCompany;
	private List<Partner>employees;
	private Partner employedBy;
	private Blob myblob;

@ManyToOne(targetEntity=Address.class)
@JoinColumn(name="BILLING_ADDRESS_ID", referencedColumnName="ID")
public Address getBillingAddress() {
	return billingAddress;
}

@ManyToOne(targetEntity=Company.class)
@JoinColumn(name="OWNED_COMPANY_ID", referencedColumnName="ID")
public Company getOwnedCompany() {
	return ownedCompany;
}

@ManyToOne(targetEntity=Partner.class)
@JoinColumn(name="EMPLOYED_BY_ID", referencedColumnName="ID")
public Partner getEmployedBy() {
	return employedBy;
}

@OneToMany(targetEntity=Partner.class,mappedBy="employedBy")
public List<Partner> getEmployees() {
	return employees;
}

public void setEmployees(List<Partner> employees) {
	this.employees = employees;
}

public void setOwnedCompany(Company ownedCompany) {
	this.ownedCompany = ownedCompany;
}

public void setBillingAddress(Address billingAddress) {
	this.billingAddress = billingAddress;
}

public void setEmployedBy(Partner employedBy) {
	this.employedBy = employedBy;
}

public Blob getMyBlob() {
	return myblob;
}

public void setMyBlob(Blob b) {
	this.myblob = b;
}
}

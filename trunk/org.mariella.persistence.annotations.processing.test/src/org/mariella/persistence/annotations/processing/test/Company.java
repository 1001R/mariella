package org.mariella.persistence.annotations.processing.test;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name="Company")
@Table(name="COMPANY")
public class Company extends Partner {
	private static final long serialVersionUID = 1L;

	private Integer nrEmployees;
	private String name;
	private Partner owner;
	
	@OneToMany(mappedBy="relationOwnerCompany")
	private List<CompanyRelation> ownedRelations;

	@OneToMany(mappedBy="relatedCompany")
	private List<CompanyRelation> relations;

@Column(name="NAME",length=100)
public String getName() {
	return name;
}

@Column(name="NR_EMPLOYEES",length=6)
public Integer getNrEmployees() {
	return nrEmployees;
}

@OneToOne(targetEntity=Partner.class,mappedBy="ownedCompany")
public Partner getOwner() {
	return owner;
}

@ManyToOne(targetEntity=Partner.class)
@JoinColumn(name="X_EMPLOYED_BY_ID", referencedColumnName="ID")
public Partner getEmployedBy() {
	return super.getEmployedBy();
}


public void setName(String name) {
	this.name = name;
}

public void setNrEmployees(Integer nrEmployees) {
	this.nrEmployees = nrEmployees;
}

public void setOwner(Partner owner) {
	this.owner = owner;
}

public List<CompanyRelation> getOwnedRelations() {
	return ownedRelations;
}

public void setOwnedRelations(List<CompanyRelation> ownedRelations) {
	this.ownedRelations = ownedRelations;
}

public List<CompanyRelation> getRelations() {
	return relations;
}

public void setRelations(List<CompanyRelation> relations) {
	this.relations = relations;
}

}

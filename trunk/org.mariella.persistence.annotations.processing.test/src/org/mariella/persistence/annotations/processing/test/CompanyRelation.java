package org.mariella.persistence.annotations.processing.test;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="COMPANYRELATION")
@ExcludeSuperclassListeners
public class CompanyRelation implements Serializable {
	private static final long serialVersionUID = 1L;

@Id
@Column(name="OWNERCOMP_ID")
private Integer relationOwnerCompanyId;

@ManyToOne
@JoinColumn(name="OWNERCOMP_ID", referencedColumnName="ID")
private Company relationOwnerCompany;

@Id
@Column(name="RELATEDCOMP_ID")
private Integer relatedCompanyId;

@ManyToOne
@JoinColumn(name="RELATEDCOMP_ID", referencedColumnName="ID")
private Company relatedCompany;

@OneToMany(mappedBy="companyRelation")
private List<CompanyRelationRole> relationRoles;

public Company getRelationOwnerCompany() {
	return relationOwnerCompany;
}

public void setRelationOwnerCompany(Company company1) {
	this.relationOwnerCompany = company1;
}

public Integer getRelationOwnerCompanyId() {
	return relationOwnerCompanyId;
}

public void setRelationOwnerCompanyId(Integer company1Id) {
	this.relationOwnerCompanyId = company1Id;
}

public Company getRelatedCompany() {
	return relatedCompany;
}

public void setRelatedCompany(Company company2) {
	this.relatedCompany = company2;
}

public Integer getRelatedCompanyId() {
	return relatedCompanyId;
}

public void setRelatedCompanyId(Integer company2Id) {
	this.relatedCompanyId = company2Id;
}

public List<CompanyRelationRole> getRelationRoles() {
	return relationRoles;
}

public void setRelationRoles(List<CompanyRelationRole> relationRoles) {
	this.relationRoles = relationRoles;
}

}

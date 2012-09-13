package org.mariella.persistence.annotations.processing.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="COMPANYRELATIONROLE")
public class CompanyRelationRole extends Superclass {
	private static final long serialVersionUID = 1L;

@Column(name="OWNERCOMP_ID")
private Integer relationOwnerCompanyId;

@Column(name="RELATEDCOMP_ID")
private Integer relatedCompanyId;

@ManyToOne
@JoinColumns({
	@JoinColumn(name="OWNERCOMP_ID", referencedColumnName="OWNERCOMP_ID"),
	@JoinColumn(name="RELATEDCOMP_ID", referencedColumnName="RELATEDCOMP_ID")
})
private CompanyRelation companyRelation;

@Column(name="ROLE",nullable=false)
private String role;

public CompanyRelation getCompanyRelation() {
	return companyRelation;
}

public void setCompanyRelation(CompanyRelation companyRelation) {
	this.companyRelation = companyRelation;
}

public Integer getRelatedCompanyId() {
	return relatedCompanyId;
}

public void setRelatedCompanyId(Integer relatedCompanyId) {
	this.relatedCompanyId = relatedCompanyId;
}

public Integer getRelationOwnerCompanyId() {
	return relationOwnerCompanyId;
}

public void setRelationOwnerCompanyId(Integer relationOwnerCompanyId) {
	this.relationOwnerCompanyId = relationOwnerCompanyId;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}


}

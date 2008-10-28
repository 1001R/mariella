package org.mariella.persistence.test.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorValue(value="P")
public class PrivatAdresse extends Adresse {

public PrivatAdresse() {
	super();
}

}

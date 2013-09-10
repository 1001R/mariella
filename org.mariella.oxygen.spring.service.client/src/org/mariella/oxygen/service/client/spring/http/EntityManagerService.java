package org.mariella.oxygen.service.client.spring.http;

import org.mariella.oxygen.annotations.RemotableService;
import org.mariella.persistence.schema.SchemaDescription;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@RemotableService(name="entityManagerService")
public interface EntityManagerService {

	@Transactional(
			propagation=Propagation.REQUIRED, 
			isolation=Isolation.READ_COMMITTED,
			readOnly=true)
	SchemaDescription getSchemaDescription();
}

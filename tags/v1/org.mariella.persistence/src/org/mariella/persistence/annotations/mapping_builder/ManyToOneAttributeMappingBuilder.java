package org.mariella.persistence.annotations.mapping_builder;

import org.mariella.persistence.mapping.ManyToOneAttributeInfo;

public class ManyToOneAttributeMappingBuilder extends ToOneAttributeMappingBuilder<ManyToOneAttributeInfo> {

public ManyToOneAttributeMappingBuilder(EntityMappingBuilder entityMappingBuilder, ManyToOneAttributeInfo attributeInfo) {
	super(entityMappingBuilder, attributeInfo);
}

}

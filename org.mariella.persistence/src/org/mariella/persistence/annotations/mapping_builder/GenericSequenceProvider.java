package org.mariella.persistence.annotations.mapping_builder;

import org.mariella.persistence.database.Sequence;

// ms: die sequences pro table brauch ich das für memox v2 fallback szenario, bitte nicht entfernen.
public interface GenericSequenceProvider {

	Sequence getSequence(String primaryTableName, String generatorName);

}

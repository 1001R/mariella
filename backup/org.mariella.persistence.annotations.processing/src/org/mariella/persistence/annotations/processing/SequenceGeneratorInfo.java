package org.mariella.persistence.annotations.processing;

import javax.persistence.SequenceGenerator;

public class SequenceGeneratorInfo {
SequenceGenerator seqGenerator;

void setSeqGenerator(SequenceGenerator seqGenerator) {
	this.seqGenerator = seqGenerator;
}

public int getAllocationSize() {
	return seqGenerator.allocationSize();
}

public int getInitialValue() {
	return seqGenerator.initialValue();
}

public String getName() {
	return seqGenerator.name();
}

public String getSequenceName() {
	return seqGenerator.sequenceName();
}

}

package org.mariella.persistence.annotations.processing;

import javax.persistence.TableGenerator;

import org.mariella.persistence.mapping.OxyUnitInfo;
import org.mariella.persistence.mapping.TableGeneratorInfo;
import org.mariella.persistence.mapping.UniqueConstraintInfo;

public class TableGeneratorInfoBuilder {
	
	private TableGenerator tableGenerator;
	private OxyUnitInfo oxyUnitInfo;
	private IModelToDb translator;
	
	public TableGeneratorInfoBuilder(TableGenerator tableGenerator, OxyUnitInfo oxyUnitInfo, IModelToDb translator) {
		this.tableGenerator = tableGenerator;
		this.oxyUnitInfo = oxyUnitInfo;
		this.translator = translator;
	}

	public void buildInfo() {
		UniqueConstraintInfo[] uniqueConstraintInfos = new UniqueConstraintInfo[tableGenerator.uniqueConstraints().length];
		for (int i=0; i<tableGenerator.uniqueConstraints().length;i++) {
			uniqueConstraintInfos[i] = new UniqueConstraintInfoBuilder(tableGenerator.uniqueConstraints()[i], translator).buildUniqueConstraintInfo();
		}

		
		TableGeneratorInfo sqinfo = new TableGeneratorInfo();
		sqinfo.setAllocationSize(tableGenerator.allocationSize());
		sqinfo.setCatalog(tableGenerator.catalog());
		sqinfo.setInitialValue(tableGenerator.initialValue());
		sqinfo.setName(tableGenerator.name());
		sqinfo.setPkColumnName(tableGenerator.pkColumnName());
		sqinfo.setPkColumnValue(tableGenerator.pkColumnValue());
		sqinfo.setSchema(tableGenerator.schema());
		sqinfo.setTable(tableGenerator.table());
		sqinfo.setUniqueConstraintInfos(uniqueConstraintInfos);
		oxyUnitInfo.getTableGeneratorInfos().add(sqinfo);
	}
	

}

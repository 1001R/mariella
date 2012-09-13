package org.mariella.persistence.runtime;


public class SavePoint {

	public static SavePoint create(ModificationTracker modificationTracker) {
		if (modificationTracker instanceof AbstractModificationTrackerImpl) {
			return ((AbstractModificationTrackerImpl) modificationTracker).getSavePointSupport().createSavePoint();
		} else {
			throw new RuntimeException("SavePoints are (at the moment?) only supported for modification trackers of type AbstractModificationTrackerImpl!");
		}
	}
	
	private SavePointSupport undoSupport;
	private int saveIndex;

	SavePoint(SavePointSupport undoSupport, int saveIndex) {
		this.undoSupport = undoSupport;
		this.saveIndex = saveIndex;
	}

	int getSaveIndex() {
		return saveIndex;
	}

	public void delete() {
		undoSupport.deleteToSavePoint(this);
	}

	public void rollback() {
		undoSupport.rollbackToSavePoint(this);
	}

}

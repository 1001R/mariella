package at.hts.persistence.runtime;

import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.CollectionPropertyDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.ReferencePropertyDescription;
import at.hts.persistence.schema.RelationshipPropertyDescription;
import at.hts.persistence.schema.SchemaDescription;

public class RIListener implements ModificationTrackerListener {
	private final SchemaDescription schemaDescription;
	
	private boolean updating = false;

public RIListener(SchemaDescription schemaDescription) {
	super();
	this.schemaDescription = schemaDescription;
}

public void indexedPropertyChanged(Modifiable modifiable, String propertyName, int index, Object oldValue, Object newValue) {
	if(!updating) {
		updating = true;
		try {
			ClassDescription cd = schemaDescription.getClassDescription(modifiable.getClass().getName());
			CollectionPropertyDescription mine = (CollectionPropertyDescription)cd.getPropertyDescription(propertyName);
			RelationshipPropertyDescription reverse = mine.getReversePropertyDescription();
			if(oldValue == null) {
				// add
				if(reverse instanceof ReferencePropertyDescription) {
					Modifiable old = getReferenceProperty((Modifiable)newValue, (ReferencePropertyDescription)reverse);
					if(old != null) {
						setReferenceProperty((Modifiable)newValue, (ReferencePropertyDescription)reverse, null);
					}
					setReferenceProperty((Modifiable)newValue, (ReferencePropertyDescription)reverse, modifiable);
				} else if(reverse != null) {
					throw new IllegalStateException("n:m relationships are not supported!");
				}
			} else {
				// remove
				if(reverse instanceof ReferencePropertyDescription) {
					setReferenceProperty((Modifiable)oldValue, (ReferencePropertyDescription)reverse, null);
				} else if(reverse != null) {
					throw new IllegalStateException("n:m relationships are not supported!");
				}
			}
		} finally {
			updating = false;
		}
	} 
}

public void propertyChanged(Modifiable modifiable, String propertyName, Object oldValue, Object newValue) {
	if(!updating) {
		updating = true;
		try {
			ClassDescription cd = schemaDescription.getClassDescription(modifiable.getClass().getName());
			PropertyDescription pd = cd.getPropertyDescription(propertyName);
			if(pd instanceof ReferencePropertyDescription) {
				ReferencePropertyDescription mine = (ReferencePropertyDescription)pd;
				RelationshipPropertyDescription reverse = mine.getReversePropertyDescription();
				if(reverse != null && oldValue != null) {
					if(reverse instanceof ReferencePropertyDescription) {
						setReferenceProperty((Modifiable)oldValue, (ReferencePropertyDescription)reverse, null);
					} else {
						getTrackedList((Modifiable)oldValue, (CollectionPropertyDescription)reverse).remove(modifiable);
					}
				}
				if(newValue != null && reverse != null) {
					if(reverse instanceof ReferencePropertyDescription) {
						setReferenceProperty((Modifiable)newValue, (ReferencePropertyDescription)reverse, modifiable);
					} else {
						getTrackedList((Modifiable)newValue, (CollectionPropertyDescription)reverse).add(modifiable);
					}
				}
			}
		} finally {
			updating = false;
		}
	} 
}

private void setReferenceProperty(Modifiable receiver, ReferencePropertyDescription pd, Modifiable value) {
	ModifiableAccessor.Singleton.setValue(receiver, pd, value);
}

private Modifiable getReferenceProperty(Modifiable receiver, ReferencePropertyDescription pd) {
	return (Modifiable) ModifiableAccessor.Singleton.getValue(receiver, pd);
}

@SuppressWarnings("unchecked")
private <T extends Modifiable> TrackedList<T> getTrackedList(Modifiable receiver, CollectionPropertyDescription pd) {
	return (TrackedList<T>)ModifiableAccessor.Singleton.getValue(receiver, pd);
}

}

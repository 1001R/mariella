package org.mariella.rcp.databinding;


public class TablePropertyBindingDetails {

String header;
TableColumnImageProvider imageProvider = null;

public TablePropertyBindingDetails(String header) {
	this.header = header;
}

public TablePropertyBindingDetails(String header, TableColumnImageProvider imageProvider) {
	this.header = header;
	this.imageProvider = imageProvider;
}

public String getHeader() {
	return header;
}

public TableColumnImageProvider getImageProvider() {
	return imageProvider;
}

}

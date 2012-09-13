package org.mariella.cat.ui.flexgridlayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * <table>
 * <tr>
 * 	<td></td><td></td>
 * 	<td align="center" style="border-width:1px;border-style:solid;border-color:black">TOP CELL</td>
 * 	<td></td><td></td>
 * </tr>
 * <tr>
 * 	<td></td><td></td>
 * 	<td align="center">&uarr;<br>row space &gt;= minTopRowSpace<br>&darr;</td>
 * 	<td></td><td></td>
 * </tr>
 * <tr>
 * 	<td style="border-width:1px;border-style:solid;border-color:black">LEFT CELL</td>
 * 	<td>&larr;column space &gt;= minLeftColumnSpace&rarr;</td>
 * 	<td style="border-width:1px;border-style:solid;border-color:red">
 *   <table>
 *    <tr><td></td><td align="center">&uarr;<br>topIndent<br>&darr;</td><td></td></tr>
 *    <tr><td>&larr;leftIndent&rarr;</td><td align="center" style="border-width:1px;border-style:dashed;border-color:black">CELL CONTROL</td><td align="right">&larr;rightIndent&rarr;</td></tr>
 *    <tr><td></td><td align="center">&uarr;<br>bottomIndent<br>&darr;</td><td></td></tr>
 *   </table>
 *  </td>
 * 	<td>&larr;column space &gt;= minRightColumnSpace&rarr;</td>
 * 	<td style="border-width:1px;border-style:solid;border-color:black">RIGHT CELL</td>
 * </tr>
 * <tr>
 * 	<td></td><td></td>
 * 	<td align="center">&uarr;<br>row space &gt;= minBottomRowSpace<br>&darr;</td>
 * 	<td></td><td></td>
 * </tr>
 * <tr>
 * 	<td></td><td></td>
 * 	<td align="center" style="border-width:1px;border-style:solid;border-color:black">BOTTOM CELL</td>
 * 	<td></td><td></td>
 * </tr>
 * </table>
 */
public class FlexGridData implements Cloneable {

	int topIndent = 0;
	int leftIndent = 0;
	int rightIndent = 0;
	int bottomIndent = 0;

	/**
	 * The
	 */
	int row;
	int column;

	/**
	 * The row/column span of this cell.
	 */
	int rowSpan = 1;
	int colSpan = 1;

	/**
	 * The row/column to expand, if control space is greater than current row/column space
	 */
	int preferredSpanRow = SWT.DEFAULT;
	int preferredSpanCol = SWT.DEFAULT;

	/**
	 * The minimal relative weight of the row/column containing this cell. 0 indicates that
	 * the cell keeps it's size when the parent control resizes.
	 */
	int rowWeight = 0;
	int colWeight = 0;

	/**
	 * The minimal control width/height of the cell (excluding the indent).
	 */
	int minWidth = SWT.DEFAULT;
	int minHeight = SWT.DEFAULT;
	
	/**
	 * The minimal column width/row height of the cell (including the indent).
	 */
	int minColWidth = SWT.DEFAULT;
	int minRowHeight = SWT.DEFAULT;

	/**
	 * The minimal row height/column width of the bordering rows/columns.
	 */
	int minTopRowSpace = 0;
	int minBottomRowSpace = 0;
	int minLeftColumnSpace = 0;
	int minRightColumnSpace = 0;

	/**
	 * The alignment within the cell. FILL assigns the cell+indent bounds to the control.
	 */
	int horAlignment = SWT.DEFAULT;
	int vertAlignment = SWT.DEFAULT;

	boolean freeRowSpaceIfInvisible = false;
	boolean freeColumnSpaceIfInvisible = false;

	public FlexGridData(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public FlexGridData span(int colSpan, int rowSpan) {
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;
		return this;
	}

	public FlexGridData preferredSpanCol(int preferredSpanCol) {
		this.preferredSpanCol = Math.min(preferredSpanCol, colSpan-1);
		return this;
	}

	public FlexGridData preferredSpanRow(int preferredSpanRow) {
		this.preferredSpanRow = Math.min(preferredSpanRow, rowSpan-1);
		return this;
	}

	public FlexGridData fillWeight(int colWeight, int rowWeight) {
		this.rowWeight = rowWeight;
		this.colWeight = colWeight;
		return this;
	}

	public FlexGridData indent(int top, int left, int bottom, int right) {
		this.topIndent = top;
		this.leftIndent = left;
		this.bottomIndent = bottom;
		this.rightIndent = right;
		return this;
	}

	public FlexGridData horIndent(int left, int right) {
		this.leftIndent = left;
		this.rightIndent = right;
		return this;
	}

	public FlexGridData vertIndent(int top, int bottom) {
		this.topIndent = top;
		this.bottomIndent = bottom;
		return this;
	}

	public FlexGridData minTopRowSpace(int space) {
		this.minTopRowSpace = space;
		return this;
	}

	public FlexGridData minBottomRowSpace(int space) {
		this.minBottomRowSpace = space;
		return this;
	}

	public FlexGridData minLeftColumnSpace(int space) {
		this.minLeftColumnSpace = space;
		return this;
	}

	public FlexGridData minRightColumnSpace(int space) {
		this.minRightColumnSpace = space;
		return this;
	}

	public FlexGridData minControlSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		return this;
	}

	public FlexGridData minCellSize(int minColWidth, int minRowHeight) {
		this.minColWidth = minColWidth;
		this.minRowHeight = minRowHeight;
		return this;
	}

	public FlexGridData align(int vertAlignment, int horAlignment) {
		this.vertAlignment = vertAlignment & (SWT.TOP | SWT.CENTER | SWT.BOTTOM | SWT.FILL);
		this.horAlignment = horAlignment & (SWT.LEFT | SWT.CENTER | SWT.RIGHT | SWT.FILL);
		return this;
	}

	public FlexGridData freeSpaceIfInvisible(boolean freeRowSpaceIfInvisible, boolean freeColumnSpaceIfInvisible) {
		this.freeRowSpaceIfInvisible = freeRowSpaceIfInvisible;
		this.freeColumnSpaceIfInvisible = freeColumnSpaceIfInvisible;
		return this;
	}

	public static FlexGridData create(int row, int column) {
		return new FlexGridData(row, column);
	}

	public static FlexGridData createFor(Control control) {
		FlexGridData layoutData = new FlexGridData(-1, -1);
		control.setLayoutData(layoutData);
		return layoutData;
	}

	public static FlexGridData fromControl(Control control) {
		return (FlexGridData) control.getLayoutData();
	}

	public static void alignAll(int vertAlignment, int horAlignment, Control... controls) {
		for (Control control : controls) {
			FlexGridData layoutData = (FlexGridData) control.getLayoutData();
			layoutData.align(vertAlignment, horAlignment);
		}
	}

	public static void fillWeightAll(int colWeight, int rowWeight, Control... controls) {
		for (Control control : controls) {
			FlexGridData layoutData = (FlexGridData) control.getLayoutData();
			layoutData.fillWeight(colWeight, rowWeight);
		}
	}

	public static void sizeAll(int minWidth, int minHeight, Control... controls) {
		for (Control control : controls) {
			FlexGridData layoutData = (FlexGridData) control.getLayoutData();
			layoutData.minControlSize(minWidth, minHeight);
		}
	}

	public static void horIndentAll(int left, int right, Control... controls) {
		for (Control control : controls) {
			FlexGridData layoutData = (FlexGridData) control.getLayoutData();
			layoutData.horIndent(left, right);
		}
	}

}

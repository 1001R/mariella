package org.mariella.cat.ui.flexgridlayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class FlexGridLayout extends Layout {

	public static int PAGE_MARGIN_TOP = 20;
	public static int PAGE_MARGIN_BOTTOM = 20;
	public static int PAGE_MARGIN_LEFT = 20;
	public static int PAGE_MARGIN_RIGHT = 20;
	public static int PAGE_ROW_SPACING = 20;
	public static int PAGE_COLUMN_SPACING = 20;

	public static int SECTION_MARGIN_TOP = 2;
	public static int SECTION_MARGIN_BOTTOM = 2;
	public static int SECTION_MARGIN_LEFT = 2;
	public static int SECTION_MARGIN_RIGHT = 2;
	public static int SECTION_ROW_SPACING = 5;
	public static int SECTION_COLUMN_SPACING = 5;

	public static int DIALOG_MARGIN_TOP = 10;
	public static int DIALOG_MARGIN_BOTTOM = 2;
	public static int DIALOG_MARGIN_LEFT = 5;
	public static int DIALOG_MARGIN_RIGHT = 5;
	public static int DIALOG_ROW_SPACING = 5;
	public static int DIALOG_COLUMN_SPACING = 5;
	
	private static IFlexGridLayoutControlConfig controlConfig = null;
	
	public static void setControlConfig(IFlexGridLayoutControlConfig controlConfig) {
		FlexGridLayout.controlConfig = controlConfig;
	}
	
	public static IFlexGridLayoutControlConfig getControlConfig() {
		return controlConfig;
	}

	private static class FlexControl {
		private Control control;
		private FlexGridData layoutData;
		private int minWidth;
		private int minHeight;

		private FlexControl(Control control, FlexGridData layoutData, int minWidth, int minHeight) {
			this.control = control;
			this.layoutData = layoutData;
			this.minWidth = minWidth;
			this.minHeight = minHeight;
		}
	}

	private static class Row {
		private int bottomSpace;
		private int minHeight;
		private int weight;
		private boolean visible = true;

		private int calcWeight;
		private int calcHeight;

		private Row() {
		}
	}

	private static class Column {
		private int rightSpace;
		private int minWidth;
		private int weight;
		private boolean visible = true;

		private int calcWeight;
		private int calcWidth;

		private Column() {
		}
	}

	private static class Grid {
		private FlexControl[] flexControls;
		private Row[] rows;
		private Column[] columns;
		private int firstRowTopSpace;
		private int firstColumnLeftSpace;
		private int minWidth;
		private int minHeight;
		private int maxWidth;
		private int maxHeight;

		private Grid(FlexControl[] flexControls, Row[] rows, Column[] columns, int minWidth, int minHeight, int maxWidth, int maxHeight, int firstRowTopSpace, int firstColumnLeftSpace) {
			this.flexControls = flexControls;
			this.rows = rows;
			this.columns = columns;
			this.minWidth = minWidth;
			this.minHeight = minHeight;
			this.maxWidth = maxWidth;
			this.maxHeight = maxHeight;
			this.firstRowTopSpace = firstRowTopSpace;
			this.firstColumnLeftSpace = firstColumnLeftSpace;
		}

		private void calcForSize(int parentWidth, int parentHeight) {
			// expand to parent control-size using the fill-weights
			for (int r = 0; r < rows.length; r++) {
				if (rows[r].visible) {
					rows[r].calcHeight = rows[r].minHeight;
				} else {
					rows[r].calcHeight = 0;
				}
			}
			if (parentHeight > minHeight) {
				int heightToSplit = parentHeight-minHeight;
				int sumWeights = 0;
				int sumWeightedRowHeights = 0;
				for (int r = 0; r < rows.length; r++) {
					if (rows[r].visible && rows[r].weight > 0) {
						sumWeights += rows[r].weight;
						sumWeightedRowHeights += rows[r].calcHeight;
					}
				}
				if (sumWeights > 0) {
					int availableHeight = sumWeightedRowHeights+heightToSplit;
					int sumCalcWeights = 0;
					for (int r = 0; r < rows.length; r++) {
						if (rows[r].visible) {
							rows[r].calcWeight = Math.max(0, ((availableHeight*rows[r].weight)/sumWeights)-rows[r].calcHeight);
							sumCalcWeights += rows[r].calcWeight;
						}
					}
					if (sumCalcWeights > 0) {
						int lastY = 0;
						int currentSumWeights = 0;
						for (int r = 0; r < rows.length; r++) {
							if (rows[r].visible) {
								currentSumWeights += rows[r].calcWeight;
								int y = (heightToSplit*currentSumWeights)/sumCalcWeights;
								rows[r].calcHeight = rows[r].calcHeight+(y-lastY);
								lastY = y;
							}
						}
					}
				}
			}

			// expand to parent control-size using the fill-weights
			for (int c = 0; c < columns.length; c++) {
				if (columns[c].visible) {
					columns[c].calcWidth = columns[c].minWidth;
				} else {
					columns[c].calcWidth = 0;
				}
			}
			if (parentWidth > minWidth) {
				int widthToSplit = parentWidth-minWidth;
				int sumWeights = 0;
				int sumWeightedColumnsWidth = 0;
				for (int c = 0; c < columns.length; c++) {
					if (columns[c].visible && columns[c].weight > 0) {
						sumWeights += columns[c].weight;
						sumWeightedColumnsWidth += columns[c].calcWidth;
					}
				}
				if (sumWeights > 0) {
					int availableWidth = sumWeightedColumnsWidth+widthToSplit;
					int sumCalcWeights = 0;
					for (int c = 0; c < columns.length; c++) {
						if (columns[c].visible) {
							columns[c].calcWeight = Math.max(0, ((availableWidth*columns[c].weight)/sumWeights)-columns[c].calcWidth);
							sumCalcWeights += columns[c].calcWeight;
						}
					}
					if (sumCalcWeights > 0) {
						int lastX = 0;
						int currentSumWeights = 0;
						for (int c = 0; c < columns.length; c++) {
							if (columns[c].visible) {
								currentSumWeights += columns[c].calcWeight;
								int x = (widthToSplit*currentSumWeights)/sumCalcWeights;
								columns[c].calcWidth = columns[c].calcWidth+(x-lastX);
								lastX = x;
							}
						}
					}
				}
			}
		}
	}

	private int topMargin = 0;
	private int leftMargin = 0;
	private int rightMargin = 0;
	private int bottomMargin = 0;

	private int colSpacing = 5;
	private int rowSpacing = 5;

	private int defaultControlHorAlignment = SWT.FILL;
	private int defaultControlVertAlignment = SWT.FILL;

	private boolean makeColumnsEqualWidth = false;

	private Grid grid = null;

	public FlexGridLayout() {
	}

	public int getRowSpacing() {
		return rowSpacing;
	}

	public int getColSpacing() {
		return colSpacing;
	}
	
	private static int cmp(int v1, int v2) {
		return v1 < v2 ? -1 : (v1 == v2 ? 0 : 1);
	}

	public static void setControls(Composite parent, Control[][] controls) {
		setControls(parent, true, controls);
	}

	public static void setControls(Composite parent, boolean clearLayoutDatas, Control[][] controls) {
		Control[] childControls = parent.getChildren();
		for (int i = 0; i < childControls.length; i++) {
			FlexGridData layoutData = getLayoutData(childControls[i]);
			if (layoutData != null) {
				if (clearLayoutDatas) {
					childControls[i].setLayoutData(null);
				} else {
					layoutData.row = -1;
					layoutData.column = -1;
				}
			}
		}
		insertControls(parent, 0, controls);
	}

	public static void appendControls(Composite parent, Control[][] controls) {
		insertControls(parent, -1, controls);
	}

	public static void insertControls(Composite parent, int insertRow, Control[][] controls) {
		int lastRow = 0;
		Control[] childControls = parent.getChildren();
		for (int i = 0; i < childControls.length; i++) {
			FlexGridData layoutData = getLayoutData(childControls[i]);
			if (layoutData != null) {
				if (insertRow >= 0 && layoutData.row >= insertRow) {
					layoutData.row += controls.length;
				}
				lastRow = Math.max(lastRow, layoutData.row+1);
			}
		}
		if (insertRow < 0) {
			insertRow = lastRow;
		}
		for (int row = 0; row < controls.length; row++) {
			for (int col = 0; col < controls[row].length; col++) {
				if (controls[row][col] != null) {
					if (controls[row][col].getParent() != parent) {
						throw new RuntimeException("Wrong parent for control!");
					}
					FlexGridData layoutData = getLayoutData(controls[row][col]);
					if (layoutData == null) {
						layoutData = FlexGridData.create(-1, -1);
						preconfigureFlexGridLayoutData(controls[row][col], layoutData);
						controls[row][col].setLayoutData(layoutData);
					} else {
						layoutData.row = -1;
						layoutData.column = -1;
						layoutData.rowSpan = 1;
						layoutData.colSpan = 1;
					}
				}
			}
		}
		for (int row = 0; row < controls.length; row++) {
			for (int col = 0; col < controls[row].length; col++) {
				if (controls[row][col] != null) {
					FlexGridData layoutData = getLayoutData(controls[row][col]);
					if (layoutData.row < 0 || layoutData.column < 0) {
						layoutData.row = insertRow+row;
						layoutData.column = col;
					} else {
						layoutData.rowSpan = Math.max(layoutData.rowSpan, insertRow+row-layoutData.row+1);
						layoutData.colSpan = Math.max(layoutData.colSpan, col-layoutData.column+1);
					}
				}
			}
		}
		adjustTabOrder(parent);
		setDefaultControlAdjustment(parent);
	}

	private static void preconfigureFlexGridLayoutData(Control control, FlexGridData layoutData) {
		if (controlConfig != null) {
			controlConfig.preconfigureFlexGridLayoutData(control, layoutData);
		}
	}

	public static void adjustTabOrder(Composite parent) {
		Control[] controls = parent.getChildren();
		List<FlexControl> flexControls = new ArrayList<FlexControl>(controls.length);
		for (int i = 0; i < controls.length; i++) {
			FlexGridData layoutData = getLayoutData(controls[i]);
			if (layoutData != null && (controls[i].getStyle() & SWT.NO_FOCUS) == 0) {
				flexControls.add(new FlexControl(controls[i], layoutData, 0, 0));
			}
		}
		Collections.sort(flexControls, new Comparator<FlexControl>() {
			private int compareInt(int i1, int i2) {
				return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
			}
			@Override
			public int compare(FlexControl c1, FlexControl c2) {
				int result = compareInt(c1.layoutData.row, c2.layoutData.row);
				if (result == 0) {
					result = compareInt(c1.layoutData.column, c2.layoutData.column);
				}
				return result;
			}
		});
		Control[] tabOrderControls = new Control[flexControls.size()];
		int i = 0;
		for (FlexControl flexControl : flexControls) {
			tabOrderControls[i++] = flexControl.control;
		}
		parent.setTabList(tabOrderControls);
	}

	private static void setDefaultControlAdjustment(Composite parent) {
		FlexGridLayout layout = (FlexGridLayout) parent.getLayout();
		for (Control control : parent.getChildren()) {
			FlexGridData layoutData = getLayoutData(control);
			if (layoutData != null) {
				if (layoutData.vertAlignment == SWT.DEFAULT) {
					layoutData.vertAlignment = layout.defaultControlVertAlignment;
				}
				if (layoutData.horAlignment == SWT.DEFAULT) {
					layoutData.horAlignment = layout.defaultControlHorAlignment;
				}
			}
		}
	}
	
	/**
	 * Removes rows and columns where no controls exists. Usually used after control disposal.
	 */
	public static void pack(Composite parent) {
		Control[] controls = parent.getChildren();
		List<FlexGridData> layoutDatas = new ArrayList<FlexGridData>(controls.length);
		for (int i = 0; i < controls.length; i++) {
			FlexGridData layoutData = getLayoutData(controls[i]);
			if (layoutData != null) {
				layoutDatas.add(layoutData);
			}
		}
		// pack rows
		Collections.sort(layoutDatas, new Comparator<FlexGridData>() {
			@Override
			public int compare(FlexGridData d1, FlexGridData d2) {
				return cmp(d1.row, d2.row);
			}
		});
		int currentRow = 0;
		int rowDif = 0;
		for (FlexGridData data : layoutDatas) {
			if (data.row+data.rowSpan-1 > currentRow) {
				currentRow = data.row+data.rowSpan-1;
			} else if (data.row > currentRow) {
				rowDif += data.row-currentRow;
				currentRow = data.row;
			}
			data.row -= rowDif;
		}
		// pack columns
		Collections.sort(layoutDatas, new Comparator<FlexGridData>() {
			@Override
			public int compare(FlexGridData d1, FlexGridData d2) {
				return cmp(d1.column, d2.column);
			}
		});
		int currentColumn = 0;
		int columnDif = 0;
		for (FlexGridData data : layoutDatas) {
			if (data.column+data.colSpan-1 > currentColumn) {
				currentColumn = data.column+data.colSpan-1;
			} else if (data.column > currentColumn) {
				columnDif += data.column-currentColumn;
				currentColumn = data.column;
			}
			data.column -= columnDif;
		}
	}

	@Override
	protected Point computeSize(Composite composite, int preferredWidth, int preferredHeight, boolean flushCache) {
		if (grid == null || flushCache) {
			grid = calcGrid(composite);
		}
		int width = Math.max(grid.minWidth, Math.min(grid.maxWidth, preferredWidth));
		int height = Math.max(grid.minHeight, Math.min(grid.maxHeight, preferredHeight));
		return new Point(width, height);
	}

//	@Override
//	public int computeMinimumWidth(Composite composite, boolean flushCache) {
//		if (grid == null || flushCache) {
//			grid = calcGrid(composite);
//		}
//		return grid.minWidth;
//	}

//	@Override
//	public int computeMaximumWidth(Composite composite, boolean flushCache) {
//		if (grid == null || flushCache) {
//			grid = calcGrid(composite);
//		}
//		return grid.maxWidth;
//	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		if (grid == null || flushCache) {
			grid = calcGrid(composite);
		}
		Rectangle clientArea = composite.getClientArea();
		grid.calcForSize(clientArea.width, clientArea.height);

		int[] rowPos = new int[grid.rows.length+1];
		rowPos[0] = topMargin+grid.firstRowTopSpace;
		for (int r = 1; r < rowPos.length; r++) {
			if (grid.rows[r-1].visible) {
				rowPos[r] = rowPos[r-1]+grid.rows[r-1].calcHeight+grid.rows[r-1].bottomSpace;
			} else {
				rowPos[r] = rowPos[r-1];
			}
		}

		int[] colPos = new int[grid.columns.length+1];
		colPos[0] = leftMargin+grid.firstColumnLeftSpace;
		for (int c = 1; c < colPos.length; c++) {
			if (grid.columns[c-1].visible) {
				colPos[c] = colPos[c-1]+grid.columns[c-1].calcWidth+grid.columns[c-1].rightSpace;
			} else {
				colPos[c] = colPos[c-1];
			}
		}

		int[] x = new int[2];
		int[] y = new int[2];
		for (FlexControl flexControl : grid.flexControls) {
			int cellX1 = colPos[flexControl.layoutData.column]+flexControl.layoutData.leftIndent;
			int cellY1 = rowPos[flexControl.layoutData.row]+flexControl.layoutData.topIndent;
			int cellX2 = colPos[flexControl.layoutData.column+flexControl.layoutData.colSpan]-grid.columns[flexControl.layoutData.column+flexControl.layoutData.colSpan-1].rightSpace-flexControl.layoutData.rightIndent;
			int cellY2 = rowPos[flexControl.layoutData.row+flexControl.layoutData.rowSpan]-grid.rows[flexControl.layoutData.row+flexControl.layoutData.rowSpan-1].bottomSpace-flexControl.layoutData.bottomIndent;
			align(flexControl.minWidth, cellX2-cellX1, flexControl.layoutData.horAlignment, x);
			align(flexControl.minHeight, cellY2-cellY1, flexControl.layoutData.vertAlignment, y);
			flexControl.control.setBounds(clientArea.x+cellX1+x[0], clientArea.y+cellY1+y[0], Math.max(0, x[1]), Math.max(0, y[1]));
		}
	}

	private void align(int controlDimension, int boundDimension, int alignment, int[] result) {
		controlDimension = Math.min(controlDimension, boundDimension);
		if ((alignment & SWT.CENTER) != 0) {
			result[0] = (boundDimension-controlDimension)/2;
			result[1] = controlDimension;
		} else if ((alignment & (SWT.BOTTOM | SWT.RIGHT)) != 0) {
			result[0] = boundDimension-controlDimension;
			result[1] = controlDimension;
		} else if ((alignment & SWT.FILL) != 0) {
			result[0] = 0;
			result[1] = boundDimension;
		} else {
			result[0] = 0;
			result[1] = controlDimension;
		}
	}

	private static FlexGridData getLayoutData(Control control) {
		Object layoutData = control.getLayoutData();
		return layoutData instanceof FlexGridData ? (FlexGridData) layoutData : null;
	}

	private int cmp(int preferredSpan1, int span1, int pos11, int pos12, int preferredSpan2, int span2, int pos21, int pos22) {
		if (preferredSpan1 >= 0 && preferredSpan2 < 0) {
			return -1;
		} else if (preferredSpan1 < 0 && preferredSpan2 >= 0) {
			return 1;
		} else if (span1 < span2) {
			return -1;
		} else if (span1 > span2) {
			return 1;
		} else if (pos11 < pos21) {
			return -1;
		} else if (pos11 > pos21) {
			return 1;
		} else if (pos12 < pos22) {
			return -1;
		} else if (pos12 > pos22) {
			return 1;
		} else {
			return 0;
		}
	}

	private Grid calcGrid(Composite composite) {
		Control[] controls = composite.getChildren();
		int rowCount = 0;
		int colCount = 0;
		List<FlexControl> flexControlList = new ArrayList<FlexControl>(controls.length);
		for (Control control : controls) {
			FlexGridData layoutData = getLayoutData(control);
			if (layoutData != null) {
				rowCount = Math.max(rowCount, layoutData.row+layoutData.rowSpan);
				colCount = Math.max(colCount, layoutData.column+layoutData.colSpan);
				Point minSize = control.computeSize(layoutData.minWidth, layoutData.minHeight);
				minSize.x = Math.max(0, Math.max(layoutData.minWidth, minSize.x));
				minSize.y = Math.max(0, Math.max(layoutData.minHeight, minSize.y));
				flexControlList.add(new FlexControl(control, layoutData, minSize.x, minSize.y));
			}
		}
		FlexControl[] flexControls = flexControlList.toArray(new FlexControl[flexControlList.size()]);

		// calc row/col spaces
		Row[] rows = new Row[rowCount];
		for (int r = 0; r < rows.length; r++) {
			rows[r] = new Row();
		}
		Column[] columns = new Column[colCount];
		for (int c = 0; c < columns.length; c++) {
			columns[c] = new Column();
		}
		int firstRowTopSpace = 0;
		Arrays.sort(flexControls, new Comparator<FlexControl>() {
			@Override
			public int compare(FlexControl c1, FlexControl c2) {
				return cmp(c1.layoutData.preferredSpanRow, c1.layoutData.rowSpan, c1.layoutData.row, c1.layoutData.column,
						   c2.layoutData.preferredSpanRow, c2.layoutData.rowSpan, c2.layoutData.row, c2.layoutData.column);
			}
		});
		for (FlexControl flexControl : flexControls) {
			boolean controlVisible = flexControl.control.getVisible();
			if (!controlVisible && flexControl.layoutData.freeRowSpaceIfInvisible) {
				for (int r = flexControl.layoutData.row; r < flexControl.layoutData.row+flexControl.layoutData.rowSpan; r++) {
					rows[r].visible = false;
				}
			}
		}
		for (FlexControl flexControl : flexControls) {
			boolean controlVisible = flexControl.control.getVisible();
			if (controlVisible || !flexControl.layoutData.freeRowSpaceIfInvisible) {
				int topmostVisibleColumn = -1;
				for (int r = flexControl.layoutData.row; r < flexControl.layoutData.row+flexControl.layoutData.rowSpan && topmostVisibleColumn < 0; r++) {
					if (rows[r].visible) {
						topmostVisibleColumn = r;
					}
				}
				if (topmostVisibleColumn < 0) {
					rows[flexControl.layoutData.row].visible = true;
					topmostVisibleColumn = flexControl.layoutData.row;
				}
				if (topmostVisibleColumn == 0) {
					firstRowTopSpace = Math.max(firstRowTopSpace, flexControl.layoutData.minTopRowSpace);
				} else {
					rows[topmostVisibleColumn-1].bottomSpace = Math.max(rows[topmostVisibleColumn-1].bottomSpace, flexControl.layoutData.minTopRowSpace);
				}
				rows[flexControl.layoutData.row+flexControl.layoutData.rowSpan-1].bottomSpace = Math.max(rows[flexControl.layoutData.row+flexControl.layoutData.rowSpan-1].bottomSpace, flexControl.layoutData.minBottomRowSpace);
			}
		}
		int lastVisibleRow = -1;
		for (int r = 0; r < rows.length; r++) {
			if (rows[r].visible) {
				if (r < rows.length-1) {
					rows[r].bottomSpace = Math.max(rows[r].bottomSpace, rowSpacing);
				}
				lastVisibleRow = r;
			} else {
				if (lastVisibleRow < 0) {
					firstRowTopSpace = Math.max(firstRowTopSpace, rows[r].bottomSpace);
				} else {
					rows[lastVisibleRow].bottomSpace = Math.max(rows[lastVisibleRow].bottomSpace, rows[r].bottomSpace);
					rows[r].bottomSpace = 0;
				}
			}
		}

		int firstColumnLeftSpace = 0;
		Arrays.sort(flexControls, new Comparator<FlexControl>() {
			@Override
			public int compare(FlexControl c1, FlexControl c2) {
				return cmp(c1.layoutData.preferredSpanCol, c1.layoutData.colSpan, c1.layoutData.column, c1.layoutData.row,
						   c2.layoutData.preferredSpanCol, c2.layoutData.colSpan, c2.layoutData.column, c2.layoutData.row);
			}
		});
		for (FlexControl flexControl : flexControls) {
			boolean controlVisible = flexControl.control.getVisible();
			if (!controlVisible && flexControl.layoutData.freeColumnSpaceIfInvisible) {
				for (int c = flexControl.layoutData.column; c < flexControl.layoutData.column+flexControl.layoutData.colSpan; c++) {
					columns[c].visible = false;
				}
			}
		}
		for (FlexControl flexControl : flexControls) {
			boolean controlVisible = flexControl.control.getVisible();
			if (controlVisible || !flexControl.layoutData.freeColumnSpaceIfInvisible) {
				int firstVisibleColumn = -1;
				for (int c = flexControl.layoutData.column; c < flexControl.layoutData.column+flexControl.layoutData.colSpan && firstVisibleColumn < 0; c++) {
					if (columns[c].visible) {
						firstVisibleColumn = c;
					}
				}
				if (firstVisibleColumn < 0) {
					columns[flexControl.layoutData.column].visible = true;
					firstVisibleColumn = flexControl.layoutData.column;
				}
				if (firstVisibleColumn == 0) {
					firstColumnLeftSpace = Math.max(firstColumnLeftSpace, flexControl.layoutData.minLeftColumnSpace);
				} else {
					columns[firstVisibleColumn-1].rightSpace = Math.max(columns[firstVisibleColumn-1].rightSpace, flexControl.layoutData.minLeftColumnSpace);
				}
				columns[flexControl.layoutData.column+flexControl.layoutData.colSpan-1].rightSpace = Math.max(columns[flexControl.layoutData.column+flexControl.layoutData.colSpan-1].rightSpace, flexControl.layoutData.minRightColumnSpace);
			}
		}
		int lastVisibleColumn = -1;
		for (int c = 0; c < columns.length; c++) {
			if (columns[c].visible) {
				if (c < columns.length-1) {
					columns[c].rightSpace = Math.max(columns[c].rightSpace, colSpacing);
				}
				lastVisibleColumn = c;
			} else {
				if (lastVisibleColumn < 0) {
					firstColumnLeftSpace = Math.max(firstColumnLeftSpace, columns[c].rightSpace);
				} else {
					columns[lastVisibleColumn].rightSpace = Math.max(columns[lastVisibleColumn].rightSpace, columns[c].rightSpace);
					columns[c].rightSpace = 0;
				}
			}
		}

		// calc grid row heights
		boolean hasRowWeights = false;
		Arrays.sort(flexControls, new Comparator<FlexControl>() {
			@Override
			public int compare(FlexControl c1, FlexControl c2) {
				return cmp(c1.layoutData.preferredSpanRow, c1.layoutData.rowSpan, c1.layoutData.row, c1.layoutData.column,
						   c2.layoutData.preferredSpanRow, c2.layoutData.rowSpan, c2.layoutData.row, c2.layoutData.column);
			}
		});
		for (FlexControl flexControl : flexControls) {
			int currentMinGridHeight = 0;
			int lastRow = flexControl.layoutData.row+flexControl.layoutData.rowSpan-1;
			int prefRow = (flexControl.layoutData.preferredSpanRow >= 0) ? flexControl.layoutData.row+flexControl.layoutData.preferredSpanRow : lastRow;
			int sumWeight = 0;
			int visibleRows = 0;
			rows[prefRow].weight = Math.max(rows[prefRow].weight, flexControl.layoutData.rowWeight);
			for (int r = flexControl.layoutData.row; r <= lastRow; r++) {
				if (rows[r].visible) {
					visibleRows++;
					sumWeight += rows[r].weight;
					currentMinGridHeight += rows[r].minHeight;
					if (r < lastRow) {
						currentMinGridHeight += rows[r].bottomSpace;
					}
				}
			}
			if (flexControl.layoutData.rowWeight != 0) {
				hasRowWeights = true;
			}
			int neededMinGridHeight = Math.max(flexControl.minHeight+flexControl.layoutData.topIndent+flexControl.layoutData.bottomIndent, flexControl.layoutData.minRowHeight);
			if (neededMinGridHeight > currentMinGridHeight) {
				int spaceToShare = neededMinGridHeight-currentMinGridHeight;
				if (flexControl.layoutData.preferredSpanRow >= 0) {
					rows[flexControl.layoutData.row+flexControl.layoutData.preferredSpanRow].minHeight += spaceToShare;
				} else {
					int spaceShared = 0;
					int currentWeight = 0;
					for (int r = flexControl.layoutData.row; r <= lastRow; r++) {
						if (rows[r].visible) {
							currentWeight += (sumWeight == 0 ? 1 : rows[r].weight);
							int space = currentWeight*spaceToShare/(sumWeight == 0 ? visibleRows : sumWeight);
							rows[r].minHeight += space-spaceShared;
							spaceShared = space;
						}
					}
				}
			}
		}

		// calc grid column widths
		boolean hasColWeights = false;
		Arrays.sort(flexControls, new Comparator<FlexControl>() {
			@Override
			public int compare(FlexControl c1, FlexControl c2) {
				return cmp(c1.layoutData.preferredSpanCol, c1.layoutData.colSpan, c1.layoutData.column, c1.layoutData.row,
						   c2.layoutData.preferredSpanCol, c2.layoutData.colSpan, c2.layoutData.column, c2.layoutData.row);
			}
		});
		for (FlexControl flexControl : flexControls) {
			int currentMinGridWidth = 0;
			int lastCol = flexControl.layoutData.column+flexControl.layoutData.colSpan-1;
			int prefCol = (flexControl.layoutData.preferredSpanCol >= 0) ? flexControl.layoutData.column+flexControl.layoutData.preferredSpanCol : lastCol;
			int sumWeight = 0;
			int visibleColumns = 0;
			columns[prefCol].weight = Math.max(columns[prefCol].weight, flexControl.layoutData.colWeight);
			for (int c = flexControl.layoutData.column; c <= lastCol; c++) {
				if (columns[c].visible) {
					visibleColumns++;
					sumWeight += columns[c].weight;
					currentMinGridWidth += columns[c].minWidth;
					if (c < lastCol) {
						currentMinGridWidth += columns[c].rightSpace;
					}
				}
			}
			if (flexControl.layoutData.colWeight != 0) {
				hasColWeights = true;
			}
			int neededMinGridWidth = Math.max(flexControl.minWidth+flexControl.layoutData.leftIndent+flexControl.layoutData.rightIndent, flexControl.layoutData.minColWidth);
			if (neededMinGridWidth > currentMinGridWidth) {
				int spaceToShare = neededMinGridWidth-currentMinGridWidth;
				if (flexControl.layoutData.preferredSpanCol >= 0) {
					columns[flexControl.layoutData.column+flexControl.layoutData.preferredSpanCol].minWidth += spaceToShare;
				} else {
					int spaceShared = 0;
					int currentWeight = 0;
					for (int c = flexControl.layoutData.column; c <= lastCol; c++) {
						if (columns[c].visible) {
							currentWeight += (sumWeight == 0 ? 1 : columns[c].weight);
							int space = currentWeight*spaceToShare/(sumWeight == 0 ? visibleColumns : sumWeight);
							columns[c].minWidth += space-spaceShared;
							spaceShared = space;
						}
					}
				}
			}
		}

		// make columns equal width
		if (makeColumnsEqualWidth) {
			int maxColumnMinWidth = 0;
			for (int c = 0; c < columns.length; c++) {
				if (columns[c].visible) {
					maxColumnMinWidth = Math.max(maxColumnMinWidth, columns[c].minWidth);
				}
			}
			for (int c = 0; c < columns.length; c++) {
				if (columns[c].visible) {
					columns[c].minWidth = maxColumnMinWidth;
				}
			}
		}

		// calculate dimensions
		int minHeight = topMargin+bottomMargin+firstRowTopSpace;
		for (int i = 0; i < rows.length; i++) {
			if (rows[i].visible) {
				minHeight += rows[i].minHeight+rows[i].bottomSpace;
			}
		}

		int minWidth = leftMargin+rightMargin+firstColumnLeftSpace;
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].visible) {
				minWidth += columns[i].minWidth+columns[i].rightSpace;
			}
		}

		int maxWidth = (hasColWeights ? Integer.MAX_VALUE : minWidth);
		int maxHeight = (hasRowWeights ? Integer.MAX_VALUE : minHeight);

		return new Grid(flexControls, rows, columns, minWidth, minHeight, maxWidth, maxHeight, firstRowTopSpace, firstColumnLeftSpace);
	}

	public static FlexGridLayout fromComposite(Composite composite) {
		return (FlexGridLayout) composite.getLayout();
	}

	public static FlexGridLayout createDefault() {
		return new FlexGridLayout();
	}

	public static FlexGridLayout createForPage() {
		return createDefault()
			   .margins(PAGE_MARGIN_TOP, PAGE_MARGIN_LEFT, PAGE_MARGIN_BOTTOM, PAGE_MARGIN_RIGHT)
			   .spacing(PAGE_ROW_SPACING, PAGE_COLUMN_SPACING);
	}

	public static FlexGridLayout createForSection() {
		return createDefault()
		       .margins(SECTION_MARGIN_TOP, SECTION_MARGIN_LEFT, SECTION_MARGIN_BOTTOM, SECTION_MARGIN_RIGHT)
		       .spacing(SECTION_ROW_SPACING, SECTION_COLUMN_SPACING);
	}

	public static FlexGridLayout createForDialog() {
		return createDefault()
		       .margins(DIALOG_MARGIN_TOP, DIALOG_MARGIN_LEFT, DIALOG_MARGIN_BOTTOM, DIALOG_MARGIN_RIGHT)
		       .spacing(DIALOG_ROW_SPACING, DIALOG_COLUMN_SPACING);
	}

	public FlexGridLayout defaultControlAlignment(int vertAlignment, int horAlignment) {
		this.defaultControlVertAlignment = vertAlignment;
		this.defaultControlHorAlignment = horAlignment;
		return this;
	}

	public FlexGridLayout spacing(int rowSpacing, int colSpacing) {
		this.rowSpacing = rowSpacing;
		this.colSpacing = colSpacing;
		return this;
	}

	public FlexGridLayout margins(int top, int left, int bottom, int right) {
		this.topMargin = top;
		this.leftMargin = left;
		this.bottomMargin = bottom;
		this.rightMargin = right;
		return this;
	}

	public FlexGridLayout margins(int margin) {
		return margins(margin, margin, margin, margin);
	}
	
	public FlexGridLayout marginTop(int margin) {
		this.topMargin = margin;
		return this;
	}

	public FlexGridLayout columnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
		return this;
	}

}

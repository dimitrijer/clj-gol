package clj_gol.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Collection;
import java.util.concurrent.atomic.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import clojure.lang.IObj;
import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;

public class DrawingPanel extends JPanel {

	public static final int CELL_WIDTH = 5;
	public static final int CELL_HEIGHT = 5;

	private static final long serialVersionUID = -1;

	public DrawingPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	private static void drawCell(final Graphics g, final int i, final int j, final boolean alive) {
		final int startX = i * CELL_WIDTH;
		final int startY = j * CELL_HEIGHT;

		if (alive) {
			g.setColor(Color.BLACK);
			g.fillRect(startX, startY, CELL_WIDTH, CELL_HEIGHT);
		} else {
			g.drawRect(startX, startY, CELL_WIDTH, CELL_HEIGHT);
		}
	}

	public void repaintCell(final int i, final int j) {
		repaint(i * CELL_WIDTH, j * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
	}

	private final AtomicReference<Collection<Collection<IPersistentMap>>> cells = new AtomicReference<>();

	public void setBoard(final Collection<Collection<IPersistentMap>> cells) {
		if (cells == null) throw new NullPointerException();
		this.cells.set(cells);
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getRows() * CELL_WIDTH, getCols() * CELL_HEIGHT);
	}

	private int getRows() {
		final Collection<Collection<IPersistentMap>> cells = this.cells.get();
		return cells != null ? cells.size() : 100;
	}

	private int getCols() {
		final Collection<Collection<IPersistentMap>> cells = this.cells.get();
		return cells != null ? cells.iterator().next().size() : 100;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.cells.get() == null) {
			return;
		}

		final Keyword ki = Keyword.find("i");
		final Keyword kj = Keyword.find("j");
		final Keyword kalive = Keyword.find("alive?");
		for (Collection<IPersistentMap> row : this.cells.get()) {
			for (IPersistentMap cell : row) {
				final Long i = (Long) cell.entryAt(ki).val();
				final Long j = (Long) cell.entryAt(kj).val();
				final Boolean isAlive = (Boolean) cell.entryAt(kalive).val();
				drawCell(g, i.intValue(), j.intValue(), isAlive);
			}
		}
	}
}

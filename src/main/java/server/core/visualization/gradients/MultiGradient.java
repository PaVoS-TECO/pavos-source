package server.core.visualization.gradients;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import server.core.visualization.GradientRange;
import server.core.visualization.util.ColorUtil;

public class MultiGradient {
	
	private List<SimpleGradient> gradients = new ArrayList<>();
	private List<GradientRange> ranges = new ArrayList<>();
	public final String NAME;
	
	public MultiGradient(String name, Color... colors) {
		this.NAME = name;
	    if (colors.length < 1) {
	    	gradients.add(new SimpleGradient(new Color(0, 0, 0), new Color(0, 0, 0)));
	    } else if (colors.length == 1) {
			gradients.add(new SimpleGradient(colors[0], colors[0]));
		} else {
			for (int i = 0; i < colors.length - 1; i++) {
				gradients.add(new SimpleGradient(colors[i], colors[i + 1]));
			}
		}
	}
	
	public Color[] getColors() {
		if (gradients.size() == 0) return new Color[0];
		Color[] colors = new Color[gradients.size() + 1];
		colors[0] = gradients.get(0).getColorAt(0);
		for (int i = 0; i < gradients.size(); i++) {
			colors[i] = gradients.get(i).getColorAt(1);
		}
		return colors;
	}
	
	private String colorsToString(Color[] colors) {
		if (colors.length == 0) return "";
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < colors.length; i++) {
			builder.append(String.format("'%s'", ColorUtil.getHexFromColor(colors[i])));
			if (i < colors.length - 1) builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("'%s': { ", this.NAME));
		builder.append(gradientToString());
		ranges.forEach((range) -> builder.append(String.format(", %s", range.toString())));
		builder.append(" }");
		return builder.toString();
	}
	
	public String gradientToString() {
		return "'gradient': " + colorsToString(getColors());
	}
	
	public Collection<GradientRange> getRanges() {
		Collection<GradientRange> result = new ArrayList<>();
		ranges.forEach((range) -> result.add(range));
		return result;
	}
	
	public GradientRange getRange(String rangeName) {
		GradientRange check = new GradientRange(rangeName, 0, 0);
		return ranges.get(ranges.indexOf(check));
	}
	
	public void addRange(GradientRange range) {
		if (range != null) ranges.add(range);
	}
	
	public GradientRange removeRange(int index) {
		if (index >= 0 && index < ranges.size()) return ranges.remove(index);
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) return false;
		MultiGradient oGrad = (MultiGradient) o;
		return oGrad.NAME.equals(this.NAME);
	}
	
	@Override
	public int hashCode() {
		return this.NAME.hashCode();
	}
	
	public Color getColorAt(double position) {
		if (position < 0.0) position = 0.0;
		if (position > 1.0) position = 1.0;
		int amount = gradients.size();
		double gradLength = 1.0 / (double) amount;
		double ratio = (double) amount;
		double subPosition = 0.0;
		int gradIndex = 0;
		for (int i = 1; i < amount; i++) {
			if (position <= ((double) i) * gradLength) {
				gradIndex = i - 1;
				double subMin = gradLength * (double) gradIndex;
				subPosition = position - subMin;
			}
		}
		subPosition = subPosition * ratio;
		return gradients.get(gradIndex).getColorAt(subPosition);
	}
	
	public SimpleGradient substituteGradient(SimpleGradient grad, int index) {
		SimpleGradient result = removeGradient(index);
		addGradient(grad, index);
		return result;
	}
	
	public void addGradient(SimpleGradient grad, int index) {
		int beforeIndex = index - 1;
		SimpleGradient sg1 = this.gradients.remove(beforeIndex);
		SimpleGradient sg2 = this.gradients.remove(beforeIndex);
		sg1 = new SimpleGradient(sg1.cStart, grad.cStart);
		sg2 = new SimpleGradient(grad.cEnd, sg2.cEnd);
		this.gradients.add(beforeIndex, sg2);
		this.gradients.add(beforeIndex, sg1);
		this.gradients.add(index, grad);
	}
	
	public SimpleGradient removeGradient(int index) {
		int beforeIndex = index - 1;
		int afterIndex = index + 1;
		SimpleGradient sg2 = this.gradients.get(afterIndex);
		SimpleGradient sg1 = this.gradients.remove(beforeIndex);
		sg1 = new SimpleGradient(sg1.cStart, sg2.cStart);
		this.gradients.add(beforeIndex, sg1);
		return this.gradients.remove(index);
	}
	
}

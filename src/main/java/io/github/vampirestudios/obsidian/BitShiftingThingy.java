import java.awt.*;

void main() {
	Color color = Color.WHITE;
	System.out.println((color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue());
}

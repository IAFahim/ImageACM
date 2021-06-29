import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class TestBench {
    public static void main(String[] args) throws IOException {
        long x = System.currentTimeMillis();
        BufferedImage imageMain = ImageIO.read(new File("img.png"));
        for (int i = 0; i < 2; i++) {
            BufferedImage image = copyImage(imageMain);
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(new Font("Palace Script MT", Font.PLAIN, 124));
            g.setColor(Color.BLACK);
            String text = "Hello WorldWtf" + i;
            TextLayout textLayout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
            double textHeight = textLayout.getBounds().getHeight();
            double textWidth = textLayout.getBounds().getWidth();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g.drawString(text, image.getWidth() / 2 - (int) textWidth / 2, 380);
            g.dispose();
            ImageIO.write(image, "png", new File(i + "test.png"));
        }

        System.out.println(System.currentTimeMillis() - x);
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte) source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);
        return bi;
    }
}
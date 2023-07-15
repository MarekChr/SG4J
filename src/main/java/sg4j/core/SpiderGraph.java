package sg4j.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import sg4j.collections.SpiderGraphItemList;
import sg4j.collections.SpiderGraphValueNameList;
import sg4j.exceptions.ImageSavingIOException;
import sg4j.exceptions.InvalidImageProperties;

public class SpiderGraph {

    private int fontSize;
    private int webLinesWidth;

    public int getWebLinesWidth() {
        return this.webLinesWidth;
    }

    public void setWebLinesWidth(int webLinesWidth) {
        this.webLinesWidth = webLinesWidth;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    private int slices;

    public int getSlices() {
        return this.slices;
    }

    public void setSlices(int slices) {
        this.slices = slices;
    }

    public SpiderGraph() {
        this.webLinesWidth = 3;
        this.fontSize = 20;
        this.items = new SpiderGraphItemList();
        this.values = new SpiderGraphValueNameList(this.items);
        this.items.setSpiderGraphValueNameList(this.values);
        this.slices = 1;
    }

    private final SpiderGraphItemList items;

    public SpiderGraphItemList items() {
        return this.items;
    }

    private final SpiderGraphValueNameList values;

    public SpiderGraphValueNameList values() {
        return this.values;
    }

    private void drawContent(Graphics2D graphics, int width, int height) {
        //drawing main oval
        int verticalMargin = height / 8;
        int horizontalMargin = width / 2 - ((height - (2 * verticalMargin)) / 2);
        int radiusTimesTwo = height - (2 * verticalMargin);
        int mainOvalRadius = radiusTimesTwo / 2;

        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(this.webLinesWidth + 2));
        graphics.drawOval(horizontalMargin, verticalMargin, radiusTimesTwo, radiusTimesTwo);

        //additional setup
        graphics.setStroke(new BasicStroke(1));

        int sliceXstep = (width / 2 - horizontalMargin) / (this.slices + 1);
        int sliceYstep = (height / 2 - verticalMargin) / (this.slices + 1);

        double biggestVal = this.items.getBiggestValueAmongItems();
        double lowestVal = this.items.getSmallestValueAmongItems();
        double valueRange = this.items.getValueRange();
        double valueStep = valueRange / (this.slices + 1);

        // drawing slices (inner circles of mainOval)
        graphics.setColor(Color.LIGHT_GRAY);
        for (int i = 1; i < this.slices + 1; i++) {
            int posX = horizontalMargin + (i * sliceXstep);
            int posY = verticalMargin + (i * sliceYstep);
            graphics.drawOval(posX, posY, width - (2 * posX), height - (2 * posY));
        }

        // drawing item names
        int degreeStep = 360 / this.items.size();
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Segoe UI", Font.BOLD, this.fontSize));
        for (int i = 0; i < this.items.size(); i++) {
            double radiansX = Math.toRadians(degreeStep * i);
            double radiansY = Math.toRadians(degreeStep * i);

            double cos = Math.cos(radiansX);
            double sin = Math.sin(radiansY);

            double x = horizontalMargin + mainOvalRadius + (mainOvalRadius * cos);
            double y = verticalMargin + mainOvalRadius + (mainOvalRadius * sin);
            graphics.setColor(Color.BLACK);

            int xChange;
            int yChange;
            if (cos >= 0.0 && sin >= 0.0) {
                xChange = this.fontSize + 5;
                yChange = this.fontSize + 5;
            } else if (cos < 0.0 && sin >= 0.0) {
                xChange = -(this.items.get(i).getName().length() * (int) (this.fontSize / 1.8));
                yChange = this.fontSize + 5;
            } else if (cos < 0.0 && sin < 0.0) {
                xChange = -(this.items.get(i).getName().length() * (int) (this.fontSize / 1.8));
                yChange = -(this.fontSize + 5);
            } else {
                xChange = this.fontSize + 5;
                yChange = -(this.fontSize + 5);
            }

            graphics.drawString(this.items.get(i).getName().replaceAll("\\s+", "\n"), (float) x + xChange, (float) y + yChange);
            graphics.setColor(Color.GRAY);
            graphics.drawLine(horizontalMargin + mainOvalRadius, verticalMargin + mainOvalRadius, (int) x, (int) y);
        }

        // drawing value names and example lines
        if (this.values.size() > 0) {
            graphics.setColor(Color.black);
            int rectangleBoundX = (int) (0.8 * mainOvalRadius);
            int rectangleBoundY = (int) (0.6 * mainOvalRadius);
            graphics.setFont(new Font("Segoe UI", Font.BOLD, this.fontSize));
            graphics.drawRect(0, 0, rectangleBoundX, rectangleBoundY);
            int valueTableStepY = rectangleBoundY / this.values.size();

            for (int i = 0; i < this.values.size(); i++) {
                graphics.setStroke(new BasicStroke(1));
                graphics.setColor(Color.BLACK);
                graphics.drawString(this.values.get(i).getName(), 20, (i * valueTableStepY) + this.fontSize + 15);
                graphics.setStroke(new BasicStroke(4));
                graphics.setColor(this.values.get(i).getColor());
                graphics.drawLine((int) (rectangleBoundX * 0.6), (i * valueTableStepY) + this.fontSize / 2 + 16, (int) (rectangleBoundX * 0.8),
                        (i * valueTableStepY) + this.fontSize / 2 + 16);
            }
        }


        // drawing value lines
        int lastLineX = 0;
        int lastLineY = 0;
        int firstLineX = 0;
        int firstLineY = 0;
        graphics.setStroke(new BasicStroke(this.webLinesWidth));
        for (int i = 0; i < this.values.size(); i++) {
            SpiderGraphValueName sgvn = this.values.get(i);
            graphics.setColor(sgvn.getColor());

            for (int j = 0; j < this.items.size(); j++) {
                double radiansX = Math.toRadians(degreeStep * j);
                double radiansY = Math.toRadians(degreeStep * j);

                double currentItemValue = this.items.get(j).getValues().get(sgvn.getName());
                double percentage;
                if (biggestVal >= 0.0 && lowestVal < 0.0) {
                    percentage = (currentItemValue + Math.abs(lowestVal)) / valueRange;
                } else if (biggestVal >= 0.0 && lowestVal >= 0.0) {
                    percentage = (currentItemValue - lowestVal) / valueRange;
                } else // both less than 0.0
                {
                    percentage = (Math.abs(currentItemValue + Math.abs(lowestVal))) / valueRange;
                }

                double x = (width / 2.0 + ((mainOvalRadius * percentage) * Math.cos(radiansX)));
                double y = (height / 2.0 + ((mainOvalRadius * percentage) * Math.sin(radiansY)));


                if (j == this.items.size() - 1) {
                    graphics.drawLine((int) x, (int) y, lastLineX, lastLineY);
                    graphics.drawLine((int) x, (int) y, firstLineX, firstLineY);
                } else if (j == 0) {

                    lastLineX = (int) x;
                    lastLineY = (int) y;
                    firstLineX = lastLineX;
                    firstLineY = lastLineY;
                } else {
                    graphics.drawLine((int) x, (int) y, lastLineX, lastLineY);
                    lastLineX = (int) x;
                    lastLineY = (int) y;
                }
            }
        }


        // drawing valueSteps on slices
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Segoe UI", Font.PLAIN, this.fontSize));
        for (int i = 0; i < this.slices + 2; i++) {
            double numberToShow = Math.round((biggestVal - (i * valueStep)) * 100) / 100.0;
            graphics.drawString(Double.toString(numberToShow), (width / 2) - 25, verticalMargin + (i * sliceYstep) - 5);
        }

    }

    public BufferedImage toImage() {
        BufferedImage image = new BufferedImage((2048 / 9) * 16, 2048, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, (2048 / 9) * 16, 2048);
        if (this.items.size() == 0)
            return image;
        else {
            this.drawContent(graphics, image.getWidth(), image.getHeight());
        }
        return image;
    }

    public BufferedImage toImage(int height) {
        if (height < 500)
            throw new InvalidImageProperties("Height of Image has to be at least 500");
        BufferedImage image = new BufferedImage((height / 9) * 16, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, (height / 9) * 16, height);
        if (this.items.size() == 0)
            return image;
        else {
            this.drawContent(graphics, image.getWidth(), image.getHeight());
        }
        return image;
    }

    public void save(String imagePathAndName) throws IOException {
        File file = new File(imagePathAndName + ".png");
        Path path;
        if ((path = Paths.get(imagePathAndName).getParent()) != null) {
            Files.createDirectories(path);
        }
        file.createNewFile();
        if (file.exists()) {
            ImageIO.write(this.toImage(), "png", file);
        } else {
            throw new ImageSavingIOException("File could not be created");
        }
    }

    public void save(String imagePathAndName, int height, String format) throws IOException {
        File file = new File(imagePathAndName + "." + format);
        Path path;
        if ((path = Paths.get(imagePathAndName).getParent()) != null) {
            Files.createDirectories(path);
        }
        file.createNewFile();
        if (file.exists()) {
            ImageIO.write(this.toImage(height), format, file);

        } else {
            throw new ImageSavingIOException("File could not be created");
        }
    }

    @Override
    public String toString() {
        return "SpiderGraph [slices=" + this.slices + ", items=" + this.items + ", values=" + this.values + "]";
    }

}

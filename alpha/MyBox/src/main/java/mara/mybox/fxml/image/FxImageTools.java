package mara.mybox.fxml.image;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Random;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import mara.mybox.image.tools.AlphaTools;
import mara.mybox.image.tools.BufferedImageTools;
import mara.mybox.image.tools.ColorConvertTools;
import mara.mybox.image.data.PixelsBlend;
import mara.mybox.image.data.PixelsOperationFactory;
import mara.mybox.data.DoubleShape;
import mara.mybox.dev.MyBoxLog;
import static mara.mybox.fxml.image.FxColorTools.toAwtColor;
import mara.mybox.fxml.FxTask;
import mara.mybox.image.file.ImageFileReaders;
import mara.mybox.image.file.ImageFileWriters;
import mara.mybox.tools.FileTmpTools;

/**
 * @Author Mara
 * @CreateDate 2018-6-11 11:19:42
 * @License Apache License Version 2.0
 */
public class FxImageTools {

    public class ManufactureType {

        public static final int Brighter = 0;
        public static final int Darker = 1;
        public static final int Gray = 2;
        public static final int Invert = 3;
        public static final int Saturate = 4;
        public static final int Desaturate = 5;
    }

    public static Image createImage(int width, int height, Color color) {
        WritableImage newImage = new WritableImage(width, height);
        PixelWriter pixelWriter = newImage.getPixelWriter();
        for (int y = 0; y < newImage.getHeight(); y++) {
            for (int x = 0; x < newImage.getWidth(); x++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        return newImage;
    }

    public static Image readImage(FxTask task, File file) {
        BufferedImage bufferedImage = ImageFileReaders.readImage(task, file);
        if (bufferedImage == null) {
            return null;
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static File writeImage(FxTask task, Image image) {
        if (image == null) {
            return null;
        }
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        if (bufferedImage == null) {
            return null;
        }
        File tmpFile = FileTmpTools.getTempFile(".png");
        if (ImageFileWriters.writeImageFile(task, bufferedImage, "png", tmpFile.getAbsolutePath())
                && tmpFile.exists()) {
            return tmpFile;
        } else {
            return null;
        }
    }

    public static byte[] bytes(FxTask task, Image image, String format) {
        return BufferedImageTools.bytes(task, SwingFXUtils.fromFXImage(image, null), format);
    }

    public static String base64(FxTask task, File file, String format) {
        try {
            BufferedImage bufferedImage = ImageFileReaders.readImage(task, file);
            if (bufferedImage == null) {
                return null;
            }
            return BufferedImageTools.base64(task, bufferedImage, format);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

    public static String base64(FxTask task, Image image, String format) {
        try {
            if (image == null || format == null) {
                return null;
            }
            return BufferedImageTools.base64(task, SwingFXUtils.fromFXImage(image, null), format);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

    public static Image clone(FxTask task, Image srcImage) {
        if (srcImage == null) {
            return srcImage;
        }
        double w = srcImage.getWidth();
        double h = srcImage.getHeight();
        PixelReader pixelReader = srcImage.getPixelReader();
        WritableImage newImage = new WritableImage((int) w, (int) h);
        PixelWriter pixelWriter = newImage.getPixelWriter();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
            }
        }
        return newImage;
    }

    // This way may be more quicker than comparing digests
    public static boolean sameImage(FxTask task, Image imageA, Image imageB) {
        try {
            if (imageA == null || imageB == null
                    || imageA.getWidth() != imageB.getWidth()
                    || imageA.getHeight() != imageB.getHeight()) {
                return false;
            }
            int width = (int) imageA.getWidth(), height = (int) imageA.getHeight();
            PixelReader readA = imageA.getPixelReader();
            PixelReader readB = imageB.getPixelReader();
            for (int y = 0; y < height / 2; y++) {
                if (task != null && !task.isWorking()) {
                    return false;
                }
                for (int x = 0; x < width / 2; x++) {
                    if (task != null && !task.isWorking()) {
                        return false;
                    }
                    if (!readA.getColor(x, y).equals(readB.getColor(x, y))) {
                        return false;
                    }
                }
                for (int x = width - 1; x >= width / 2; x--) {
                    if (task != null && !task.isWorking()) {
                        return false;
                    }
                    if (!readA.getColor(x, y).equals(readB.getColor(x, y))) {
                        return false;
                    }
                }
            }
            for (int y = height - 1; y >= height / 2; y--) {
                if (task != null && !task.isWorking()) {
                    return false;
                }
                for (int x = 0; x < width / 2; x++) {
                    if (task != null && !task.isWorking()) {
                        return false;
                    }
                    if (!readA.getColor(x, y).equals(readB.getColor(x, y))) {
                        return false;
                    }
                }
                for (int x = width - 1; x >= width / 2; x--) {
                    if (task != null && !task.isWorking()) {
                        return false;
                    }
                    if (!readA.getColor(x, y).equals(readB.getColor(x, y))) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // https://stackoverflow.com/questions/19548363/image-saved-in-javafx-as-jpg-is-pink-toned
    public static BufferedImage toBufferedImage(Image image) {
        if (image == null) {
            return null;
        }
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        return source;
    }

    public static BufferedImage checkAlpha(FxTask task, Image image, String format) {
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        BufferedImage target = AlphaTools.checkAlpha(task, source, format);
        return target;
    }

    public static Image clearAlpha(FxTask task, Image image) {
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        BufferedImage target = AlphaTools.removeAlpha(task, source);
        Image newImage = SwingFXUtils.toFXImage(target, null);
        return newImage;
    }

    // This way may fail for big image
    public static Image addTextFx(Image image, String textString,
            Font font, Color color, int x, int y, float transparent, int shadow) {
        try {
            Group group = new Group();

            Text text = new Text(x, y, textString);
            text.setFill(color);
            text.setFont(font);
            if (shadow > 0) {
                DropShadow dropShadow = new DropShadow();
                dropShadow.setOffsetX(shadow);
                dropShadow.setOffsetY(shadow);
                text.setEffect(dropShadow);
            }

            group.getChildren().add(text);

            Blend blend = new Blend(BlendMode.SRC_OVER);
            blend.setBottomInput(new ImageInput(image));
            blend.setOpacity(1.0 - transparent);
            group.setEffect(blend);

            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage newImage = group.snapshot(parameters, null);
            return newImage;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

    public static Image setRound(FxTask task, Image image, int roundX, int roundY, Color bgColor) {
        if (image == null || roundX <= 0 || roundY <= 0) {
            return image;
        }
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        BufferedImage target = BufferedImageTools.setRound(task, source, roundX, roundY, toAwtColor(bgColor));
        Image newImage = SwingFXUtils.toFXImage(target, null);
        return newImage;
    }

    public static Image addShadow(FxTask task, Image image, int offsetX, int offsetY, Color color, boolean isBlur) {
        if (image == null || color == null) {
            return image;
        }
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        BufferedImage target = BufferedImageTools.addShadow(task, source, offsetX, offsetY, toAwtColor(color), isBlur);
        Image newImage = SwingFXUtils.toFXImage(target, null);
        return newImage;
    }

    public static Image blend(FxTask task, Image foreImage, Image backImage,
            int x, int y, PixelsBlend blender) {
        if (foreImage == null || backImage == null || blender == null) {
            return null;
        }
        BufferedImage source1 = SwingFXUtils.fromFXImage(foreImage, null);
        BufferedImage source2 = SwingFXUtils.fromFXImage(backImage, null);
        BufferedImage target = PixelsBlend.blend(task, source1, source2, x, y, blender);
        if (target == null) {
            target = source1;
        }
        Image newImage = SwingFXUtils.toFXImage(target, null);
        return newImage;
    }

    public static Image makeMosaic(FxTask task, Image image, DoubleShape shape, int size,
            boolean isMosaic, boolean isExcluded) {
        if (!shape.isValid()) {
            return image;
        }
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        PixelReader pixelReader = image.getPixelReader();
        WritableImage newImage = new WritableImage(w, h);
        PixelWriter pixelWriter = newImage.getPixelWriter();

        for (int y = 0; y < h; y++) {
            if (task != null && !task.isWorking()) {
                return null;
            }
            for (int x = 0; x < w; x++) {
                if (task != null && !task.isWorking()) {
                    return null;
                }
                if (isExcluded) {
                    if (DoubleShape.contains(shape, x, y)) {
                        pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
                    } else {
                        if (isMosaic) {
                            int mx = Math.max(0, Math.min(w - 1, x - x % size));
                            int my = Math.max(0, Math.min(h - 1, y - y % size));
                            pixelWriter.setColor(x, y, pixelReader.getColor(mx, my));
                        } else {
                            int fx = Math.max(0, Math.min(w - 1, x - new Random().nextInt(size)));
                            int fy = Math.max(0, Math.min(h - 1, y - new Random().nextInt(size)));
                            pixelWriter.setColor(x, y, pixelReader.getColor(fx, fy));
                        }
                    }
                } else {
                    if (DoubleShape.contains(shape, x, y)) {
                        if (isMosaic) {
                            int mx = Math.max(0, Math.min(w - 1, x - x % size));
                            int my = Math.max(0, Math.min(h - 1, y - y % size));
                            pixelWriter.setColor(x, y, pixelReader.getColor(mx, my));
                        } else {
                            int fx = Math.max(0, Math.min(w - 1, x - new Random().nextInt(size)));
                            int fy = Math.max(0, Math.min(h - 1, y - new Random().nextInt(size)));
                            pixelWriter.setColor(x, y, pixelReader.getColor(fx, fy));
                        }
                    } else {
                        pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
                    }
                }
            }

        }
        return newImage;
    }

    public static Image replaceColor(FxTask task, Image image, Color oldColor, Color newColor, int distance) {
        if (image == null || oldColor == null || newColor == null || distance < 0) {
            return image;
        }
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        BufferedImage target = PixelsOperationFactory.replaceColor(task, source,
                ColorConvertTools.converColor(oldColor), ColorConvertTools.converColor(newColor), distance);
        Image newImage = SwingFXUtils.toFXImage(target, null);
        return newImage;
    }

    public static Image applyRenderHints(Image image, Map<RenderingHints.Key, Object> hints) {
        if (image == null || hints == null) {
            return image;
        }
        BufferedImage source = SwingFXUtils.fromFXImage(image, null);
        BufferedImage target = BufferedImageTools.applyRenderHints(source, hints);
        Image newImage = SwingFXUtils.toFXImage(target, null);
        return newImage;
    }

}

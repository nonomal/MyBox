package mara.mybox.fxml.image;

import java.awt.Color;
import javafx.scene.image.Image;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.image.data.ImageScope;
import mara.mybox.image.data.PixelsOperation;
import mara.mybox.image.data.PixelsOperationFactory;
import mara.mybox.image.tools.ColorConvertTools;

/**
 * @Author Mara
 * @CreateDate 2018-11-13 12:38:14
 * @License Apache License Version 2.0
 */
public class ScopeTools {

    public static Image selectedScope(FxTask task, Image srcImage, ImageScope scope, Color bgColor,
            boolean cutMargins, boolean exclude, boolean ignoreTransparent) {
        try {
            if (scope == null) {
                return srcImage;
            } else {
                PixelsOperation pixelsOperation = PixelsOperationFactory.createFX(srcImage, scope,
                        PixelsOperation.OperationType.SelectPixels)
                        .setColorPara1(bgColor)
                        .setExcludeScope(exclude)
                        .setSkipTransparent(ignoreTransparent)
                        .setTask(task);
                Image scopeImage = pixelsOperation.startFx();
                if (cutMargins) {
                    return MarginTools.cutMarginsByColor(task, scopeImage,
                            ColorConvertTools.converColor(bgColor),
                            0, true, true, true, true);
                } else {
                    return scopeImage;
                }
            }
        } catch (Exception e) {
            MyBoxLog.debug(e);
            return null;
        }
    }

    public static Image maskScope(FxTask task, Image srcImage, ImageScope scope,
            boolean exclude, boolean ignoreTransparent) {
        try {
            PixelsOperation pixelsOperation = PixelsOperationFactory.createFX(
                    srcImage, scope,
                    PixelsOperation.OperationType.ShowScope)
                    .setExcludeScope(exclude)
                    .setSkipTransparent(ignoreTransparent)
                    .setTask(task);
            return pixelsOperation.startFx();
        } catch (Exception e) {
            MyBoxLog.debug(e);
            return null;
        }

    }

}

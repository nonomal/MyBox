package mara.mybox.data2d.operate;

import mara.mybox.data2d.Data2D;
import mara.mybox.data2d.Data2D_Edit;
import mara.mybox.data2d.writer.Data2DWriter;

/**
 * @Author Mara
 * @CreateDate 2022-2-25
 * @License Apache License Version 2.0
 */
public class Data2DSaveAs extends Data2DOperate {

    public static Data2DSaveAs writeTo(Data2D_Edit data, Data2DWriter writer) {
        if (data == null || writer == null) {
            return null;
        }
        Data2DSaveAs operate = new Data2DSaveAs();
        if (!operate.setSourceData(data)) {
            return null;
        }
        operate.addWriter(writer);
        return operate;

    }

    @Override
    public boolean handleRow() {
        try {
            targetRow = null;
            if (sourceRow == null) {
                return false;
            }
            targetRow = sourceRow;
            return true;
        } catch (Exception e) {
            showError(e.toString());
            return false;
        }
    }

    @Override
    public void handleTargetData(Data2D targetData) {
        copySourceAttriutes(targetData);
    }

}

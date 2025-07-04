package mara.mybox.fxml.style;

import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2021-7-30
 * @License Apache License Version 2.0
 */
public class StyleButton {

    public static StyleData buttonStyle(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        StyleData data = match(id);
        if (data != null) {
            return data;
        }
        return startsWith(id);
    }

    public static StyleData startsWith(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        if (id.startsWith("ok")) {
            switch (id) {
                case "okButton":
                    return new StyleData("okButton", message("OK"), "F1 / CTRL+E / ALT+E", "iconOK.png");
                default:
                    return new StyleData(id, message("OK"), "", "iconOK.png");
            }
        }
        if (id.startsWith("start")) {
            switch (id) {
                case "startButton":
                    return new StyleData(id, message("Start"), "F1 / CTRL+E / ALT+E", "iconStart.png");
                default:
                    return new StyleData(id, message("Start"), "", "iconStart.png");
            }
        }
        if (id.startsWith("save")) {
            if (id.startsWith("saveAs")) {
                switch (id) {
                    case "saveAsButton":
                        return new StyleData(id, message("SaveAs"), "F5 / CTRL+B / ALT+B", "iconSaveAs.png");
                    default:
                        return new StyleData(id, message("SaveAs"), "", "iconSaveAs.png");
                }
            }
            switch (id) {
                case "saveButton":
                    return new StyleData(id, message("Save"), "CTRL+S / ALT+S", "iconSave.png");
                case "saveTiffButton":
                    return new StyleData(id, message("SaveAsTiff"), "", "iconTIF.png");
                case "savePdfButton":
                    return new StyleData(id, message("SaveAsPDF"), "", "iconPDF.png");
                case "saveScopeButton":
                    return new StyleData(id, message("SaveScope"), "", "iconSave.png");
                default:
                    return new StyleData(id, message("Save"), "", "iconSave.png");
            }
        }
        if (id.startsWith("cancel")) {
            switch (id) {
                case "cancelButton":
                    return new StyleData(id, message("Cancel"), "ESC", "iconCancel.png");
                default:
                    return new StyleData(id, message("Cancel"), "", "iconCancel.png");
            }
        }
        if (id.startsWith("create")) {
            switch (id) {
                case "createButton":
                    return new StyleData(id, message("Create"), "CTRL+N", "iconAdd.png");
                case "createDataButton":
                    return new StyleData(id, message("CreateData"), "", "iconAdd.png");
                default:
                    return new StyleData(id, message("Create"), "", "iconAdd.png");
            }
        }
        if (id.startsWith("go")) {
            switch (id) {
                case "goButton":
                    return new StyleData(id, message("Go"), "F2 / CTRL+G / ALT+G", "iconGo.png");
                default:
                    return new StyleData(id, message("Go"), "", "iconGo.png");
            }
        }
        if (id.startsWith("newItem")) {
            return new StyleData(id, message("Add"), "", "iconNewItem.png");
        }
        if (id.startsWith("add")) {
            switch (id) {
                case "addButton":
                    return new StyleData(id, message("Add"), "CTRL+N / ALT+N", "iconAdd.png");
                case "addRowsButton":
                    return new StyleData(id, message("AddRows"), "CTRL+N / ALT+N", "iconNewItem.png");
                case "addFilesButton":
                    return new StyleData(id, message("AddFiles"), "", "iconSelectFile.png");
                case "addDirectoryButton":
                    return new StyleData(id, message("AddDirectory"), "", "iconSelectPath.png");
                case "addMenuButton":
                    return new StyleData(id, "", "", "iconAdd.png");
                default:
                    return new StyleData(id, message("Add"), "", "iconAdd.png");
            }
        }
        if (id.startsWith("pages")) {
            return new StyleData(id, message("Pages"), "", "iconPages.png");
        }
        if (id.startsWith("makeDirectory")) {
            return new StyleData(id, message("MakeDirectory"), "", "iconNewItem.png");
        }
        if (id.startsWith("clear")) {
            switch (id) {
                case "clearButton":
                    return new StyleData(id, message("Clear"), "CTRL+L / ALT+L", "iconClear.png");
                default:
                    return new StyleData(id, message("Clear"), "", "iconClear.png");
            }
        }
        if (id.startsWith("plus")) {
            return new StyleData(id, message("Add"), "", "iconPlus.png");
        }
        if (id.startsWith("query")) {
            return new StyleData(id, message("Query"), "", "iconQuery.png");
        }
        if (id.startsWith("reset")) {
            return new StyleData(id, message("Reset"), "", "iconRecover.png");
        }
        if (id.startsWith("analyse")) {
            return new StyleData(id, message("Analyse"), "", "iconAnalyse.png");
        }
        if (id.startsWith("ocr")) {
            return new StyleData(id, message("OCR"), "", "iconAnalyse.png");
        }
        if (id.startsWith("yes")) {
            return new StyleData(id, message("Yes"), "", "iconYes.png");
        }
        if (id.startsWith("confirm")) {
            return new StyleData(id, message("Confirm"), "", "iconYes.png");
        }
        if (id.startsWith("complete")) {
            return new StyleData(id, message("Complete"), "", "iconYes.png");
        }
        if (id.startsWith("select")) {
            if (id.startsWith("selectFile")) {
                return new StyleData(id, message("SelectFile"), "", "iconSelectFile.png");
            }
            if (id.startsWith("selectPath")) {
                return new StyleData(id, message("SelectPath"), "", "iconSelectPath.png");
            }
            if (id.startsWith("selectAll")) {
                switch (id) {
                    case "selectAllButton":
                        return new StyleData(id, message("SelectAll"), "CTRL+A / ALT+A", "iconSelectAll.png");
                    default:
                        return new StyleData(id, message("SelectAll"), "", "iconSelectAll.png");
                }
            }
            if (id.startsWith("selectNone")) {
                switch (id) {
                    case "selectNoneButton":
                        return new StyleData(id, message("UnselectAll"), "CTRL+O / ALT+O", "iconSelectNone.png");
                    default:
                        return new StyleData(id, message("UnselectAll"), "", "iconSelectNone.png");
                }
            }
            if (id.startsWith("selectPixels")) {
                return new StyleData(id, message("SelectPixels"), "CTRL+T / ALT+T", "iconSelect.png");
            }
            if (id.equalsIgnoreCase("selectButton")) {
                return new StyleData(id, message("Select"), "CTRL+T / ALT+T", "iconSelect.png");
            } else {
                return new StyleData(id, message("Select"), "", "iconSelect.png");
            }
        }

        if (id.startsWith("mybox")) {
            return new StyleData(id, message("MainPage"), "F8", "iconMyBox.png");
        }
        if (id.startsWith("download")) {
            return new StyleData(id, message("Download"), "", "iconDownload.png");
        }
        if (id.startsWith("upload")) {
            return new StyleData(id, message("Upload"), "", "iconUpload.png");
        }
        if (id.startsWith("default")) {
            return new StyleData(id, message("Default"), "", "iconDefault.png");
        }
        if (id.startsWith("random")) {
            if (id.startsWith("randomColors")) {
                return new StyleData(id, message("RandomColors"), "", "iconRandom.png");
            } else {
                return new StyleData(id, message("Random"), "", "iconRandom.png");
            }
        }
        if (id.startsWith("example")) {
            return new StyleData(id, message("Examples"), "", "iconExamples.png");
        }
        if (id.startsWith("history")) {
            return new StyleData(id, message("Histories"), "", "iconHistory.png");
        }
        if (id.startsWith("sql")) {
            return new StyleData(id, "SQL", "", "iconSQL.png");
        }
        if (id.startsWith("charts")) {
            return new StyleData(id, message("Charts"), "", "iconCharts.png");
        }
        if (id.startsWith("recover")) {
            switch (id) {
                case "recoverButton":
                    return new StyleData("recoverButton", message("Recover"), "CTRL+R / ALT+R", "iconRecover.png");
                default:
                    return new StyleData(id, message("Recover"), "", "iconRecover.png");
            }
        }
        if (id.startsWith("zoomIn")) {
            switch (id) {
                case "zoomInButton":
                    return new StyleData(id, message("ZoomIn"), "CTRL+3", "iconZoomIn.png");
                default:
                    return new StyleData(id, message("ZoomIn"), "", "iconZoomIn.png");
            }
        }
        if (id.startsWith("zoomOut")) {
            switch (id) {
                case "zoomOutButton":
                    return new StyleData(id, message("ZoomOut"), "CTRL+4", "iconZoomOut.png");
                default:
                    return new StyleData(id, message("ZoomOut"), "", "iconZoomOut.png");
            }
        }
        if (id.startsWith("copyToSystemClipboard")) {
            return new StyleData(id, message("CopyToSystemClipboard"), "", "iconCopySystem.png");
        }
        if (id.startsWith("copyToMyBoxClipboard")) {
            return new StyleData(id, message("CopyToMyBoxClipboard"), "", "iconCopy.png");
        }
        if (id.startsWith("CopyToClipboards")) {
            return new StyleData(id, message("CopyToClipboards"), "", "iconCopy.png");
        }
        if (id.startsWith("copy")) {
            switch (id) {
                case "copyButton":
                    return new StyleData(id, message("Copy"), "CTRL+C / ALT+C ", "iconCopy.png");
                case "copyEnglishButton":
                    return new StyleData(id, message("CopyEnglish"), "CTRL+E / ALT+E ", "iconCopy.png");
                default:
                    return new StyleData(id, message("Copy"), "", "iconCopy.png");
            }
        }
        if (id.startsWith("paste")) {
            switch (id) {
                case "pasteButton":
                    return new StyleData(id, message("Paste"), "CTRL+V / ALT+V", "iconPaste.png");
                case "pasteTxtButton":
                    return new StyleData(id, message("PasteTextAsHtml"), "", "iconPaste.png");
                case "pasteContentInSystemClipboardButton":
                    return new StyleData(id, message("PasteContentInSystemClipboard"), "", "iconPasteSystem.png");
                case "pasteContentInDataClipboardButton":
                    return new StyleData(id, message("PasteContentInMyBoxClipboard"), "", "iconPaste.png");
                default:
                    return new StyleData(id, message("Paste"), "", "iconPaste.png");
            }
        }
        if (id.startsWith("next")) {
            switch (id) {
                case "nextButton":
                    return new StyleData(id, message("Next"), "PAGE DOWN", "iconNext.png");
                case "nextFileButton":
                    return new StyleData(id, message("NextFile"), "", "iconNext.png");
                default:
                    return new StyleData(id, message("Next"), "", "iconNext.png");
            }
        }
        if (id.startsWith("previous")) {
            switch (id) {
                case "previousButton":
                    return new StyleData(id, message("Previous"), "PAGE UP", "iconPrevious.png");
                case "previousFileButton":
                    return new StyleData(id, message("PreviousFile"), "", "iconPrevious.png");
                default:
                    return new StyleData(id, message("Previous"), "", "iconPrevious.png");
            }
        }
        if (id.startsWith("backward")) {
            return new StyleData(id, message("Backward"), "", "iconPrevious.png");
        }
        if (id.startsWith("forward")) {
            return new StyleData(id, message("Forward"), "", "iconNext.png");
        }
        if (id.startsWith("more")) {
            return new StyleData(id, message("More"), "", "iconMore.png");
        }
        if (id.startsWith("palette")) {
            switch (id) {
                case "paletteManageButton":
                    return new StyleData(id, message("ColorPaletteManage"), "", "iconPalette.png");
                case "paletteAddInButton":
                    return new StyleData(id, message("AddInColorPalette"), "", "iconPalette.png");
                default:
                    return new StyleData(id, message("ColorPalette"), "", "iconPalette.png");
            }
        }
        if (id.startsWith("color")) {
            switch (id) {
                case "colorsButton":
                    return new StyleData(id, message("Colors"), "", "iconColor.png");
                default:
                    return new StyleData(id, message("Color"), "", "iconColor.png");
            }
        }
        if (id.startsWith("pixels")) {
            return new StyleData(id, message("Pixels"), "", "iconMatrix.png");
        }
        if (id.startsWith("open")) {
            if (id.startsWith("openPath") || id.startsWith("openTarget") || id.startsWith("openSource")) {
                return new StyleData(id, message("OpenDirectory"), "", "iconOpenPath.png");
            }
            return new StyleData(id, message("Open"), "", "iconSelectFile.png");
        }
        if (id.startsWith("delete")) {
            switch (id) {
                case "deleteButton":
                    return new StyleData(id, message("Delete"), "DELETE / CTRL+D / ALT+D", "iconDelete.png");
                case "deleteRowsButton":
                    return new StyleData(id, message("DeleteRows"), "DELETE / CTRL+D / ALT+D", "iconDelete.png");
                default:
                    return new StyleData(id, message("Delete"), "", "iconDelete.png");
            }
        }
        if (id.startsWith("input")) {
            return new StyleData(id, message("Input"), "", "iconInput.png");
        }
        if (id.startsWith("suggestion")) {
            return new StyleData(id, message("CodeCompletionSuggestions"), "CTRL+1 / ALT+1", "iconInput.png");
        }
        if (id.startsWith("verify")) {
            return new StyleData(id, message("Validate"), "", "iconVerify.png");
        }
        if (id.startsWith("data")) {
            if (id.startsWith("dataManufacture")) {
                return new StyleData(id, message("DataManufacture"), "", "iconData.png");
            } else if (id.startsWith("database")) {
                return new StyleData(id, message("DatabaseTable"), "", "iconDatabase.png");
            } else if (id.startsWith("dataDefinition")) {
                return new StyleData(id, message("DefineData"), "", "iconMeta.png");
            } else if (id.startsWith("dataImport")) {
                return new StyleData(id, message("Import"), "", "iconImport.png");
            } else if (id.startsWith("dataExport")) {
                return new StyleData(id, message("Export"), "", "iconExport.png");
            } else if (id.startsWith("dataA")) {
                return new StyleData(id, message("SetAsDataA"), "", "iconA.png");
            } else if (id.startsWith("dataBB")) {
                return new StyleData(id, message("SetAsDataB"), "", "iconB.png");
            } else {
                return new StyleData(id, message("Data"), "", "iconData.png");
            }
        }
        if (id.startsWith("query")) {
            return new StyleData(id, message("Query"), "", "iconData.png");
        }
        if (id.startsWith("map")) {
            return new StyleData(id, message("Map"), "", "iconMap.png");
        }
        if (id.startsWith("import")) {
            return new StyleData(id, message("Import"), "", "iconImport.png");
        }
        if (id.startsWith("export")) {
            return new StyleData(id, message("Export"), "", "iconExport.png");
        }
        if (id.startsWith("sureButton")) {
            return new StyleData(id, message("Sure"), "", "iconYes.png");
        }
        if (id.startsWith("fill")) {
            return new StyleData(id, message("Fill"), "", "iconButterfly.png");
        }
        if (id.startsWith("cat")) {
            return new StyleData(id, message("Meow"), "", "iconCat.png");
        }
        if (id.startsWith("help")) {
            return new StyleData(id, message("HelpMe"), "", "iconClaw.png");
        }
        if (id.startsWith("edit")) {
            if (id.startsWith("editFrames")) {
                return new StyleData(id, message("ImagesEditor"), "", "iconThumbsList.png");
            }
            return new StyleData(id, message("Edit"), "", "iconEdit.png");
        }
        if (id.startsWith("size")) {
            return new StyleData(id, message("Size"), "", "iconSplit.png");
        }
        if (id.startsWith("equal")) {
            return new StyleData(id, message("EqualTo"), "", "iconEqual.png");
        }

        if (id.startsWith("use")) {
            return new StyleData(id, message("Use"), "", "iconYes.png");
        }
        if (id.startsWith("refresh")) {
            return new StyleData(id, message("Refresh"), "", "iconRefresh.png");
        }

        if (id.startsWith("manufacture")) {
            return new StyleData(id, message("Manufacture"), "", "iconEdit.png");
        }
        if (id.startsWith("run")) {
            return new StyleData(id, message("Run"), "", "iconRun.png");
        }
        if (id.startsWith("info")) {
            switch (id) {
                case "infoButton":
                    return new StyleData(id, message("Information"), "CTRL+I", "iconInfo.png");
                default:
                    return new StyleData(id, message("Information"), "", "iconInfo.png");
            }
        }
        if (id.startsWith("view")) {
            switch (id) {
                case "viewFileButton":
                    return new StyleData(id, message("Open"), "", "iconView.png");
                default:
                    return new StyleData(id, message("View"), "", "iconView.png");
            }
        }
        if (id.startsWith("html")) {
            return new StyleData(id, message("Html"), "", "iconHtml.png");
        }
        if (id.startsWith("pdf")) {
            return new StyleData(id, "PDF", "", "iconPDF.png");
        }
        if (id.startsWith("link")) {
            return new StyleData(id, message("Link"), "", "iconLink.png");
        }
        if (id.startsWith("stop")) {
            return new StyleData(id, message("Stop"), "", "iconStop.png");
        }
        if (id.startsWith("synchronize")) {
            switch (id) {
                case "synchronizeButton":
                    return new StyleData(id, message("SynchronizeChangesToOtherPanes"), "F10", "iconSynchronize.png");
                default:
                    return new StyleData(id, message("SynchronizeChangesToOtherPanes"), "", "iconSynchronize.png");
            }
        }
        if (id.startsWith("function")) {
            return new StyleData(id, message("Functions"), "", "iconFunction.png");
        }
        if (id.startsWith("style")) {
            return new StyleData(id, message("Style"), "", "iconStyle.png");
        }
        if (id.startsWith("paneSize")) {
            switch (id) {
                case "paneSizeButton":
                    return new StyleData(id, message("PaneSize"), "CTRL+2", "iconPaneSize.png");
                default:
                    return new StyleData(id, message("PaneSize"), "", "iconPaneSize.png");
            }
        }
        if (id.startsWith("panes")) {
            return new StyleData(id, message("Panes"), "", "iconPanes.png");
        }
        if (id.startsWith("extract")) {
            return new StyleData(id, message("Extract"), "", "iconExtract.png");
        }
        if (id.startsWith("demo")) {
            return new StyleData(id, message("Demo"), "", "iconDemo.png");
        }
        if (id.startsWith("count")) {
            return new StyleData(id, message("Count"), "", "iconCalculator.png");
        }
        if (id.startsWith("delimiter")) {
            return new StyleData(id, message("Delimiter"), "", "iconDelimiter.png");
        }
        if (id.startsWith("comma")) {
            return new StyleData(id, message("Comma"), "", "iconDelimiter.png");
        }

        if (id.startsWith("matrix")) {
            if (id.startsWith("matrixA")) {
                return new StyleData(id, message("SetAsMatrixA"), "", "iconA.png");
            } else if (id.startsWith("matrixB")) {
                return new StyleData(id, message("SetAsMatrixB"), "", "iconB.png");
            } else {
                return new StyleData(id, message("Matrix"), "", "iconMatrix.png");
            }
        }
        if (id.startsWith("tableDefinition")) {
            return new StyleData(id, message("TableDefinition"), "", "iconInfo.png");
        }
        if (id.startsWith("width")) {
            return new StyleData(id, message("Width"), "", "iconXRuler.png");
        }

        if (id.startsWith("preview")) {
            switch (id) {
                case "previewButton":
                    return new StyleData(id, message("Preview"), "F3 / CTRL+U / ALT+U", "iconPreview.png");
                default:
                    return new StyleData(id, message("Preview"), "", "iconPreview.png");
            }
        }
        if (id.startsWith("rotateLeft")) {
            return new StyleData(id, message("RotateLeft"), "", "iconRotateLeft.png");
        }
        if (id.startsWith("rotateRight")) {
            return new StyleData(id, message("RotateRight"), "", "iconRotateRight.png");
        }
        if (id.startsWith("turnOver")) {
            return new StyleData(id, message("TurnOver"), "", "iconTurnOver.png");
        }
        if (id.startsWith("rename")) {
            return new StyleData(id, message("Rename"), "", "iconInput.png");
        }
        if (id.startsWith("header")) {
            return new StyleData(id, "", "", "iconHeader.png");
        }
        if (id.startsWith("list")) {
            return new StyleData(id, message("List"), "", "iconList.png");
        }
        if (id.startsWith("codes")) {
            return new StyleData(id, message("Codes"), "", "iconMeta.png");
        }
        if (id.startsWith("fold")) {
            return new StyleData(id, message("Fold"), "", "iconMinus.png");
        }
        if (id.startsWith("unfold")) {
            return new StyleData(id, message("Unfold"), "", "iconPlus.png");
        }
        if (id.startsWith("moveData")) {
            return new StyleData(id, message("Move"), "", "iconMove.png");
        }
        if (id.startsWith("tree")) {
            if (id.startsWith("treeLocate")) {
                return new StyleData(id, message("Locate"), "", "iconTree.png");
            } else {
                return new StyleData(id, message("DataTree"), "", "iconTree.png");
            }
        }
        if (id.startsWith("csv")) {
            return new StyleData(id, "CSV", "", "iconCSV.png");
        }
        if (id.startsWith("excel")) {
            return new StyleData(id, "Excel", "", "iconExcel.png");
        }

        if (id.startsWith("ssl")) {
            return new StyleData(id, "SSL", "", "iconSSL.png");
        }
        if (id.startsWith("github")) {
            return new StyleData(id, "github", "", "iconGithub.png");
        }
        if (id.startsWith("txt")) {
            return new StyleData(id, message("Texts"), "", "iconTxt.png");
        }
        if (id.startsWith("clipboard")) {
            return new StyleData(id, message("Clipboard"), "", "iconClipboard.png");
        }
        if (id.startsWith("myBoxClipboard")) {
            return new StyleData(id, message("MyBoxClipboard"), "CTRL+M", "iconClipboard.png");
        }
        if (id.startsWith("systemClipboard")) {
            return new StyleData(id, message("SystemClipboard"), "CTRL+J", "iconSystemClipboard.png");
        }
        if (id.startsWith("loadContentInSystemClipboard")) {
            return new StyleData(id, message("LoadContentInSystemClipboard"), "", "iconImageSystem.png");
        }
        if (id.startsWith("number")) {
            return new StyleData(id, "", "", "iconNumber.png");
        }
        if (id.startsWith("trim")) {
            if (id.startsWith("trimData")) {
                return new StyleData(id, message("Trim"), "", "iconClean.png");
            } else {
                return new StyleData(id, "", "", "iconNumber.png");
            }
        }
        if (id.startsWith("lowerLetter")) {
            return new StyleData(id, "", "", "iconLowerLetter.png");
        }
        if (id.startsWith("upperLetter")) {
            return new StyleData(id, "", "", "iconUpperLetter.png");
        }
        if (id.startsWith("character")) {
            return new StyleData(id, "", "", "iconCharacter.png");
        }
        if (id.startsWith("menu")) {
            switch (id) {
                case "menuButton":
                    return new StyleData(id, message("ContextMenuTips"), "", "iconMenu.png");
                default:
                    return new StyleData(id, message("ContextMenu"), "", "iconMenu.png");
            }
        }
        if (id.startsWith("close")) {
            if (id.startsWith("closePop")) {
                return new StyleData(id, message("Close"), "ESC", "iconCancel.png");
            }
            switch (id) {
                case "closeButton":
                    return new StyleData("closeButton", message("Close"), "", "iconClose.png");
                default:
                    return new StyleData(id, message("Close"), "", "iconClose.png");
            }
        }
        if (id.startsWith("disconnect")) {
            return new StyleData(id, message("Disconnect"), "", "iconClose.png");
        }
        if (id.startsWith("permission")) {
            return new StyleData(id, message("Permissions"), "", "iconPermission.png");
        }
        if (id.startsWith("message")) {
            return new StyleData(id, message("SendMessage"), "", "iconMessage.png");
        }
        if (id.startsWith("pop")) {
            switch (id) {
                case "popButton":
                    return new StyleData(id, message("Pop"), "CTRL+P / ALT+P", "iconPop.png");
                case "popScopeButton":
                    return new StyleData(id, message("PopScope"), "", "iconTarget.png");
                default:
                    return new StyleData(id, message("Pop"), "", "iconPop.png");
            }
        }
        if (id.startsWith("play")) {
            switch (id) {
                case "playButton":
                    return new StyleData(id, message("Play"), "F1 / CTRL+E / ALT+E", "iconPlay.png");
                default:
                    return new StyleData(id, message("Play"), "", "iconPlay.png");
            }
        }
        if (id.startsWith("sort")) {
            return new StyleData(id, message("Sort"), "", "iconSort.png");
        }
        if (id.startsWith("minus")) {
            return new StyleData(id, message("Minus"), "", "iconMinus.png");
        }
        if (id.startsWith("calculate")) {
            return new StyleData(id, message("Calculate"), "", "iconCalculator.png");
        }
        if (id.startsWith("columnsAdd")) {
            return new StyleData(id, message("AddColumns"), "", "iconColumnAdd.png");
        }
        if (id.startsWith("set")) {
            switch (id) {
                case "setButton":
                    return new StyleData(id, message("Set"), "", "iconEqual.png");
                case "setAllOrSelectedButton":
                    return new StyleData(id, message("SetAllOrSelected"), "", "iconEqual.png");
                case "setValuesButton":
                    return new StyleData(id, message("SetValues"), "", "iconEqual.png");
                case "settingsClearButton":
                    return new StyleData(id, message("ClearPersonalSettings"), "", "iconClear.png");
                case "settingsOpenButton":
                    return new StyleData(id, message("OpenDataPath"), "", "iconOpenPath.png");
                case "settingsRecentOKButton":
                    return new StyleData(id, message("OK"), "", "iconOK.png");
                case "settingsJVMButton":
                    return new StyleData(id, message("OK"), "", "iconOK.png");
                case "settingsRecentClearButton":
                    return new StyleData(id, message("Clear"), "", "iconClear.png");
                case "settingsChangeRootButton":
                    return new StyleData(id, message("Change"), "", "iconOK.png");
                case "settingsImageHisOKButton":
                    return new StyleData(id, message("OK"), "", "iconOK.png");
                case "settingsImageHisNoButton":
                    return new StyleData(id, message("NotRecord"), "", "iconCancel.png");
                case "settingsImageHisClearButton":
                    return new StyleData(id, message("Clear"), "", "iconClear.png");
                default:
                    return new StyleData(id, message("Set"), "", "iconEqual.png");
            }
        }
        if (id.startsWith("hex")) {
            return new StyleData(id, message("FormattedHexadecimal"), "", "iconHex.png");
        }
        if (id.startsWith("operation")) {
            return new StyleData(id, message("Operations"), "", "iconOperation.png");
        }
        if (id.startsWith("location")) {
            return new StyleData(id, message("Location"), "", "iconLocation.png");
        }
        if (id.startsWith("locate")) {
            return new StyleData(id, message("Locate"), "", "iconLocation.png");
        }
        if (id.startsWith("imageSize")) {
            switch (id) {
                case "imageSizeButton":
                    return new StyleData(id, message("LoadedSize"), "CTRL+1", "iconLoadSize.png");
                default:
                    return new StyleData(id, message("LoadedSize"), "", "iconLoadSize.png");
            }
        }
        if (id.startsWith("validate")) {
            return new StyleData(id, message("Validate"), "", "iconAnalyse.png");
        }
        if (id.startsWith("script")) {
            return new StyleData(id, message("Script"), "", "iconScript.png");
        }
        if (id.startsWith("moveUp")) {
            return new StyleData(id, message("MoveUp"), "", "iconUp.png");
        }
        if (id.startsWith("moveDown")) {
            return new StyleData(id, message("MoveDown"), "", "iconDown.png");
        }
        if (id.startsWith("moveLeft")) {
            return new StyleData(id, message("MoveLeft"), "", "iconLeft.png");
        }
        if (id.startsWith("moveRight")) {
            return new StyleData(id, message("MoveRight"), "", "iconRight.png");
        }
        if (id.startsWith("insert")) {
            switch (id) {
                case "insertFilesButton":
                    return new StyleData("insertFilesButton", message("InsertFiles"), "", "iconInsertFile.png");
                case "insertDirectoryButton":
                    return new StyleData("insertDirectoryButton", message("InsertDirectory"), "", "iconInsertPath.png");
                default:
                    return new StyleData(id, message("Insert"), "", "iconInsert.png");
            }
        }
        if (id.startsWith("XYChart")) {
            return new StyleData(id, message("XYChart"), "", "iconXYChart.png");
        }
        if (id.startsWith("jar")) {
            return new StyleData(id, message("JarFile"), "", "iconJar.png");
        }
        if (id.startsWith("options")) {
            return new StyleData(id, message("Options"), "", "iconOptions.png");
        }
        if (id.startsWith("systemMethod")) {
            return new StyleData(id, message("SystemMethod"), "", "iconSystemOpen.png");
        }
        if (id.startsWith("panesMenu")) {
            return new StyleData(id, message("Panes"), "", "iconPanes.png");
        }
        if (id.startsWith("backup")) {
            return new StyleData(id, message("FileBackups"), "", "iconBackup.png");
        }
        if (id.startsWith("file")) {
            switch (id) {
                case "fileMenuButton":
                    return new StyleData(id, message("File"), "", "iconFile.png");
                case "filesListButton":
                    return new StyleData(id, message("FilesList"), "", "iconList.png");
                default:
                    return new StyleData(id, message("File"), "", "iconFile.png");
            }
        }
        if (id.startsWith("frame")) {
            return new StyleData(id, message("Frames"), "", "iconFrame.png");
        }
        if (id.startsWith("canvas")) {
            return new StyleData(id, message("Canvas"), "", "iconRectangle.png");
        }
        if (id.startsWith("target")) {
            return new StyleData(id, message("Target"), "", "iconTarget.png");
        }
        if (id.startsWith("scope")) {
            return new StyleData(id, message("Scope"), "", "iconTarget.png");
        }
        if (id.startsWith("shape")) {
            return new StyleData(id, message("Shape"), "", "iconShape.png");
        }
        if (id.startsWith("manage")) {
            return new StyleData(id, message("Manage"), "", "iconManage.png");
        }
        if (id.startsWith("draw")) {
            return new StyleData(id, message("Draw"), "", "iconDraw.png");
        }
        if (id.startsWith("format")) {
            return new StyleData(id, message("Typesetting"), "", "iconFormat.png");
        }
        if (id.startsWith("typesetting")) {
            return new StyleData(id, message("Typesetting"), "", "iconFormat.png");
        }
        if (id.startsWith("translate")) {
            return new StyleData(id, message("TranslateShape"), "", "iconMove.png");
        }
        if (id.startsWith("transform")) {
            return new StyleData(id, message("Transform"), "", "iconReplace.png");
        }
        if (id.startsWith("anchor")) {
            return new StyleData(id, message("Anchor"), "", "iconAnchor.png");
        }
        if (id.startsWith("withdraw")) {
            switch (id) {
                case "withdrawButton":
                    return new StyleData(id, message("WithdrawLastItem"), "CTRL+W / ALT+W", "iconWithdraw.png");
                default:
                    return new StyleData(id, message("WithdrawLastItem"), "", "iconWithdraw.png");
            }
        }
        if (id.startsWith("onTop")) {
            return new StyleData(id, message("AlwayOnTop"), "", "iconTop.png");
        }
        if (id.startsWith("disableOnTop")) {
            return new StyleData(id, message("DisableAlwayOnTop"), "", "iconTopDisable.png");
        }
        if (id.startsWith("placeholders")) {
            return new StyleData(id, message("Placeholders"), "", "iconList.png");
        }

        return null;
    }

    public static StyleData match(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        switch (id) {
            case "cropButton":
                return new StyleData("cropButton", message("Crop"), "CTRL+X / ALT+X", "iconCrop.png");
            case "metaButton":
                return new StyleData("metaButton", message("MetaData"), "", "iconMeta.png");
            case "brButton":
                return new StyleData(id, message("Newline"), "", "iconBr.png");
            case "statisticButton":
                return new StyleData("statisticButton", message("Statistic"), "", "iconStatistic.png");
            case "firstButton":
                return new StyleData("firstButton", message("First"), "ALT+HOME", "iconFirst.png");
            case "lastButton":
                return new StyleData("lastButton", message("Last"), "ALT+END", "iconLast.png");
            case "redoButton":
                return new StyleData("redoButton", message("Redo"), "CTRL+Y / ALT+Y", "iconRedo.png");
            case "undoButton":
                return new StyleData("undoButton", message("Undo"), "CTRL+Z / ALT+Z", "iconUndo.png");
            case "findNextButton":
                return new StyleData("findNextButton", message("Next"), "CTRL+2", "iconNext.png");
            case "findPreviousButton":
                return new StyleData("findPreviousButton", message("Previous"), "CTRL+1", "iconPrevious.png");
            case "findButton":
                return new StyleData(id, message("Find"), "CTRL+F", "iconFind.png");
            case "replaceButton":
                return new StyleData("replaceButton", message("Replace"), "CTRL+H", "iconReplace.png");
            case "replaceAllButton":
                return new StyleData("replaceAllButton", message("ReplaceAll"), "CTRL+W", "iconReplaceAll.png");
            case "browseButton":
                return new StyleData("browseButton", message("Browse"), "", "iconBrowse.png");
            case "mirrorHButton":
                return new StyleData("mirrorHButton", message("MirrorHorizontal"), "", "iconHorizontal.png");
            case "mirrorVButton":
                return new StyleData("mirrorVButton", message("MirrorVertical"), "", "iconVertical.png");
            case "shearButton":
                return new StyleData("shearButton", message("Shear"), "", "iconShear.png");
            case "testButton":
                return new StyleData("testButton", message("Test"), "", "iconGo.png");
            case "newTabButton":
                return new StyleData(id, message("NewTab"), "", "iconPlus.png");
            case "strightButton":
                return new StyleData("strightButton", message("Straighten"), "", "iconRefresh.png");
            case "activeButton":
                return new StyleData("activeButton", message("Active"), "", "iconActive.png");
            case "inactiveButton":
                return new StyleData("inactiveButton", message("Inactive"), "", "iconInactive.png");
            case "thumbsListButton":
                return new StyleData("thumbsListButton", message("ThumbnailsList"), "", "iconThumbsList.png");
            case "gridButton":
                return new StyleData(id, message("Grid"), "", "iconBrowse.png");
            case "snapshotButton":
                return new StyleData("snapshotButton", message("SnapshotWindow"), "", "iconSnapshot.png");
            case "splitButton":
                return new StyleData("splitButton", message("Split"), "", "iconSplit.png");
            case "sampleButton":
                return new StyleData("sampleButton", message("Sample"), "", "iconSample.png");
            case "calculatorButton":
                return new StyleData("calculatorButton", message("PixelsCalculator"), "", "iconCalculator.png");
            case "filterButton":
                return new StyleData("filterButton", message("Filter"), "", "iconFilter.png");
            case "pauseButton":
                return new StyleData("pauseButton", message("Pause"), "", "iconPause.png");
            case "loadButton":
                return new StyleData("loadButton", message("Load"), "", "iconGo.png");
            case "increaseButton":
                return new StyleData("increaseButton", message("Increase"), "", "iconPlus.png");
            case "decreaseButton":
                return new StyleData("decreaseButton", message("Decrease"), "", "iconMinus.png");
            case "invertButton":
                return new StyleData("invertButton", message("Invert"), "", "iconInvert.png");
            case "originalButton":
                return new StyleData("originalButton", message("OriginalSize"), "", "iconOriginalSize.png");
            case "xmlButton":
                return new StyleData("xmlButton", "XML", "", "iconXML.png");
            case "refreshHeaderButton":
                return new StyleData("refreshHeaderButton", message("Refresh"), "", "iconRefresh.png");
            case "refreshXmlButton":
                return new StyleData("refreshXmlButton", message("Refresh"), "", "iconRefresh.png");
            case "refreshCieDataButton":
                return new StyleData("refreshCieDataButton", message("Refresh"), "", "iconRefresh.png");
            case "calculateXYZButton":
                return new StyleData("calculateXYZButton", message("Calculate"), "", "iconCalculator.png");
            case "calculateXYButton":
                return new StyleData("calculateXYButton", message("Calculate"), "", "iconCalculator.png");
            case "displayDataButton":
                return new StyleData("displayDataButton", message("Display"), "", "iconGraph.png");
            case "valueOkButton":
                return new StyleData("valueOkButton", message("OK"), "", "iconOK.png");
            case "multiplyButton":
                return new StyleData("multiplyButton", message("Multiply"), "", "iconMultiply.png");
            case "moveTopButton":
                return new StyleData("moveTopButton", message("MoveTop"), "", "iconDoubleUp.png");
            case "pagePreviousButton":
                return new StyleData("pagePreviousButton", message("PreviousPage"), "ALT+PAGE_UP", "iconPrevious.png");
            case "pageNextButton":
                return new StyleData("pageNextButton", message("NextPage"), "ALT+PAGE_DOWN", "iconNext.png");
            case "pageFirstButton":
                return new StyleData("pageFirstButton", message("FirstPage"), "ALT+HOME", "iconFirst.png");
            case "pageLastButton":
                return new StyleData("pageLastButton", message("LastPage"), "ALT+END", "iconLast.png");
            case "refreshHtmlButton":
                return new StyleData("refreshHtmlButton", message("Refresh"), "", "iconRefresh.png");
            case "refreshTextButton":
                return new StyleData("refreshTextButton", message("Refresh"), "", "iconRefresh.png");
            case "refreshMarkdownButton":
                return new StyleData("refreshMarkdownButton", message("Refresh"), "", "iconRefresh.png");
            case "streamMediaButton":
                return new StyleData(id, message("StreamMedia"), "", "iconLink.png");
            case "pickColorButton":
                return new StyleData(id, message("PickColor"), "", "iconPickColor.png");
            case "chinaButton":
                return new StyleData(id, message("China"), "", "iconChina.png");
            default:
                return null;
        }
    }

}

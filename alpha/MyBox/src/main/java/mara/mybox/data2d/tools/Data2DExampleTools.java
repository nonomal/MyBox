package mara.mybox.data2d.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import mara.mybox.controller.BaseController;
import mara.mybox.controller.BaseData2DLoadController;
import mara.mybox.data.FunctionsList;
import mara.mybox.data.StringTable;
import mara.mybox.data2d.DataFileCSV;
import mara.mybox.data2d.DataFileText;
import mara.mybox.data2d.DataMatrix;
import mara.mybox.db.data.ColumnDefinition.ColumnType;
import mara.mybox.db.data.Data2DColumn;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxFileTools;
import mara.mybox.fxml.HelpTools;
import static mara.mybox.fxml.style.NodeStyleTools.attributeTextStyle;
import mara.mybox.fxml.style.StyleTools;
import mara.mybox.tools.DateTools;
import mara.mybox.tools.FileDeleteTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.value.Languages;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2022-9-15
 * @License Apache License Version 2.0
 */
public class Data2DExampleTools {

    public static List<MenuItem> examplesMenu(BaseData2DLoadController controller) {
        try {
            List<MenuItem> items = new ArrayList<>();
            String lang = Languages.embedFileLang();

            items.add(myData(lang, controller));

            items.add(statisticDataOfChina(lang, controller));

            items.add(regression(lang, controller));

            items.add(location(lang, controller));

            items.add(matrix(lang, controller));

            items.add(projectManagement(lang, controller));

            items.add(SoftwareTesting(lang, controller));

            items.add(new SeparatorMenuItem());

            CheckMenuItem onlyMenu = new CheckMenuItem(message(lang, "ImportDefinitionOnly"),
                    StyleTools.getIconImageView("iconHeader.png"));
            onlyMenu.setSelected(UserConfig.getBoolean("Data2DExampleImportDefinitionOnly", false));
            onlyMenu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    UserConfig.setBoolean("Data2DExampleImportDefinitionOnly", onlyMenu.isSelected());
                }
            });
            items.add(onlyMenu);

            return items;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu myData(String lang, BaseData2DLoadController controller) {
        try {
            Menu myMenu = new Menu(message(lang, "MyData"), StyleTools.getIconImageView("iconCat.png"));

            MenuItem menu = new MenuItem(message(lang, "Notes"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = Notes(lang);
                if (makeExampleFile("MyData_notes_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "Contacts"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = Contacts(lang);
                if (makeExampleFile("MyData_contacts_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "CashFlow"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = CashFlow(lang);
                if (makeExampleFile("MyData_cashflow_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "PrivateProperty"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = PrivateProperty(lang);
                if (makeExampleFile("MyData_property_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            myMenu.getItems().add(new SeparatorMenuItem());

            menu = new MenuItem(message(lang, "Eyesight"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = Eyesight(lang);
                if (makeExampleFile("MyData_eyesight", data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "Weight"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = Weight(lang);
                if (makeExampleFile("MyData_weight", data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "Height"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = Height(lang);
                if (makeExampleFile("MyData_height", data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "Menstruation"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = Menstruation(lang);
                if (makeExampleFile("MyData_menstruation", data)) {
                    controller.loadDef(data);
                }
            });
            myMenu.getItems().add(menu);

            return myMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu statisticDataOfChina(String lang, BaseData2DLoadController controller) {
        try {
            // https://data.stats.gov.cn/index.htm
            Menu chinaMenu = new Menu(message(lang, "StatisticDataOfChina"), StyleTools.getIconImageView("iconChina.png"));

            MenuItem menu = new MenuItem(message(lang, "ChinaPopulation"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaPopulation(lang);
                if (makeExampleFile("ChinaPopulation", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaCensus"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaCensus(lang);
                if (makeExampleFile("ChinaCensus", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaGDP"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaGDP(lang);
                if (makeExampleFile("ChinaGDP", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaCPI"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaCPI(lang);
                if (makeExampleFile("ChinaCPI", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaFoodConsumption"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaFoodConsumption(lang);
                if (makeExampleFile("ChinaFoods_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaGraduates"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaGraduates(lang);
                if (makeExampleFile("ChinaGraduates", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaMuseums"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaMuseums(lang);
                if (makeExampleFile("ChinaMuseums", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaHealthPersonnel"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaHealthPersonnel(lang);
                if (makeExampleFile("ChinaHealthPersonnel", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaMarriage"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaMarriage(lang);
                if (makeExampleFile("ChinaMarriage", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ChinaSportWorldChampions"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChinaSportWorldChampions(lang);
                if (makeExampleFile("ChinaSportWorldChampions", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "CrimesFiledByChinaPolice"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = CrimesFiledByChinaPolice(lang);
                if (makeExampleFile("ChinaCrimesFiledByPolice", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "CrimesFiledByChinaProcuratorate"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = CrimesFiledByChinaProcuratorate(lang);
                if (makeExampleFile("ChinaCrimesFiledByProcuratorate", data)) {
                    controller.loadDef(data);
                }
            });
            chinaMenu.getItems().add(menu);

            chinaMenu.getItems().add(new SeparatorMenuItem());

            menu = new MenuItem(message(lang, "ChinaNationalBureauOfStatistics"));
            menu.setStyle(attributeTextStyle());
            menu.setOnAction((ActionEvent event) -> {
                controller.browse("https://data.stats.gov.cn/");
            });
            chinaMenu.getItems().add(menu);

            return chinaMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu regression(String lang, BaseData2DLoadController controller) {
        try {
            Menu regressionMenu = new Menu(message(lang, "RegressionData"),
                    StyleTools.getIconImageView("iconLinearPgression.png"));

            MenuItem menu = new MenuItem(message(lang, "IncomeHappiness"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = IncomeHappiness(lang);
                if (makeExampleFile("DataAnalyse_IncomeHappiness", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ExperienceSalary"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ExperienceSalary(lang);
                if (makeExampleFile("DataAnalyse_ExperienceSalary", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "IrisSpecies"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = IrisSpecies(lang);
                if (makeExampleFile("DataAnalyse_IrisSpecies", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DiabetesPrediction"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = DiabetesPrediction(lang);
                if (makeExampleFile("DataAnalyse_DiabetesPrediction", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DiabetesPredictionStandardized"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = DiabetesPredictionStandardized(lang);
                if (makeExampleFile("DataAnalyse_DiabetesPrediction_standardized", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "HeartFailure"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = HeartFailure(lang);
                if (makeExampleFile("DataAnalyse_HeartFailure", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ConcreteCompressiveStrength"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ConcreteCompressiveStrength(lang);
                if (makeExampleFile("DataAnalyse_ConcreteCompressiveStrength", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DogRadiographsDataset"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = DogRadiographsDataset(lang);
                if (makeExampleFile("DataAnalyse_DogRadiographs", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "BaseballSalaries"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = BaseballSalaries(lang);
                if (makeExampleFile("DataAnalyse_BaseballSalaries", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "SouthGermanCredit"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = SouthGermanCredit(lang);
                if (makeExampleFile("DataAnalyse_SouthGermanCredit", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "BostonHousingPrices"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = BostonHousingPrices(lang);
                if (makeExampleFile("DataAnalyse_BostonHousingPrices", data)) {
                    controller.loadDef(data);
                }
            });
            regressionMenu.getItems().add(menu);

            regressionMenu.getItems().add(new SeparatorMenuItem());

            menu = new MenuItem(message(lang, "AboutDataAnalysis"));
            menu.setStyle(attributeTextStyle());
            menu.setOnAction((ActionEvent event) -> {
                controller.openHtml(HelpTools.aboutDataAnalysis());
            });
            regressionMenu.getItems().add(menu);

            return regressionMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu location(String lang, BaseData2DLoadController controller) {
        try {

            Menu locationMenu = new Menu(message(lang, "LocationData"),
                    StyleTools.getIconImageView("iconLocation.png"));

            MenuItem menu = new MenuItem(message(lang, "ChineseHistoricalCapitals"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ChineseHistoricalCapitals(lang);
                if (makeExampleFile("Location_ChineseHistoricalCapitals_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "AutumnMovementPatternsOfEuropeanGadwalls"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = AutumnMovementPatternsOfEuropeanGadwalls(lang);
                if (makeExampleFile("Location_EuropeanGadwalls", data)) {
                    controller.loadDef(data);
                }
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "SpermWhalesGulfOfMexico"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = SpermWhalesGulfOfMexico(lang);
                if (makeExampleFile("Location_SpermWhales", data)) {
                    controller.loadDef(data);
                }
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "EpidemicReportsCOVID19"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = EpidemicReportsCOVID19(lang);
                if (makeExampleFile("Location_EpidemicReports", data)) {
                    controller.loadDef(data);
                }
            });
            locationMenu.getItems().add(menu);

            return locationMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu matrix(String lang, BaseData2DLoadController controller) {
        try {

            Menu locationMenu = new Menu(message(lang, "Matrix"),
                    StyleTools.getIconImageView("iconMatrix.png"));

            short scale1 = 4, scale2 = 8;
            int max = 100000;
            MenuItem menu = new MenuItem(message(lang, "DoubleMatrix") + " 10*10");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Double", 10, 10, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DoubleMatrix") + " "
                    + message(lang, "PureDecimal") + " 10*10");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Double", 10, 10, scale2, 1);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DoubleMatrix") + " 50*50");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Double", 50, 50, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DoubleMatrix") + " "
                    + message(lang, "PureDecimal") + " 50*50");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Double", 50, 50, scale2, 1);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DoubleMatrix") + " 500*500");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Double", 500, 500, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DoubleMatrix") + " 1000*1000");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Double", 1000, 1000, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "FloatMatrix") + " 50*50");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Float", 50, 50, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "FloatMatrix") + " "
                    + message(lang, "PureDecimal") + " 50*50");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Float", 50, 50, scale2, 1);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "IntegerMatrix") + " 50*50");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("Integer", 50, 50, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "BooleanMatrix") + " 50*50");
            menu.setOnAction((ActionEvent event) -> {
                DataMatrix matrix = DataMatrix.makeMatrix("NumberBoolean", 50, 50, scale1, max);
                controller.loadDef(matrix);
            });
            locationMenu.getItems().add(menu);

            return locationMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu projectManagement(String lang, BaseData2DLoadController controller) {
        try {

            Menu pmMenu = new Menu(message(lang, "ProjectManagement"),
                    StyleTools.getIconImageView("iconCalculator.png"));

            MenuItem menu = new MenuItem(message(lang, "ProjectRegister"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ProjectRegister(lang);
                if (makeExampleFile("PM_ProjectRegister_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ProjectStatus"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ProjectStatus(lang);
                if (makeExampleFile("PM_ProjectStatus_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "TaskRegister"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = TaskRegister(lang);
                if (makeExampleFile("PM_TaskRegister_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "TaskStatus"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = TaskStatus(lang);
                if (makeExampleFile("PM_TaskStatus_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "PersonRegister"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = PersonRegister(lang);
                if (makeExampleFile("PM_PersonRegister_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "PersonStatus"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = PersonStatus(lang);
                if (makeExampleFile("PM_PersonStatus_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ResourceRegister"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ResourceRegister(lang);
                if (makeExampleFile("PM_ResourceRegister_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "ResourceStatus"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = ResourceStatus(lang);
                if (makeExampleFile("PM_ResourceStatus_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "RiskAnalysis"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = RiskAnalysis(lang);
                if (makeExampleFile("PM_RiskAnalysis_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "CostRecord"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = CostRecord(lang);
                if (makeExampleFile("PM_CostRecords_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "VerificationRecord"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = VerificationRecord(lang);
                if (makeExampleFile("PM_VerifyRecord_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            pmMenu.getItems().add(menu);

            return pmMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static Menu SoftwareTesting(String lang, BaseData2DLoadController controller) {
        try {

            Menu stMenu = new Menu(message(lang, "SoftwareTesting"), StyleTools.getIconImageView("iconVerify.png"));

            MenuItem menu = new MenuItem(message(lang, "TestEnvironment"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = TestEnvironment(lang);
                if (makeExampleFile("ST_TestEnvironment_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            stMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "CompatibilityTesting"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = CompatibilityTesting(lang);
                if (makeExampleFile("ST_CompatibilityTesting_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            stMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "DetailedTesting"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = DetailedTesting(lang);
                if (makeExampleFile("ST_DetailedTesting_" + lang, data)) {
                    controller.loadDef(data);
                }
            });
            stMenu.getItems().add(menu);

            menu = new MenuItem(message(lang, "MyBoxBaseVerificationList"));
            menu.setOnAction((ActionEvent event) -> {
                DataFileCSV data = MyBoxBaseVerificationList(controller, lang,
                        UserConfig.getBoolean("Data2DExampleImportDefinitionOnly", false));
                controller.loadDef(data);
            });
            stMenu.getItems().add(menu);

            return stMenu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static boolean makeExampleFile(String fileName, DataFileCSV targetData) {
        try {
            if (fileName == null || targetData == null) {
                return false;
            }
            File srcFile = FxFileTools.getInternalFile("/data/examples/" + fileName + ".csv");
            File targetFile = targetData.tmpFile(fileName, "example", "csv");
            if (targetFile.exists()) {
                targetFile.delete();
            }
            Charset charset = Charset.forName("utf-8");
            try (BufferedReader reader = new BufferedReader(new FileReader(srcFile, charset));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, charset, false))) {
                String line = null;
                while ((line = reader.readLine()) != null && line.startsWith(DataFileText.CommentsMarker)) {
                    writer.write(line + System.lineSeparator());
                }
                if (line != null) {
                    List<Data2DColumn> columns = targetData.getColumns();
                    if (columns != null) {
                        String header = null;
                        for (Data2DColumn column : columns) {
                            String name = "\"" + column.getColumnName() + "\"";
                            if (header == null) {
                                header = name;
                            } else {
                                header += "," + name;
                            }
                        }
                        writer.write(header + System.lineSeparator());
                    } else {
                        writer.write(line + System.lineSeparator());
                    }
                    if (!UserConfig.getBoolean("Data2DExampleImportDefinitionOnly", false)) {
                        while ((line = reader.readLine()) != null) {
                            writer.write(line + System.lineSeparator());
                        }
                    }
                }
                writer.flush();
            } catch (Exception e) {
                MyBoxLog.error(e);
                return false;
            }
            targetData.setFile(targetFile).setHasHeader(true).setCharset(charset).setDelimiter(",");
            targetData.saveAttributes();
            FileDeleteTools.delete(srcFile);
            return true;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return false;
        }
    }

    /*
        exmaples of data 2D definition
     */
    public static DataFileCSV Notes(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, 180));
        columns.add(new Data2DColumn(message(lang, "Title"), ColumnType.String, 180));
        columns.add(new Data2DColumn(message(lang, "InvolvedObjects"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Location"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "Notes"));
        return data;
    }

    public static DataFileCSV Contacts(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Name"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Relationship"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "PhoneNumber") + "1", ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "PhoneNumber") + "2", ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Email"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Address"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Birthday"), ColumnType.Datetime));
        columns.add(new Data2DColumn(message(lang, "Hobbies"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "Contacts"));
        return data;
    }

    public static DataFileCSV CashFlow(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, 180));
        columns.add(new Data2DColumn(message(lang, "Amount"), ColumnType.Float));
        columns.add(new Data2DColumn(message(lang, "Type"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Account"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "InvolvedObjects"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "CashFlow"));
        return data;
    }

    public static DataFileCSV PrivateProperty(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, 180));
        columns.add(new Data2DColumn(message(lang, "Amount"), ColumnType.Float));
        columns.add(new Data2DColumn(message(lang, "Type"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Account"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "PrivateProperty"));
        return data;
    }

    public static DataFileCSV Eyesight(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, true, true).setWidth(180));
        columns.add(new Data2DColumn(isChinese ? "左眼" : "left eye", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "右眼" : "right eye", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "基弧" : "Radian", ColumnType.Short));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "Eyesight"));
        return data;
    }

    public static DataFileCSV Weight(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, true, true).setWidth(180));
        columns.add(new Data2DColumn(message(lang, "Weight") + "(kg)", ColumnType.Float).setScale(2));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "Weight"));
        return data;
    }

    public static DataFileCSV Height(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, true, true).setWidth(180));
        columns.add(new Data2DColumn(message(lang, "Height") + "(cm)", ColumnType.Float).setScale(2));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "Height"));
        return data;
    }

    public static DataFileCSV Menstruation(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "StartTime"), ColumnType.Datetime, true).setWidth(180));
        columns.add(new Data2DColumn(message(lang, "EndTime"), ColumnType.Datetime, true).setWidth(180));
        columns.add(new Data2DColumn(isChinese ? "疼痛" : "Pain", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "卫生巾" : "Pads", ColumnType.Short));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String, 300));
        data.setColumns(columns).setDataName(message(lang, "Menstruation"));
        return data;
    }

    public static DataFileCSV ChinaPopulation(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "年末总人口(万人)" : "population at year-end(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "男性人口(万人)" : "male(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "女性人口(万人)" : "female(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "城镇人口(万人)" : "urban(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "乡村人口(万人)" : "rural(ten thousand)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaPopulation"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaCensus(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "人口普查总人口(万人)" : "total population of census(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "男性(万人)" : "male(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "女性(万人)" : "female(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "性别比(女=100)" : "sex ratio(female=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "城镇(万人)" : "urban(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "乡村(万人)" : "rural(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "家庭户规模(人/户)" : "family size", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "0-14岁占比(%)" : "aged 0-14(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "15-64岁占比(%)" : "aged 15-64(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "65岁及以上占比(%)" : "aged over 65(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "汉族(万人)" : "han nationality population(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "汉族占比(%)" : "han nationality precentage(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "少数民族(万人)" : "minority nationality population(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "少数民族占比(%)" : "minority nationality precentage(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "每十万人口中受大专及以上教育人口数(人)" : "junior college or above education per one hundred thousand", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "每十万人口中受高中和中专教育人口数(人)" : "high school and secondary education per hundred thousand", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "每十万人口中受初中教育人口数(人)" : "junior high school education per hundred thousand", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "每十万人口中受小学教育人口数(人)" : "primary school education per hundred thousand", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "文盲人口数(万人)" : "illiteracy(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "文盲率(%)" : "illiteracy percentage(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "城镇化率(%)" : "urbanization rate(%)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "平均预期寿命(岁)" : "average life expectancy(years)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "男性平均预期寿命(岁)" : "male average life expectancy(years)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "女性平均预期寿命(岁)" : "female average life expectancy(years)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaCensus"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaGDP(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "国民总收入(GNI 亿元)" : "gross national income(GNI hundred million yuan)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "国内生产总值(GDP 亿元)" : "gross domestic product(GDP hundred million yuan)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "第一产业增加值(VA1 亿元)" : "value-added of first industry(VA1 hundred million yuan)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "第二产业增加值(VA2 亿元)" : "value-added of secondary industry(VA2 hundred million yuan)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "第三产业增加值(VA3 亿元)" : "value-added of tertiary industry(VA3 hundred million yuan)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "人均国内生产总值(元)" : "GDP per capita(yuan)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaGDP"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaCPI(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "居民消费价格指数(CPI 上年=100)" : "consumer price index(CPI last_year=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "城市居民消费价格指数(上年=100)" : "urban consumer price index(last_year=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "农村居民消费价格指数(上年=100)" : "rural consumer price index(last_year=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "商品零售价格指数(RPI 上年=100)" : "retail price index(RPI last_year=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "工业生产者出厂价格指数(PPI 上年=100)" : "producer price index(PPI last_year=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "工业生产者购进价格指数(PPIRM 上年=100)" : "producer price pndices of raw material(PPIRM last_year=100)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "固定资产投资价格指数(上年=100)" : "price indices of investment in fixed assets(last_year=100)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaCPI"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaFoodConsumption(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "指标" : "item", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "2020年" : "year 2020", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2019年" : "year 2019", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2018年" : "year 2018", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2017年" : "year 2017", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2016年" : "year 2016", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2015年" : "year 2015", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2014年" : "year 2014", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "2013年" : "year 2013", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaFoodConsumption"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaGraduates(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "普通高等学校毕业生数(万人)" : "college graduates(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "普通中学毕业生数(万人)" : "middle school graduates(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "普通高中毕业生数(万人)" : "high school graduates(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "初中毕业生数(万人)" : "junior high school graduates(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "职业中学毕业生数(万人)" : "vocational high school graduates(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "普通小学毕业生数(万人)" : "primary school graduates(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "特殊教育学校毕业生数(万人)" : "special education school graduates(ten thousand)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaGraduates"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaMuseums(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "博物馆机构数(个)" : "museum institutions", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "博物馆从业人员(人)" : "employed", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "博物馆文物藏品(件/套)" : "relics", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "本年博物馆从有关部门接收文物数(件/套)" : "received in the year", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "本年博物馆修复文物数(件/套)" : "fixed in the year", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "博物馆考古发掘项目(个)" : "archaeology projects", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "博物馆基本陈列(个)" : "basical exhibition", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "博物馆举办展览(个)" : "special exhibition", ColumnType.Long));
        columns.add(new Data2DColumn(isChinese ? "博物馆参观人次(万人次)" : "visits(ten thousand)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaMuseums"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaHealthPersonnel(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "卫生人员数(万人)" : "health personnel(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "卫生技术人员数(万人)" : "medical personnel(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "执业(助理)医师数(万人)" : "practitioner(assistant)(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "执业医师数(万人)" : "practitioner(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "注册护士数(万人)" : "registered nurse(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "药师数(万人)" : "pharmacist(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "乡村医生和卫生员数(万人)" : "rural(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "其他技术人员数(万人)" : "other technical personnel(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "管理人员数(万人)" : "managerial personnel(ten thousand)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "工勤技能人员数(万人)" : "worker(ten thousand)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaHealthPersonnel"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaMarriage(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "结婚登记(万对)" : "married(ten thousand pairs)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "内地居民登记结婚(万对)" : "mainland residents married(ten thousand pairs)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "内地居民初婚登记(万人)" : "mainland residents newly married(ten thousand persons)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "内地居民再婚登记(万人)" : "mainland residents remarried(ten thousand persons)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "涉外及港澳台居民登记结婚(万对)" : "foreigners/HongKong/Macao/Taiwan married(ten thousand pairs)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "离婚登记(万对)" : "divorced(ten thousand pairs)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "粗离婚率(千分比)" : "divorced ratio(permillage)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ChinaMarriage"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV ChinaSportWorldChampions(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "世界冠军项数" : "categories of world champions", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "女子世界冠军项数" : "categories of female world champions", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "世界冠军人数" : "athletes of world champions", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "女子世界冠军人数" : "female athletes of world champions", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "世界冠军个数" : "number of world champions", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "女子世界冠军个数" : "number of female world champions", ColumnType.Integer));
        data.setColumns(columns).setDataName(message(lang, "ChinaSportWorldChampions"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV CrimesFiledByChinaPolice(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "立案的刑事案件" : "filed crimes", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "杀人" : "murder", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "伤害" : "injure", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "抢劫" : "rob", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "强奸" : "rape", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "拐卖妇女儿童" : "trafficking", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "盗窃" : "steal", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "诈骗" : "scam", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "走私" : "smuggle", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "假币" : "counterfeit money", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "其他" : "others", ColumnType.Integer));
        data.setColumns(columns).setDataName(message(lang, "CrimesFiledByChinaPolice"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV CrimesFiledByChinaProcuratorate(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年" : "year_", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "受案数" : "filed crimes", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "贪污贿赂" : "corruption and bribery", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "贪污" : "corruption", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "贿赂" : "bribery", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "挪用公款" : "embezzlement", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "集体私分" : "collectively dividing up state-owned properties without permission", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "巨额财产来源不明" : "huge unidentified property", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "其他贪污贿赂" : "other corruption and bribery", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "渎职" : "malfeasance", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "滥用职权" : "abuses of power", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "玩忽职守" : "neglecting of duty", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "徇私舞弊" : "favoritism", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "其他渎职" : "other malfeasance", ColumnType.Integer));
        data.setColumns(columns).setDataName(message(lang, "CrimesFiledByChinaProcuratorate"))
                .setComments("https://data.stats.gov.cn/index.htm");
        return data;
    }

    public static DataFileCSV IncomeHappiness(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "收入" : "income", ColumnType.Double, true));
        columns.add(new Data2DColumn(isChinese ? "快乐" : "happiness", ColumnType.Double, true));
        data.setColumns(columns).setDataName(message(lang, "IncomeHappiness"))
                .setComments("https://www.scribbr.com/statistics/simple-linear-regression/");
        return data;
    }

    public static DataFileCSV ExperienceSalary(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "工作经验" : "YearsExperience", ColumnType.Double, true));
        columns.add(new Data2DColumn(isChinese ? "薪资" : "Salary", ColumnType.Double, true));
        data.setColumns(columns).setDataName(message(lang, "ExperienceSalary"))
                .setComments("https://github.com/krishnaik06/simple-Linear-Regression");
        return data;
    }

    public static DataFileCSV IrisSpecies(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "花萼长度(cm)" : "SepalLengthCm", ColumnType.Double, true));
        columns.add(new Data2DColumn(isChinese ? "花萼宽度(cm)" : "SepalWidthCm", ColumnType.Double, true));
        columns.add(new Data2DColumn(isChinese ? "花瓣长度(cm)" : "PetalLengthCm", ColumnType.Double, true));
        columns.add(new Data2DColumn(isChinese ? "花瓣宽度(cm)" : "PetalWidthCm", ColumnType.Double, true));
        columns.add(new Data2DColumn(isChinese ? "种类" : "Species", ColumnType.String, true));
        data.setColumns(columns).setDataName(message(lang, "IrisSpecies"))
                .setComments("http://archive.ics.uci.edu/ml/datasets/Iris");
        return data;
    }

    public static DataFileCSV DiabetesPrediction(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年龄" : "age", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "性别" : "sex", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "BMI(体质指数)" : "BMI(body mass index)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "BP(平均血压)" : "BP(average blood pressure)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S1(血清指标1)" : "S1(blood serum measurement 1)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S2(血清指标2)" : "S2(blood serum measurement 2)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S3(血清指标3)" : "S3(blood serum measurement 3)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S4(血清指标4)" : "S4(blood serum measurement 4)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S5(血清指标5)" : "S5(blood serum measurement 5)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S6(血清指标6)" : "S6(blood serum measurement 6)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "一年后病情进展" : "disease progression one year after baseline", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "DiabetesPrediction"))
                .setComments("https://hastie.su.domains/Papers/LARS/");
        return data;
    }

    public static DataFileCSV DiabetesPredictionStandardized(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年龄" : "age", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "性别" : "sex", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "BMI(体质指数)" : "BMI(body mass index)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "BP(平均血压)" : "BP(average blood pressure)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S1(血清指标1)" : "S1(blood serum measurement 1)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S2(血清指标2)" : "S2(blood serum measurement 2)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S3(血清指标3)" : "S3(blood serum measurement 3)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S4(血清指标4)" : "S4(blood serum measurement 4)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S5(血清指标5)" : "S5(blood serum measurement 5)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "S6(血清指标6)" : "S6(blood serum measurement 6)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "一年后病情进展" : "disease progression one year after baseline", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "DiabetesPredictionStandardized"))
                .setComments("https://hastie.su.domains/Papers/LARS/ \n"
                        + "first 10 columns have been normalized to have mean 0 and "
                        + "Euclidean norm 1 and the last column y has been centered (mean 0).");
        return data;
    }

    public static DataFileCSV HeartFailure(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "年龄" : "age", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "贫血" : "anaemia", ColumnType.Boolean)
                .setDescription("decrease of red blood cells or hemoglobin (boolean)"));
        columns.add(new Data2DColumn(isChinese ? "肌酐磷酸激酶(CPK_mcg/L)" : "creatinine_phosphokinase(CPK_mcg/L)", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "糖尿病" : "diabetes", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "喷血分数" : "ejection fraction", ColumnType.Integer)
                .setDescription("percentage of blood leaving the heart at each contraction (percentage)"));
        columns.add(new Data2DColumn(isChinese ? "高血压" : "high blood pressure", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "血小板(kiloplatelets/mL)" : "platelets(kiloplatelets/mL)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "血清肌酸酐(mg/dL)" : "serum creatinine(mg/dL)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "血清钠(mEq/L)" : "serum sodium(mEq/L)", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "性别" : "sex", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "抽烟" : "smoking", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "观察期" : "follow-up period(days)", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "死亡" : "death event", ColumnType.Boolean));
        data.setColumns(columns).setDataName(message(lang, "HeartFailure"))
                .setComments("http://archive.ics.uci.edu/ml/datasets/Heart+failure+clinical+records \n"
                        + "Davide Chicco, Giuseppe Jurman: \"Machine learning can predict survival of patients with heart failure "
                        + "from serum creatinine and ejection fraction alone\". BMC Medical Informatics and Decision Making 20, 16 (2020)");
        return data;
    }

    public static DataFileCSV ConcreteCompressiveStrength(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "水泥(公斤)" : "Cement(kg)", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "矿渣(公斤)" : "Blast Furnace Slag(kg)", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "煤灰(公斤)" : "Fly Ash(kg)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "水(公斤)" : "Water(kg)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "塑化剂(公斤)" : "Superplasticizer(kg)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "粗颗粒(公斤)" : "Coarse Aggregate(kg)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "细颗料(公斤)" : "Fine Aggregate(kg)", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "已使用天数" : "Age(days)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "1立方米混凝土抗压强度(兆帕)" : "Concrete compressive strength(MPa)", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "ConcreteCompressiveStrength"))
                .setComments("http://archive.ics.uci.edu/ml/datasets/Concrete+Compressive+Strength \n"
                        + "https://zhuanlan.zhihu.com/p/168747748");
        return data;
    }

    public static DataFileCSV DogRadiographsDataset(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "透亮度有改变" : "lucency changed", ColumnType.Boolean)
                .setDescription("changed(1) or not changed(0)"));
        columns.add(new Data2DColumn(isChinese ? "刀片尺寸" : "blade size", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "胫骨结节面积" : "tibial tuberosity area", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "胫骨结节宽度" : "tibial tuberosity width", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "年龄(年)" : "age in years", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "重量(公斤)" : "weight in kilograms", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "抗滚针的位置" : "location of the anti-rotational pin", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "双侧手术" : "bilateral surgery", ColumnType.Boolean)
                .setDescription("bilateral surgery(1) or unilateral surgery(0)"));
        data.setColumns(columns).setDataName(message(lang, "DogRadiographsDataset"))
                .setComments("https://www4.stat.ncsu.edu/~boos/var.select/lucency.html \n"
                        + "Radiographic and Clinical Changes of the Tibial Tuberosity after Tibial Plateau Leveling Osteomtomy.");
        return data;
    }

    public static DataFileCSV BaseballSalaries(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "薪水(千美元)" : "Salary (thousands of dollars)", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "击球平均得分数" : "Batting average", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "上垒率" : "On-base percentage", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "跑动数" : "Number of runs", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "击球数" : "Number of hits", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "二垒数" : "Number of doubles", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "三垒数" : "Number of triples", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "全垒数" : "Number of home runs", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "打点数" : "Number of runs batted in", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "走动数" : "Number of walks", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "三振出局数" : "Number of strike-outs", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "盗垒数" : "Number of stolen bases", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "失误数" : "Number of errors", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "自由球员资格" : "free agency eligibility", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "1991/2的自由球员" : "free agent in 1991/2", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "仲裁资格" : "arbitration eligibility", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "于1991/2仲裁" : "arbitration in 1991/2", ColumnType.Boolean));
        data.setColumns(columns).setDataName(message(lang, "BaseballSalaries"))
                .setComments("https://www4.stat.ncsu.edu/~boos/var.select/baseball.html \n"
                        + "Salary information for 337 Major League Baseball (MLB) players who are not pitchers "
                        + "and played at least one game during both the 1991 and 1992 seasons.");
        return data;
    }

    public static DataFileCSV SouthGermanCredit(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "状态" : "status", ColumnType.Short)
                .setDescription("status of the debtor's checking account with the bank\n"
                        + "1:no checking account   \n"
                        + "2: ... < 0 DM    \n"
                        + "3: 0<= ... < 200 DM   \n"
                        + "4: >= 200 DM \n"
                        + "salary for at least 1 year"));
        columns.add(new Data2DColumn(isChinese ? "持续时间(月)" : "duration(months)", ColumnType.Short)
                .setDescription("credit duration in months"));
        columns.add(new Data2DColumn(isChinese ? "信用卡历史" : "credit_history", ColumnType.Short)
                .setDescription("history of compliance with previous or concurrent credit contracts\n"
                        + " 0 : delay in paying off in the past   \n"
                        + " 1 : critical account/other credits elsewhere \n"
                        + " 2 : no credits taken/all credits paid back duly\n"
                        + " 3 : existing credits paid back duly till now   \n"
                        + " 4 : all credits at this bank paid back duly "));
        columns.add(new Data2DColumn(isChinese ? "用途" : "purpose", ColumnType.Short)
                .setDescription("purpose for which the credit is needed\n"
                        + " 0 : others             \n"
                        + " 1 : car (new)          \n"
                        + " 2 : car (used)         \n"
                        + " 3 : furniture/equipment\n"
                        + " 4 : radio/television   \n"
                        + " 5 : domestic appliances\n"
                        + " 6 : repairs            \n"
                        + " 7 : education          \n"
                        + " 8 : vacation           \n"
                        + " 9 : retraining         \n"
                        + " 10 : business  "));
        columns.add(new Data2DColumn(isChinese ? "金额" : "amount", ColumnType.Short)
                .setDescription("credit amount in DM (quantitative; result of monotonic transformation; "
                        + "actual data and type of transformation unknown)"));
        columns.add(new Data2DColumn(isChinese ? "储蓄" : "savings", ColumnType.Short)
                .setDescription("debtor's savings\n"
                        + " 1 : unknown/no savings account\n"
                        + " 2 : ... <  100 DM             \n"
                        + " 3 : 100 <= ... <  500 DM      \n"
                        + " 4 : 500 <= ... < 1000 DM      \n"
                        + " 5 : ... >= 1000 DM   "));
        columns.add(new Data2DColumn(isChinese ? "职业年限" : "employment_duration", ColumnType.Short)
                .setDescription("duration of debtor's employment with current employer\n"
                        + " 1 : unemployed      \n"
                        + " 2 : < 1 yr          \n"
                        + " 3 : 1 <= ... < 4 yrs\n"
                        + " 4 : 4 <= ... < 7 yrs\n"
                        + " 5 : >= 7 yrs "));
        columns.add(new Data2DColumn(isChinese ? "信贷率" : "installment_rate", ColumnType.Short)
                .setDescription("credit installments as a percentage of debtor's disposable income\n"
                        + "1 : >= 35         \n"
                        + " 2 : 25 <= ... < 35\n"
                        + " 3 : 20 <= ... < 25\n"
                        + " 4 : < 20  "));
        columns.add(new Data2DColumn(isChinese ? "个人状态" : "personal_status_sex", ColumnType.Short)
                .setDescription("combined information on sex and marital status\n"
                        + " 1 : male : divorced/separated           \n"
                        + " 2 : female : non-single or male : single\n"
                        + " 3 : male : married/widowed              \n"
                        + " 4 : female : single "));
        columns.add(new Data2DColumn(isChinese ? "其他债务人" : "other_debtors", ColumnType.Short)
                .setDescription("Is there another debtor or a guarantor for the credit\n"
                        + " 1 : none        \n"
                        + " 2 : co-applicant\n"
                        + " 3 : guarantor "));
        columns.add(new Data2DColumn(isChinese ? "当前居住年限(年)" : "present_residence", ColumnType.Short)
                .setDescription("length of time (in years) the debtor lives in the present residence\n"
                        + " 1 : < 1 yr          \n"
                        + " 2 : 1 <= ... < 4 yrs\n"
                        + " 3 : 4 <= ... < 7 yrs\n"
                        + " 4 : >= 7 yrs "));
        columns.add(new Data2DColumn(isChinese ? "财产" : "property", ColumnType.Short)
                .setDescription("the debtor's most valuable property\n"
                        + " 1 : unknown / no property                    \n"
                        + " 2 : car or other                             \n"
                        + " 3 : building soc. savings agr./life insurance\n"
                        + " 4 : real estate   "));
        columns.add(new Data2DColumn(isChinese ? "年龄" : "age", ColumnType.Short));
        columns.add(new Data2DColumn(isChinese ? "其它贷款计划" : "other_installment_plans", ColumnType.Short)
                .setDescription("installment plans from providers other than the credit-giving bank\n"
                        + " 1 : bank  \n"
                        + " 2 : stores\n"
                        + " 3 : none"));
        columns.add(new Data2DColumn(isChinese ? "居所类型" : "housing", ColumnType.Short)
                .setDescription("type of housing the debtor lives in\n"
                        + " 1 : for free\n"
                        + " 2 : rent    \n"
                        + " 3 : own "));
        columns.add(new Data2DColumn(isChinese ? "信用卡数目" : "number_credits", ColumnType.Short)
                .setDescription("number of credits including the current one the debtor has (or had) at this bank\n"
                        + " 1 : 1   \n"
                        + " 2 : 2-3 \n"
                        + " 3 : 4-5 \n"
                        + " 4 : >= 6"));
        columns.add(new Data2DColumn(isChinese ? "工作类型" : "job", ColumnType.Short)
                .setDescription(" 1 : unemployed/unskilled - non-resident       \n"
                        + " 2 : unskilled - resident                      \n"
                        + " 3 : skilled employee/official                 \n"
                        + " 4 : manager/self-empl./highly qualif. employee"));
        columns.add(new Data2DColumn(isChinese ? "被依赖人数" : "people_liable", ColumnType.Short)
                .setDescription("number of persons who financially depend on the debtor\n"
                        + " 1 : 3 or more\n"
                        + " 2 : 0 to 2"));
        columns.add(new Data2DColumn(isChinese ? "有电话" : "telephone", ColumnType.Short)
                .setDescription("Is there a telephone landline registered on the debtor's name? "
                        + "(binary; remember that the data are from the 1970s)\n"
                        + " 1 : no                       \n"
                        + " 2 : yes (under customer name)"));
        columns.add(new Data2DColumn(isChinese ? "是外籍雇员" : "foreign_worker", ColumnType.Short)
                .setDescription("Is the debtor a foreign worker\n"
                        + " 1 : yes\n"
                        + " 2 : no "));
        columns.add(new Data2DColumn(isChinese ? "信用风险" : "credit_risk", ColumnType.Short)
                .setDescription("Has the credit contract been complied with (good) or not (bad) ?\n"
                        + " 0 : bad \n"
                        + " 1 : good"));

        data.setColumns(columns).setDataName(message(lang, "SouthGermanCredit"))
                .setComments("http://archive.ics.uci.edu/ml/datasets/South+German+Credit\n"
                        + "700 good and 300 bad credits with 20 predictor variables. Data from 1973 to 1975. "
                        + "Stratified sample from actual credits with bad credits heavily oversampled. A cost matrix can be used.");
        return data;
    }

    public static DataFileCSV BostonHousingPrices(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "镇" : "town", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "经度" : "longitude", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "纬度" : "latitude", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "犯罪率" : "crime_ratio", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "超过25000平米的区" : "zoned_bigger_25000", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "工业用地" : "industrial_land", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "靠近查理斯河" : "near_Charies_River", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "一氧化氮浓度" : "nitrogen_density", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "平均房间数" : "average_room_number", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "早于1940建成比率" : "built_before_1940_ratio", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "到达市中心的距离" : "distance_to_centre", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "公路可达性" : "accessbility_to_hightway", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "税率" : "tax_rate", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "学生教师比" : "pupil_teacher_ratio", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "低收入比" : "lower_class_ratio", ColumnType.Double));
        columns.add(new Data2DColumn(isChinese ? "价格中位数" : "median_price", ColumnType.Double));
        data.setColumns(columns).setDataName(message(lang, "BostonHousingPrices"))
                .setComments("https://github.com/tomsharp/SVR/tree/master/data");
        return data;
    }

    public static DataFileCSV ChineseHistoricalCapitals(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Country"), ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Capital"), ColumnType.String, true).setWidth(200));
        columns.add(new Data2DColumn(message(lang, "Longitude"), ColumnType.Longitude));
        columns.add(new Data2DColumn(message(lang, "Latitude"), ColumnType.Latitude));
        columns.add(new Data2DColumn(message(lang, "StartTime"), ColumnType.Era).setFormat(isChinese ? "Gy" : "y G"));
        columns.add(new Data2DColumn(message(lang, "EndTime"), ColumnType.Era).setFormat(isChinese ? "Gy" : "y G"));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(message(lang, "ChineseHistoricalCapitals"));
        return data;
    }

    public static DataFileCSV AutumnMovementPatternsOfEuropeanGadwalls(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "StartTime"), ColumnType.Datetime, 180).setFixTwoDigitYear(true));
        columns.add(new Data2DColumn(message(lang, "EndTime"), ColumnType.Datetime, 180).setFixTwoDigitYear(true));
        columns.add(new Data2DColumn(message(lang, "Longitude"), ColumnType.Longitude));
        columns.add(new Data2DColumn(message(lang, "Latitude"), ColumnType.Latitude));
        columns.add(new Data2DColumn(message(lang, "CoordinateSystem"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(message(lang, "AutumnMovementPatternsOfEuropeanGadwalls"))
                .setComments("https://www.datarepository.movebank.org/handle/10255/move.346");
        return data;
    }

    public static DataFileCSV SpermWhalesGulfOfMexico(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Time"), ColumnType.Datetime, 180).setFixTwoDigitYear(true));
        columns.add(new Data2DColumn(message(lang, "Longitude"), ColumnType.Longitude));
        columns.add(new Data2DColumn(message(lang, "Latitude"), ColumnType.Latitude));
        columns.add(new Data2DColumn(message(lang, "CoordinateSystem"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(message(lang, "SpermWhalesGulfOfMexico"))
                .setComments("https://www.datarepository.movebank.org/handle/10255/move.1059");
        return data;
    }

    public static DataFileCSV EpidemicReportsCOVID19(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Date"), ColumnType.Date)
                .setFixTwoDigitYear(false).setFormat("yyyy-MM-dd"));
        columns.add(new Data2DColumn(message(lang, "Country"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Province"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Confirmed"), ColumnType.Integer));
        columns.add(new Data2DColumn(message(lang, "Healed"), ColumnType.Integer));
        columns.add(new Data2DColumn(message(lang, "Dead"), ColumnType.Integer));
        columns.add(new Data2DColumn(message(lang, "Longitude"), ColumnType.Longitude));
        columns.add(new Data2DColumn(message(lang, "Latitude"), ColumnType.Latitude));
        data.setColumns(columns).setDataName(message(lang, "EpidemicReportsCOVID19"))
                .setComments("https://github.com/CSSEGISandData/COVID-19");
        return data;
    }

    public static DataFileCSV ProjectRegister(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "ConfigurationID"), ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Name"), ColumnType.String, true).setWidth(200));
        columns.add(new Data2DColumn(message(lang, "LastStatus"), ColumnType.Enumeration)
                .setFormat(isChinese ? "申请\n已批准\n需求分析\n设计\n实现\n测试\n验证\n维护\n已完成\n被否定\n失败\n已取消"
                        : "Applying\nApproved\nRequirement\nDesign\nImplementing\nTesting\nValidated\nMaintenance\nCompleted\nDenied\nFailed\nCanceled"));
        columns.add(new Data2DColumn(isChinese ? "项目经理" : "Manager", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "批准者" : "Approver", ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "StartTime"), ColumnType.Datetime));
        columns.add(new Data2DColumn(isChinese ? "关闭时间" : "Closed time", ColumnType.Datetime));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(isChinese ? "项目登记" : "Project register");
        return data;
    }

    public static DataFileCSV ProjectStatus(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "项目编号" : "Project ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Status"), ColumnType.Enumeration)
                .setFormat(isChinese ? "申请\n已批准\n需求分析\n设计\n实现\n测试\n验证\n维护\n已完成\n被否定\n失败\n已取消"
                        : "Applying\nApproved\nRequirement\nDesign\nImplementing\nTesting\nValidated\nMaintenance\nCompleted\nDenied\nFailed\nCanceled"));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Recorder"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "RecordTime"), ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "项目状态" : "Project Status");
        return data;
    }

    public static DataFileCSV TaskRegister(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "ConfigurationID"), ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(isChinese ? "项目编号" : "Project ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Name"), ColumnType.String, true).setWidth(200));
        columns.add(new Data2DColumn(message(lang, "LastStatus"), ColumnType.Enumeration)
                .setFormat(isChinese ? "分派\n执行\n完成\n失败\n取消"
                        : "Assign\nPerform\nComplete\nFail\nCancel"));
        columns.add(new Data2DColumn(isChinese ? "执行者" : "Performer", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "开始时间" : "StartTime", ColumnType.Datetime));
        columns.add(new Data2DColumn(isChinese ? "关闭时间" : "ClosedTime", ColumnType.Datetime));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(isChinese ? "任务登记" : "Task register");
        return data;
    }

    public static DataFileCSV TaskStatus(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "任务编号" : "Task ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Status"), ColumnType.Enumeration)
                .setFormat(isChinese ? "计划\n执行\n完成\n失败\n取消"
                        : "Plan\nPerform\nComplete\nFail\nCancel"));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Recorder"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "RecordTime"), ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "任务状态" : "Task Status");
        return data;
    }

    public static DataFileCSV PersonRegister(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "ConfigurationID"), ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(isChinese ? "任务编号" : "Task ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Role"), ColumnType.Enumeration)
                .setFormat(isChinese ? "投资人\n监管者\n项目经理\n组长\n设计者\n编程者\n测试者\n其他/她"
                        : "Investor\nSupervisor\nProject manager\nTeam leader\nDesigner\nProgrammer\nTester\n\nOther"));
        columns.add(new Data2DColumn(message(lang, "Name"), ColumnType.String, true));
        columns.add(new Data2DColumn(message(lang, "PhoneNumber"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(isChinese ? "人员登记" : "Person register");
        return data;
    }

    public static DataFileCSV PersonStatus(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "人员编号" : "Person ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Status"), ColumnType.Enumeration)
                .setFormat(isChinese ? "加入\n修改信息\n退出"
                        : "Join\nUpdate information\nQuit"));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Recorder"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "RecordTime"), ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "人员状态" : "Person Status");
        return data;
    }

    public static DataFileCSV ResourceRegister(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "ConfigurationID"), ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(isChinese ? "任务编号" : "Task ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Type"), ColumnType.Enumeration)
                .setFormat(isChinese ? "设备\n程序\n源代码\n文档\n数据\n其它"
                        : "Device\nProgram\nSource codes\nDocument\nData\nOther"));
        columns.add(new Data2DColumn(message(lang, "Name"), ColumnType.String, true));
        columns.add(new Data2DColumn(message(lang, "LastStatus"), ColumnType.Enumeration)
                .setFormat(isChinese ? "正常\n出借\n出售\n废弃\n损毁\n丢失"
                        : "Normal\nLent\nSaled\nDiscarded\nDamaged\nLost"));
        columns.add(new Data2DColumn(isChinese ? "保管者" : "Keeper", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "登记时间" : "Register time", ColumnType.Datetime));
        columns.add(new Data2DColumn(isChinese ? "失效时间" : "Invalid time", ColumnType.Datetime));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(isChinese ? "资源登记" : "Resource register");
        return data;
    }

    public static DataFileCSV ResourceStatus(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "资源编号" : "Resource ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Status"), ColumnType.Enumeration)
                .setFormat(isChinese ? "正常\n出借\n出售\n废弃\n损毁\n丢失"
                        : "Normal\nLent\nSaled\nDiscarded\nDamaged\nLost"));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Recorder"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "RecordTime"), ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "资源状态" : "Resource Status");
        return data;
    }

    public static DataFileCSV RiskAnalysis(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "ConfigurationID"), ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(isChinese ? "任务编号" : "Task ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(message(lang, "Type"), ColumnType.Enumeration)
                .setFormat(isChinese ? "范围\n质量\n时间\n资金\n技术\n人力\n法律\n其它"
                        : "Scope\nQuality\nTime\nMoney\nTechnique\nHuman\nLaw\nOther"));
        columns.add(new Data2DColumn(isChinese ? "风险项" : "Risk Item", ColumnType.String, true));
        columns.add(new Data2DColumn(isChinese ? "可能性" : "Probability", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "严重性" : "Severity", ColumnType.Integer));
        columns.add(new Data2DColumn(isChinese ? "优先级" : "Priority", ColumnType.Integer));
        columns.add(new Data2DColumn(message(lang, "Description"), ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "影响" : "Effects", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "应急措施" : "Contingency Actions", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "分析者" : "Analyzer", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "分析时间" : "Analysis time", ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "风险分析" : "Risk Analysis");
        return data;
    }

    public static DataFileCSV CostRecord(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "任务编号" : "Task ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(isChinese ? "计划开始日期" : "Planned start time", ColumnType.Date));
        columns.add(new Data2DColumn(isChinese ? "计划结束日期" : "Planned end time", ColumnType.Date));
        columns.add(new Data2DColumn(isChinese ? "计划工作量（人月）" : "Planned workload(person-month)", ColumnType.Float));
        columns.add(new Data2DColumn(isChinese ? "计划成本（元）" : "Planned cost(Yuan)", ColumnType.Float));
        columns.add(new Data2DColumn(isChinese ? "计划产出" : "Planned results", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "实际开始日期" : "Actual start time", ColumnType.Date));
        columns.add(new Data2DColumn(isChinese ? "实际结束日期" : "Actual end time", ColumnType.Date));
        columns.add(new Data2DColumn(isChinese ? "实际工作量（人月）" : "Actual workload(person-month)", ColumnType.Float));
        columns.add(new Data2DColumn(isChinese ? "实际成本（元）" : "Actual cost(Yuan)", ColumnType.Float));
        columns.add(new Data2DColumn(isChinese ? "实际产出" : "Actual results", ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Recorder"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "RecordTime"), ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "成本记录" : "Cost Records");
        return data;
    }

    public static DataFileCSV VerificationRecord(String lang) {
        boolean isChinese = Languages.isChinese(lang);
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(isChinese ? "任务编号" : "Task ID", ColumnType.String, true).setWidth(140));
        columns.add(new Data2DColumn(isChinese ? "事项" : "Item", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "通过" : "Pass", ColumnType.Boolean));
        columns.add(new Data2DColumn(isChinese ? "严重性" : "Severity", ColumnType.Integer));
        columns.add(new Data2DColumn(message(lang, "Description"), ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "影响" : "Effects", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "建议" : "Suggestions", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "检验者" : "Verifier", ColumnType.String));
        columns.add(new Data2DColumn(isChinese ? "检验时间" : "Verification time", ColumnType.Datetime));
        data.setColumns(columns).setDataName(isChinese ? "检验记录" : "Verify Record");
        return data;
    }

    public static DataFileCSV TestEnvironment(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "Title"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Hardwares"), ColumnType.String));
        columns.add(new Data2DColumn("CPU", ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Memory"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "OS"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Softwares"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Language"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Target"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Comments"), ColumnType.String));
        data.setColumns(columns).setDataName(message(lang, "TestEnvironment"));
        return data;
    }

    public static DataFileCSV CompatibilityTesting(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "TestEnvironment"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Item"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "ModifyTime"), ColumnType.Datetime));
        columns.add(new Data2DColumn(message(lang, "Description"), ColumnType.String));
        data.setColumns(columns).setDataName(message(lang, "CompatibilityTesting"));
        return data;
    }

    public static DataFileCSV MyBoxBaseVerificationList(BaseController controller,
            String lang, boolean onlyDef) {
        try {
            boolean isChinese = Languages.isChinese(lang);

            DataFileCSV targetData = new DataFileCSV();
            List<Data2DColumn> columns = new ArrayList<>();
            columns.add(new Data2DColumn(message(lang, "Index"), ColumnType.Integer));
            columns.add(new Data2DColumn(message(lang, "HierarchyNumber"), ColumnType.String));
            for (int i = 1; i <= FunctionsList.MaxLevel; i++) {
                columns.add(new Data2DColumn(message(lang, "Level") + " " + i, ColumnType.String));
            }
            for (Data2DColumn c : columns) {
                c.setEditable(false);
            }
            int preSize = columns.size();
            columns.add(0, new Data2DColumn(message(lang, "TestEnvironment"), ColumnType.String));
            String defauleValue = "";
            String format = defauleValue + "\n" + message(lang, "Success") + "\n" + message(lang, "Fail");
            columns.add(new Data2DColumn(isChinese ? "打开界面" : "Open interface", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "正常值" : "Normal values", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "正常操作" : "Normal operations", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "正常结果" : "Normal results", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "边界值" : "Boundary values", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "异常值" : "Abnormal values", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "异常操作" : "Abnormal operations", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "异常结果" : "Exception results", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "提示" : "Tips", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "按钮" : "Buttons", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "快捷键" : "Shortcuts", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "菜单" : "Menu", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "响应时间" : "Response time", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "内存占用" : "Memory occupied", ColumnType.EnumerationEditable)
                    .setFormat(format));
            columns.add(new Data2DColumn(isChinese ? "检验者" : "Verifier", ColumnType.String));
            columns.add(new Data2DColumn(message(lang, "ModifyTime"), ColumnType.Datetime));
            columns.add(new Data2DColumn(message(lang, "Description"), ColumnType.String));

            String dataName = message(lang, "MyBoxBaseVerificationList") + " - " + DateTools.nowString();
            targetData.setColumns(columns).setDataName(dataName);

            File targetFile = FileTmpTools.generateFile(dataName, "csv");
            if (targetFile.exists()) {
                targetFile.delete();
            }
            Charset charset = Charset.forName("utf-8");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, charset, false))) {
                String header = null;
                for (Data2DColumn column : columns) {
                    String name = "\"" + column.getColumnName() + "\"";
                    if (header == null) {
                        header = name;
                    } else {
                        header += "," + name;
                    }
                }
                writer.write(header + System.lineSeparator());
                if (!onlyDef) {
                    FunctionsList list = new FunctionsList(controller.getMainMenu(), false, lang);
                    StringTable table = list.make();
                    if (table == null) {
                        return null;
                    }
                    String values = "";
                    for (int i = 0; i < columns.size() - preSize; i++) {
                        values += "," + defauleValue;
                    }
                    String line;
                    for (List<String> row : table.getData()) {
                        line = "win";
                        for (String v : row) {
                            line += "," + v;
                        }
                        writer.write(line + values + System.lineSeparator());
                    }
                }
                writer.flush();
            } catch (Exception e) {
                MyBoxLog.error(e);
                return null;
            }
            targetData.setFile(targetFile).setHasHeader(true).setCharset(charset).setDelimiter(",");
            targetData.saveAttributes();
            return targetData;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static DataFileCSV DetailedTesting(String lang) {
        DataFileCSV data = new DataFileCSV();
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message(lang, "TestEnvironment"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Version"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Type"), ColumnType.EnumerationEditable)
                .setFormat("\n" + message(lang, "Function") + "\n"
                        + message(lang, "UserInterface") + "\n" + message(lang, "Bundary") + "\n"
                        + message(lang, "Invalid") + "\n"
                        + message(lang, "Data") + "\n" + message(lang, "API") + "\n"
                        + message(lang, "IO") + "\n" + message(lang, "Exception") + "\n"
                        + message(lang, "Performance") + "\n" + message(lang, "Robustness") + "\n"
                        + message(lang, "Usability") + "\n" + message(lang, "Compatibility") + "\n"
                        + message(lang, "Security") + "\n" + message(lang, "Document")));
        columns.add(new Data2DColumn(message(lang, "Object"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Title"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Steps"), ColumnType.String));
        columns.add(new Data2DColumn(message(lang, "Status"), ColumnType.EnumerationEditable)
                .setFormat("\n" + message(lang, "NotTested") + "\n"
                        + message(lang, "Testing") + "\n"
                        + message(lang, "Success") + "\n"
                        + message(lang, "Fail")));
        columns.add(new Data2DColumn(message(lang, "ModifyTime"), ColumnType.Datetime));
        columns.add(new Data2DColumn(message(lang, "Description"), ColumnType.String));
        data.setColumns(columns).setDataName(message(lang, "DetailedTesting"));
        return data;
    }

}

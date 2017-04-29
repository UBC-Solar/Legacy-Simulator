package com.ubcsolar.Main.test;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.testAssistanceFiles.RandomObjectGenerator;
import org.apache.commons.lang3.SystemUtils;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jacob on 4/15/2017.
 */
public class RunSimTestCircuitOfAmerica {
    private static GlobalController theProgram;
    private static String windowsPrefix = "res\\";
    private static String linuxPrefix = "res/";
    private static String fileName = "Circuit_of_Americas_labled.kml";
    private static String file = windowsPrefix+fileName;


    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, JDOMException, NoForecastReportException, NoLoadedRouteException, NoLocationReportedException, NoCarStatusException {
        if (SystemUtils.IS_OS_LINUX) {file = linuxPrefix+fileName;}
        SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");

        theProgram = new GlobalController(true);

        theProgram.getMapController().load(new File(file));
        theProgram.getMyWeatherController().downloadNewForecastsForRoute(100);


        theProgram.getMyCarController().adviseOfNewCarReport(RandomObjectGenerator.generateNewTelemDataPack());
        LocationReport carLocationReported = new LocationReport(theProgram.getMapController().getAllPoints().getTrailMarkers().get(0), "raven", "generated");
        theProgram.getMapController().recordNewCarLocation(carLocationReported);
    }
}

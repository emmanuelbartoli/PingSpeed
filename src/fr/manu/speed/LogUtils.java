/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016-2017 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.manu.speed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.model.SpeedTestMode;

/**
 * Log utilities method for examples.
 *
 * @author Bertrand Martel
 */
public class LogUtils {

    /**
     * default scale for BigDecimal.
     */
    private static final int DEFAULT_SCALE = 4;

    /**
     * default rounding mode for BigDecimal.
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /**
     * conversion const for per second value.
     */
    private static final BigDecimal VALUE_PER_SECONDS = new BigDecimal(1000);

    /**
     * conversion const for M per second value.
     */
    private static final BigDecimal MEGA_VALUE_PER_SECONDS = new BigDecimal(1000000);

    /**
     * log report separator.
     */
    public static final String LOG_REPORT_SEPARATOR = "--------------------------------------------------------";

    /**
     * print speed examples report object.
     *
     * @param report speed examples report to log
     * @param logger log4j logger
     */
    public static void logSpeedTestReport(final SpeedTestReport report, final Logger logger) {


            switch (report.getSpeedTestMode()) {
                case DOWNLOAD:
                    System.out.println("--------------current download report--------------------");
                    break;
                case UPLOAD:
                    System.out.println("---------------current upload report--------------------");
                    break;
                default:
                    break;
            }
            logReport(report, logger);
    }

    /**
     * print upload/download result.
     *
     * @param mode                        speed examples mode
     * @param packetSize                  packet size received
     * @param transferRateBitPerSeconds   transfer rate in bps
     * @param transferRateOctetPerSeconds transfer rate in Bps
     * @param logger                      log4j logger
     */
    public static void logFinishedTask(final SpeedTestMode mode, final long packetSize, final BigDecimal
            transferRateBitPerSeconds, final BigDecimal transferRateOctetPerSeconds, final Logger logger) {

   
            switch (mode) {
                case DOWNLOAD:
                    System.out.println("======== Download [ OK ] =============");
                    break;
                case UPLOAD:
                    System.out.println("========= Upload [ OK ]  =============");
                    break;
                default:
                    break;
            }

            System.out.println("packetSize     : " + packetSize + " octet(s)");
            System.out.println("transfer rate  : " + transferRateBitPerSeconds + " bit/second   | " +
                    transferRateBitPerSeconds.divide(VALUE_PER_SECONDS, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE)
                    + " Kbit/second  | " + transferRateBitPerSeconds.divide(MEGA_VALUE_PER_SECONDS) + " Mbit/second");
            /*
            System.out.println("transfer rate  : " + transferRateOctetPerSeconds + " octet/second  | " +
                    transferRateOctetPerSeconds.divide(VALUE_PER_SECONDS, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE)
                    + " Koctet/second | " + transferRateOctetPerSeconds.divide(MEGA_VALUE_PER_SECONDS, DEFAULT_SCALE,
                    DEFAULT_ROUNDING_MODE) + " " +
                    "Moctet/second");
            */
            System.out.println("##################################################################");
        
    }

    /**
     * Print log report from speed test report object.
     *
     * @param report speed test report instance
     * @param logger log4j logger
     */
    public static void logReport(final SpeedTestReport report, final Logger logger) {

            System.out.println("progress             : " + report.getProgressPercent() + "%");
            System.out.println("transfer rate bit    : " + report.getTransferRateBit() + "b/s");
           // System.out.println("transfer rate octet  : " + report.getTransferRateOctet() + "B/s");

            /*
            switch (report.getSpeedTestMode()) {
                case DOWNLOAD:
                    System.out.println("downloaded for now   : " + report.getTemporaryPacketSize()
                            + "/" + report.getTotalPacketSize());
                    break;
                case UPLOAD:
                    System.out.println("uploaded for now     : " + report.getTemporaryPacketSize()
                            + "/" + report.getTotalPacketSize());
                    break;
                default:
                    break;
            }
            */
            /*
            if (report.getStartTime() > 0) {
                System.out.println("amount of time       : " +
                        ((report.getReportTime() - report.getStartTime()) / 1000) + "s");
            }
            */

    }
}

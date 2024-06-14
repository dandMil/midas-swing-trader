package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.controller.MidasController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PythonCaller {
    private static final Logger logger = LoggerFactory.getLogger(PythonCaller.class);


//    @Scheduled(fixedRate = 300000)
//    @Scheduled(cron = "0 0 * * * *") // Run every hour at the start of the hour

//    public void findShorts() throws IOException {
//        String fetching = "python3 " + "/Users/dandmil/Desktop/Projects/midas/short_squeeze_finder.py";
//        String[] commandToExecute = new String[]{"cmd.exe", "/c", fetching};
//    }

//    public void yahooSignificantVolume(String[]tickers) throws IOException {
//        String fetching = "python3 "+"/Users/dandmil/Desktop/Projects/midas/short_squeeze_finder.py";
//        String[] commandToExecute = new String[]{"cmd.exe", "/c", fetching};
//
//        Process process = Runtime.getRuntime().exec(fetching);
//
////        ProcessBuilder processBuilder = new ProcessBuilder(fetching);
////        processBuilder.redirectErrorStream(true); // Redirect error stream to standard output
////        Process process = processBuilder.start();
////
////        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////        String line;
////        while ((line = reader.readLine()) != null) {
////            System.out.println(line); // or logger.info(line) if using a logger
////        }
//    }
//    }
    public void yahooSignificantVolume(String[]tickers) throws IOException {
        String scriptPath = "/Users/dandmil/Desktop/Projects/midas/yahoo_significant_volume_change.py";
        String[] commandToExecute = new String[]{
                "python3", scriptPath
        };

        // Path to your virtual environment's Python executable
        String pythonExecutable = "/Users/dandmil/Desktop/Projects/midas-master/path/to/venv/bin/python";

        // Prepare the command to execute the Python script with arguments
        List<String> command = new ArrayList<>();
        command.add(pythonExecutable);
        command.add(scriptPath);

        // Add tickers as arguments
        Collections.addAll(command, tickers);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirect error stream to standard output
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // or logger.info(line) if using a logger
        }
    }
    }



package com.dandmil.midasswingtrader.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PythonCaller {


//    @Scheduled(fixedRate = 300000)
    @Scheduled(cron = "0 0 * * * *") // Run every hour at the start of the hour

    public void findShorts() throws IOException {
        String fetching = "python3 "+"/Users/dandmil/Desktop/Projects/midas/short_squeeze_finder.py";
        String[] commandToExecute = new String[]{"cmd.exe", "/c", fetching};

        Process process = Runtime.getRuntime().exec(fetching);
    }
}

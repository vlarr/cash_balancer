package ru.vlarp.cp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.vlarp.cp.logic.DeltaCalculator;
import ru.vlarp.cp.logic.PartyInfoHelper;
import ru.vlarp.cp.pojo.TransferInfo;
import ru.vlarp.cp.service.PartyInfoParser;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {
    private final DecimalFormat formatter = new DecimalFormat("#0.00");

    private DeltaCalculator deltaCalculator;

    @Autowired
    public void setDeltaCalculator(DeltaCalculator deltaCalculator) {
        this.deltaCalculator = deltaCalculator;
    }

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Banner.Mode.OFF);
        final ApplicationContext applicationContext = app.run(args);
        SpringApplication.exit(applicationContext);
    }

    @Override
    public void run(String... args) {
        log.info("EXECUTING : command line runner");
        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }

        if (args.length != 1) {
            log.error("file name not found");
            return;
        }

        PartyInfoHelper info;

        try {
            info = new PartyInfoParser().parseFile(args[0]);
        } catch (IOException ex) {
            log.error(ex.toString());
            return;
        }

        List<TransferInfo> result = deltaCalculator.buildTransfers(info);

        for (TransferInfo transferInfo : result) {
            log.info("from: \"{}\", to: \"{}\", value: {}",
                    info.getMemberName().get(transferInfo.getFrom()),
                    info.getMemberName().get(transferInfo.getTo()),
                    formatter.format(transferInfo.getVal())
            );
        }


    }
}

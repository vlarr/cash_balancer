package ru.vlarp.cp.service;

import org.apache.commons.lang3.math.NumberUtils;
import ru.vlarp.cp.logic.PartyInfoHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PartyInfoParser {
    public PartyInfoHelper parseFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        List<String> lines = Files.readAllLines(path);
        PartyInfoHelper partyInfoHelper = new PartyInfoHelper();

        for (String line : lines) {
            String[] words = line.split("\\s*,\\s*");

            if (words.length >= 2) {
                if (words.length == 3 && NumberUtils.isParsable(words[words.length - 1])) {
                    partyInfoHelper.addPurchase(words[0], words[1], Double.parseDouble(words[2]));
                } else {
                    partyInfoHelper.addCheck(words[0], Arrays.copyOfRange(words, 1, words.length));
                }
            }
        }

        return partyInfoHelper;
    }
}

package de.kaleidox.workbench.flk.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaleidox.workbench.flk.FlkResultsFile;
import de.kaleidox.workbench.flk.model.Test;
import de.kaleidox.workbench.model.abstr.StorageService;
import de.kaleidox.workbench.storage.StorageContentConverter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.comroid.api.func.BetterIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Log
@Service
public class FlkConverter implements StorageContentConverter {
    @Autowired StorageService storage;
    @Autowired ObjectMapper   objectMapper;

    @Override
    @SneakyThrows
    public String convert(String id) {
        List<String> lines;
        try (var data = storage.load(id); var isr = new InputStreamReader(data); var string = new StringWriter()) {
            isr.transferTo(string);
            lines = string.toString().lines().toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not load result data from storage", e);
        }

        var file = FlkResultsFile.builder();
        var test = new Test.Builder[1];
        for (var iter = new BetterIterator<>(lines.listIterator()); iter.hasNext(); ) {
            var $ = new Object() {
                void push() {
                    file.test(test[0].build());
                    test[0] = Test.builder();
                }
            };
            var line = iter.next();

            if (line.startsWith("TEST") && test[0] == null) test[0] = Test.builder();

            if (line.isBlank()) {
                if (test[0] != null) $.push();
                continue;
            }

            Arrays.stream(LineParserEntry.values())
                    .filter(lpe -> lpe.test(line))
                    .findAny()
                    .ifPresent(lpe -> lpe.parse(file, test[0], iter));

            if (iter.getCurrent().isEmpty()) $.push();
        }

        var resultData = objectMapper.writeValueAsString(file.build());
        return storage.store(new ByteArrayInputStream(resultData.getBytes(StandardCharsets.US_ASCII)), ".json");
    }
}

package de.kaleidox.workbench.storage;

import de.kaleidox.workbench.model.abstr.StorageService;
import lombok.SneakyThrows;
import org.comroid.api.net.Token;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.function.Predicate;

@Service
public class TempFileStorageService implements StorageService {
    final File baseDir = new File(System.getProperty("java.io.tmpdir"), getClass().getSimpleName() + '/');

    @EventListener
    public void on(ApplicationStartedEvent ignored) {
        if (!baseDir.exists() && !baseDir.mkdirs())
            throw new RuntimeException("Unable to initialize TempFileStorageService");
        baseDir.deleteOnExit();
    }

    @Override
    @SneakyThrows
    public String store(InputStream data, String nameSuffix) {
        var id = Token.generate(8, false, Predicate.not(this::exists)) + nameSuffix;

        try (var in = data; var out = new FileOutputStream(new File(baseDir, id))) {
            in.transferTo(out);
            return id;
        }
    }

    @Override
    public boolean exists(String id) {
        return new File(baseDir, id).exists();
    }

    @Override
    @SneakyThrows
    public InputStream load(String id) {
        return new FileInputStream(new File(baseDir, id));
    }
}

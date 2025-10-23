package de.kaleidox.workbench.flk;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaleidox.workbench.flk.converter.FlkConverter;
import de.kaleidox.workbench.flk.model.entity.FlkScan;
import de.kaleidox.workbench.flk.model.repo.FlkScanRepository;
import de.kaleidox.workbench.model.abstr.StorageService;
import lombok.SneakyThrows;
import org.comroid.api.func.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/flk")
public class FlkConverterController {
    @Autowired StorageService    storage;
    @Autowired FlkConverter      converter;
    @Autowired ObjectMapper      objectMapper;
    @Autowired FlkScanRepository scans;

    @ModelAttribute("available")
    public List<FlkScan> available() {
        return Streams.of(scans.findAll()).toList();
    }

    @GetMapping
    public String upload() {
        return "flkConverter/upload";
    }

    @PostMapping
    @SneakyThrows
    public String upload(@RequestParam("file") MultipartFile file) {
        var filename = Objects.requireNonNull(file.getOriginalFilename(), "missing filename");
        var ext      = filename.substring(filename.lastIndexOf('.'));
        var id       = storage.store(file.getInputStream(), ext);
        return "redirect:/flk/" + id;
    }

    @SneakyThrows
    @GetMapping("/{id}")
    public String view(Model model, @PathVariable String id) {
        var ext = id.substring(id.lastIndexOf('.') + 1);

        FlkResultsFile result;
        switch (ext.toLowerCase()) {
            case "flk":
                id = converter.convert(id);
                return "redirect:/flk/" + id;
            case "json":
                var resultData = storage.load(id);
                result = objectMapper.readValue(resultData, FlkResultsFile.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + ext);
        }

        model.addAttribute("result", result);

        return "flkConverter/view";
    }
}

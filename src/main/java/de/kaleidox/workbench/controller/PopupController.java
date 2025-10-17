package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.model.EntityInfo;
import de.kaleidox.workbench.util.Exceptions;
import jakarta.persistence.EntityManager;
import org.comroid.api.data.bind.DataStructure;
import org.comroid.api.func.util.Pair;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.comroid.api.Polyfill.*;

@Controller
@RequestMapping("/popup")
public class PopupController {
    public static final Map<Class<?>, EntityInfo.Provider<?>> INFO_SERIALIZATION_PROVIDERS = new ConcurrentHashMap<>();
    @Autowired private  EntityManager                         entityManager;

    // /popup/edit/assignment/{entryInfo}/startTime/
    @GetMapping("/edit/{dtype}/{info}/{property}/show")
    public String edit(
            Model model,
            @PathVariable("dtype") String dtype,
            @PathVariable("info") String infoStr,
            @PathVariable("property") String property,
            @RequestParam("redirect_url") String redirectUrl
    ) {
        var meta    = getEditMeta(dtype, infoStr, property);
        var current = meta.getFirst().getFrom(meta.getSecond());

        model.addAttribute("meta", meta);
        model.addAttribute("current", String.valueOf(current));
        model.addAttribute("redirect_url", redirectUrl);

        return "popup/edit_value";
    }

    @PostMapping("/edit/{dtype}/{info}/{property}/apply")
    public void edit(
            @PathVariable("dtype") String dtype,
            @PathVariable("info") String infoStr,
            @PathVariable("property") String property,
            @RequestBody String valueStr
    ) {
        var meta = getEditMeta(dtype, infoStr, property);
        meta.getFirst().parseAndSet(meta.getSecond(), valueStr);
    }

    private Pair<DataStructure<?>.Property<Object>, Object> getEditMeta(String dtype, String infoStr, String property) {
        var type   = getEntityClassByDiscriminator(dtype);
        var struct = DataStructure.of(type);
        var provider = INFO_SERIALIZATION_PROVIDERS.entrySet()
                .stream()
                .filter(e -> e.getKey().isAssignableFrom(type))
                .findAny()
                .map(Map.Entry::getValue)
                .orElseThrow();
        var info = provider.backward(infoStr);
        var it   = provider.findById(uncheckedCast(info)).orElseThrow();
        var prop = struct.getProperty(property).orElseThrow();
        return new Pair<>(prop, it);
    }

    @SuppressWarnings("resource")
    private Class<?> getEntityClassByDiscriminator(String dtype) {
        // todo: remove deprecated api usage

        var sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        var metamodel      = (MetamodelImplementor) sessionFactory.getMetamodel();

        for (var entityName : metamodel.getAllEntityNames()) {
            var persister = metamodel.entityPersister(entityName);
            if (persister instanceof SingleTableEntityPersister singleTablePersister) {
                String discriminatorValue = (String) singleTablePersister.getDiscriminatorValue();
                if (dtype.equals(discriminatorValue)) {
                    return singleTablePersister.getMappedClass();
                }
            }
        }

        throw Exceptions.noSuchDtype();
    }
}

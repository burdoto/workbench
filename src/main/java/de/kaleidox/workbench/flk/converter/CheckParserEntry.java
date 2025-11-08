package de.kaleidox.workbench.flk.converter;

import de.kaleidox.workbench.flk.model.data.Check;
import org.comroid.api.attr.Named;
import org.comroid.api.func.BetterIterator;
import org.comroid.units.UValue;
import org.comroid.units.Unit;

import java.util.Arrays;
import java.util.function.Predicate;

public enum CheckParserEntry implements Named, Predicate<String> {
    VISUAL_CHECK, LEAD_CONTINUITY, BOND_RANGE, INS, SUBST;

    public static Check read(BetterIterator<String> iter) {
        var check = new Check.Builder[]{ Check.builder() };
        var value = iter.getCurrent();
        var name  = value.substring(0, 16);

        check[0].passed(value.charAt(17) == 'P');

        Arrays.stream(CheckParserEntry.values()).filter(cpe -> cpe.test(name)).findAny().ifPresent(cpe -> {
            check[0].name(cpe.getName());
            cpe.accept(iter, check[0]);
        });

        return check[0].build();
    }

    @Override
    public String getName() {
        return Named.super.getName().replaceAll("_", " ");
    }

    private void accept(BetterIterator<String> iter, Check.Builder check) {
        new Object() {
            String buf = iter.getCurrent();

            {
                while (!buf.startsWith("LIMIT")) {
                    if (!buf.matches(".+\\sP"))
                        check.value(parse(getName()));
                    buf = iter.next();
                }

                if (buf.startsWith("LIMIT")) check.limit(parse("LIMIT"));
                else iter.previous();
            }

            UValue parse(String name) {
                var trim = buf.substring(name.length()).trim();
                if (trim.matches("\\d\\s+[->0-9].+")) trim = trim.substring(1).trim();
                if (trim.matches(".+\\sP")) trim = trim.substring(0, trim.length() - 2).trim();
                var    split = trim.split(" ");
                double num;
                Unit   unit;
                if (split.length == 1) {
                    return UValue.parse(split[0]);
                } else {
                    num  = split[0].startsWith(">") ? 299 : Double.parseDouble(split[0]);
                    unit = new Unit(split[1]);
                    return UValue.builder().value(num).unit(unit).build();
                }
            }
        };
    }

    @Override
    public boolean test(String s) {
        return s.startsWith(getName());
    }
}

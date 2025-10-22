package de.kaleidox.workbench.flk.converter;

import de.kaleidox.workbench.flk.model.Check;
import org.comroid.api.attr.Named;
import org.comroid.api.func.BetterIterator;
import org.comroid.units.UValue;
import org.comroid.units.Unit;

public enum CheckParserEntry implements Named {
    VISUAL_CHECK, LEAD_CONTINUITY, BOND_RANGE, INS, SUBST;

    public static Check read(BetterIterator<String> iter) {
        var check = Check.builder();
        var value = iter.getCurrent();
        var name  = value.substring(0, 16);

        check.name(name);
        check.passed(value.charAt(17) == 'P');

        while (!iter.getCurrent().startsWith("LIMIT")) {
            var next = iter.next();
            if (next.startsWith(name)) {
                var trim = next.substring(name.length()).trim();
                if (trim.matches("\\d.+")) trim = trim.substring(1).trim();
                var eon  = trim.lastIndexOf(' ');
                var num  = Double.parseDouble(trim.substring(0, eon));
                var unit = Unit.builder().name(trim.substring(eon)).build();
                check.value(UValue.builder().value(num).unit(unit).build());
            } else iter.previous();
        }

        return check.build();
    }

    @Override
    public String getName() {
        return Named.super.getName().replaceAll("_", " ");
    }
}

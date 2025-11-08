package de.kaleidox.workbench.flk.converter;

import de.kaleidox.workbench.flk.FlkResultsFile;
import de.kaleidox.workbench.flk.model.data.Test;
import de.kaleidox.workbench.flk.model.data.TestMode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.comroid.api.attr.Named;
import org.comroid.api.func.BetterIterator;

import java.time.LocalDate;
import java.util.function.Predicate;

import static java.lang.Integer.*;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public enum LineParserEntry implements Named, Predicate<String> {
    MODEL {
        @Override
        public void parse(FlkResultsFile.Builder file, Test.Builder $, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent());
            file.deviceModel(value);
        }
    }, SN {
        @Override
        public void parse(FlkResultsFile.Builder file, Test.Builder $, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent());
            file.deviceSn(value);
        }
    }, TEST_NUMBER {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent());
            test.number(parseInt(value));
        }
    }, DATE {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent())
                    // for some reason, java wont parse dd-LLL-yy
                    .replace("JAN", "01")
                    .replace("FEB", "02")
                    .replace("MAR", "03")
                    .replace("APR", "04")
                    .replace("MAY", "05")
                    .replace("JUN", "06")
                    .replace("JUL", "07")
                    .replace("AUG", "08")
                    .replace("SEP", "09")
                    .replace("OCT", "10")
                    .replace("NOV", "11")
                    .replace("DEC", "12");
            test.date(LocalDate.parse(value, Test.DATE));
        }
    }, APP_NO {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent());
            test.applianceNumber(parseInt(value));
        }
    }, TEST_MODE {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent());
            var split = value.split(" ");
            var mode  = new TestMode(parseInt(split[0].trim()), split[1].trim());
            test.testMode(mode);
        }
    }, SITE {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            test.site(readString(iter, 3));
        }
    }, USER {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            var value = readTrim(iter.getCurrent());
            test.user(value);
        }
    }, VISUAL_CHECK {
        @Override
        public void parse(FlkResultsFile.Builder file, Test.Builder test, BetterIterator<String> iter) {
            test.check(CheckParserEntry.read(iter));
        }
    }, LEAD_CONTINUITY {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            test.check(CheckParserEntry.read(iter));
        }
    }, BOND_RANGE {
        @Override
        public void parse(FlkResultsFile.Builder file, Test.Builder test, BetterIterator<String> iter) {
            test.check(CheckParserEntry.read(iter));
        }
    }, INS {
        @Override
        public void parse(FlkResultsFile.Builder file, Test.Builder test, BetterIterator<String> iter) {
            test.check(CheckParserEntry.read(iter));
        }
    }, SUBST {
        @Override
        public void parse(FlkResultsFile.Builder file, Test.Builder test, BetterIterator<String> iter) {
            test.check(CheckParserEntry.read(iter));
        }
    }, DES {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            test.description(readString(iter, 3));
        }
    }, LOC {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            test.location(readString(iter, 2));
        }
    }, TEXT {
        @Override
        public void parse(FlkResultsFile.Builder $, Test.Builder test, BetterIterator<String> iter) {
            test.text(readString(iter, 2));
        }
    };

    @Override
    public String getName() {
        return Named.super.getName().replaceAll("_", " ");
    }

    protected String readTrim(String str) {
        return str.substring(getName().length()).trim();
    }

    protected String readString(BetterIterator<String> iter, int lines) {
        int i   = 0;
        var val = new StringBuilder();
        do {
            var current = iter.getCurrent();
            if ("".equals(current))
                break;
            current = current.substring(getName().length() + 2);
            val.append(current);
            iter.next();
        } while (++i < lines);
        iter.previous();
        return val.toString().trim();
    }

    @Override
    public boolean test(String s) {
        return s.startsWith(getName());
    }

    public abstract void parse(FlkResultsFile.Builder file, Test.Builder test, BetterIterator<String> iter);
}

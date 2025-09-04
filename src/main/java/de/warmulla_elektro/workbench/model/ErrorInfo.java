package de.warmulla_elektro.workbench.model;

import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.NestedServletException;

import java.io.PrintWriter;
import java.io.StringWriter;

public record ErrorInfo(String code, String message, String stacktrace) {
    public static ErrorInfo create(Throwable ex, int code, @Nullable String message, @Nullable String requestUri) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        //noinspection deprecation
        if (ex instanceof NestedServletException) {
            ex = ex.getCause();
            ex.printStackTrace(pw);
        }
        String     codeMessage = code + " - ";
        HttpStatus status      = HttpStatus.resolve(code);
        if (status == null) codeMessage += "Internal Server Error";
        else codeMessage += status.getReasonPhrase();
        if (code == 404) codeMessage += requestUri;
        return new ErrorInfo(codeMessage, message, sw.toString().replace("\r\n", "\n"));
    }
}

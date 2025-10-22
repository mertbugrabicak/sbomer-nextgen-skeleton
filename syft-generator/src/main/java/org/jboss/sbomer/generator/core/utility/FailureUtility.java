package org.jboss.sbomer.generator.core.utility;

import org.jboss.sbomer.events.kafka.common.FailureSpec;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FailureUtility {

    private FailureUtility() {}

    /**
     * Utility method to build a FailureSpec object from a Java Exception.
     *
     * @param e The exception that was caught.
     * @return A populated FailureSpec object.
     */
    public static FailureSpec buildFailureSpecFromException(Exception e) {
        // 1. Create a new FailureSpec instance.
        FailureSpec failure = new FailureSpec();

        // 2. Set the human-readable reason from the exception's message.
        failure.setReason(e.getMessage());

        // 3. Use the exception's class name as a simple, machine-readable error code.
        failure.setErrorCode(e.getClass().getSimpleName());

        // 4. Capture the full stack trace for detailed debugging context.
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // 5. Add the stack trace to the details map.
        Map<String, String> details = new HashMap<>();
        details.put("stackTrace", stackTrace);
        failure.setDetails(details);

        // 6. Return the fully constructed object.
        return failure;
    }


}

package edu.hm.cs.interpreter.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Represents an option for the user during the process.
 */
@Data
@AllArgsConstructor
@ToString
public class Option {

    private String value;
    private String variableName;
    private boolean memory;
    private boolean next;
    private boolean back;

}

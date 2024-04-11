package edu.hm.cs.interpreter.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a forwarding after a condition. It extends the {@link Transition}.
 */
@Data
@NoArgsConstructor
@ToString(callSuper=true)
public class Forwarding extends Transition {

    private String condition;
    private boolean defaultTransition;

}

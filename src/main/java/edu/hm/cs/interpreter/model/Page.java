package edu.hm.cs.interpreter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * Represents page in the process. It extends the {@link Navigationelement}.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
public class Page extends Navigationelement {

    private Map<String, String> variables;
    private String description;

}

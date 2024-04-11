package edu.hm.cs.interpreter.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a transition between navigation elements.
 */
@Data
@NoArgsConstructor
@ToString
public class Transition {

    private String name;
    private Navigationelement next;

}

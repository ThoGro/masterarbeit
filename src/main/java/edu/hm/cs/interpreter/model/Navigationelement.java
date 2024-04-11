package edu.hm.cs.interpreter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a navigation element.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Navigationelement {

    private String name;
    private List<Transition> transition = new ArrayList<>();

}

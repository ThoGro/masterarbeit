package edu.hm.cs.interpreter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a condition as a branching point in the process. It extends the {@link Navigationelement}.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
public class Condition extends Navigationelement{

}

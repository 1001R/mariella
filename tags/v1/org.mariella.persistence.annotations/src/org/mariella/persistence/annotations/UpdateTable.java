package org.mariella.persistence.annotations;

import javax.persistence.UniqueConstraint;

@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface UpdateTable {

  public abstract java.lang.String name() default "";
  
  public abstract java.lang.String catalog() default "";
  
  public abstract java.lang.String schema() default "";
  
  public abstract UniqueConstraint[] uniqueConstraints() default {};

}

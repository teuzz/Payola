package cz.payola.model.generic

import cz.payola.common.model.NamedEntity
import cz.payola.scala2json.annotations.JSONFieldName

trait ConcreteNamedEntity extends ConcreteEntity with NamedEntity
{
    @JSONFieldName(name = "name") protected var _name: String = null

    /** Returns the object's name.
      *
      * @return Object's name.
      */
    def name: String = _name

    /** Sets the object's name.
      *
      * @param n New name.
      *
      * @throws IllegalArgumentException if the new name is null or empty.
      */
    def name_=(n: String) = {
        // The name mustn't be null and mustn't be empty
        require(n != null && n != "")

        _name = n
    }

    /** Convenience method that just calls name_=.
      *
      * @param n The new object's name.
      *
      * @throws IllegalArgumentException if the new name is null or empty.
      */
    def setName(n: String) = name_=(n);
}

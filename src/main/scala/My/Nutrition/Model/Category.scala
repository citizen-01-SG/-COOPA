package My.Nutrition.Model

import scalafx.beans.property.{StringProperty, IntegerProperty}

class Category(val id: Int, val name: String):
  // We need these properties for the UI (Dropdown list) later
  val nameProp = new StringProperty(this, "name", name)
  val idProp   = new IntegerProperty(this, "id", id)

  // This formatting is what shows up in the Dropdown box
  override def toString: String = name

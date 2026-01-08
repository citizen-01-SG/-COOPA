package My.Nutrition.Model

import scalafx.beans.property.{StringProperty, IntegerProperty}

class Category(val id: Int, val name: String):
  // UI Properties
  val nameProp = new StringProperty(this, "name", name)
  val idProp   = new IntegerProperty(this, "id", id)

  // --- NEW HELPER ---
  // This fixes the "value categoryName is not a member" error
  def categoryName: String = name

  override def toString: String = name
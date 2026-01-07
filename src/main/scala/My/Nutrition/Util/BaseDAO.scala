package My.Nutrition.Util

// It is a TRAIT (Interface)
trait BaseDAO[T] {
  def selectAll(): List[T]
  def insert(item: T): Unit
  def update(item: T): Unit
  def delete(item: T): Unit
}

package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait UserRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val userRepository = new LazyTableRepository[User](schema.users, User)
        with UserRepository
        with NamedEntityTableRepository[User]
    {
        def getAllWithNameLike(name: String, pagination: Option[PaginationInfo] = None): Seq[User] = {
            selectWhere(_.name like "%" + name + "%", pagination)
        }

        def getByCredentials(name: String, password: String): Option[User] = {
            selectOneWhere(u => u.name === name and u.password === password)
        }
    }
}

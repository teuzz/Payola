package cz.payola.data.squeryl.entities

import cz.payola.data._
import org.squeryl.annotations.Transient
import scala.collection.immutable
import scala.Some
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.Group]] to [[cz.payola.data.squeryl.entities.Group]]
  */
object Group extends EntityConverter[Group]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[Group] = {
        entity match {
            case e: Group => Some(e)
            case e: cz.payola.common.entities.Group => Some(new Group(e.id, e.name, User(e.owner)))
            case _ => None
        }
    }
}

class Group(override val id: String, name: String, o: User)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Group(name, o)
    with PersistableEntity with PrivilegableEntity
{
    val ownerId: Option[String] = if (owner == null) None else Some(owner.id)

    private lazy val _ownerQuery = context.schema.groupOwnership.right(this)

    @Transient
    private var _groupMembersLoaded = false
    private lazy val _groupMembersQuery = context.schema.groupMembership.right(this)

    override def members: immutable.Seq[UserType] = {
        if (!_groupMembersLoaded) {
            evaluateCollection(_groupMembersQuery).map(u =>
                if (!super.members.contains(u)) {
                    super.storeMember(u)
                }
            )

            _groupMembersLoaded = true
        }

        super.members
    }

    override def owner: UserType = {
        if (_owner == null && ownerId != null){
            _owner = evaluateCollection(_ownerQuery)(0)
        }

        _owner
    }

    override def storeMember(u: UserType) {
        super.storeMember(associate(User(u), _groupMembersQuery))
    }

    override protected def discardMember(user: UserType) {
        super.discardMember(dissociate(User(user), _groupMembersQuery))
    }
}
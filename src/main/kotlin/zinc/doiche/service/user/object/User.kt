package zinc.doiche.service.user.`object`

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.*

@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column
    val uuid: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
)

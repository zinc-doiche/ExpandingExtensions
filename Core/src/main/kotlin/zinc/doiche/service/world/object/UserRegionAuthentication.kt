package zinc.doiche.service.world.`object`

import jakarta.persistence.*

@Entity
@Table(name = "TBL_USER_REGION_AUTHENTICATION")
class UserRegionAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null


}
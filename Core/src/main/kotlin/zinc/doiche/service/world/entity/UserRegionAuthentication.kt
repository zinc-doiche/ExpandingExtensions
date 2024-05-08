package zinc.doiche.service.world.entity

import jakarta.persistence.*
import zinc.doiche.service.user.entity.User

@Entity
@Table(name = "TBL_USER_REGION_AUTHENTICATION")
class UserRegionAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    val user: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXTENSION_WORLD_ID")
    val extensionWorld: ExtensionWorld? = null


}
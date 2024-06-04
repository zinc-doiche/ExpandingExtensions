package zinc.doiche.service.user.entity

import jakarta.persistence.*
import org.bukkit.Material
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_POST")
class Post(
    @Column(nullable = false)
    val displayedInfo: DisplayedInfo,

    @Enumerated(EnumType.STRING)
    val material: Material,

    @Column(nullable = false)
    val content: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}
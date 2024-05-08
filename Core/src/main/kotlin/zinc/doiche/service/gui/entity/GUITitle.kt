package zinc.doiche.service.gui.entity

import jakarta.persistence.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

@Entity
class GUITitle(
    @Column(nullable = false)
    val title: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUI_ID")
    val gui: GUI? = null

    fun title(): Component = Component.text(title).color(TextColor.fromHexString("#FEFEFE"))
}
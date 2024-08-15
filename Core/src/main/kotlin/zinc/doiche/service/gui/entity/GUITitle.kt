package zinc.doiche.service.gui.entity

import zinc.doiche.service.image.entity.Image

/**
 * Mapping Class between GUI and Image

 */
class GUITitle(
    val name: String,
    val image: Image,
    val gui: GUI
) {
    val id: Long? = null
}
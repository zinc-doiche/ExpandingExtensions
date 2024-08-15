package zinc.doiche.service.user.entity.post

import zinc.doiche.service.user.entity.User
import java.time.LocalDate

class ReceivedPost(
    val post: Post? = null,
    val receiver: User? = null,
    val expiredDate: LocalDate? = null
){
    val id: Long? = null

    final var isRead: Boolean = false
        private set

    final var isClaimed: Boolean = false
        private set
}
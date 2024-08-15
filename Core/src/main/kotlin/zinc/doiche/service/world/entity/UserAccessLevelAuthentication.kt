package zinc.doiche.service.world.entity

import zinc.doiche.lib.embeddable.Period

class UserAccessLevelAuthentication(
    val period: Period
) {
    val id: Long? = null

//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userAccessLevelAuthentication")
//    val user: User? = null

    val accessLevel: AccessLevel? = null
}
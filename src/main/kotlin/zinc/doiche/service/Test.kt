package zinc.doiche.service

import jakarta.persistence.*

@Entity
data class Test(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column
    val name: String = ""
)

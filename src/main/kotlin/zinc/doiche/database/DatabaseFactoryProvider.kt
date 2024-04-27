package zinc.doiche.database

import zinc.doiche.database.`object`.Config
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.searchInProject
import zinc.doiche.plugin
import zinc.doiche.util.toObject
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.jar.JarFile
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DatabaseFactoryProvider {
    private var entityManagerFactory: EntityManagerFactory? = null

    fun get() = entityManagerFactory ?: create()

    private fun create(): EntityManagerFactory {
        val config = plugin.config("database.json").toObject(Config::class.java)
        //replacePlaceholdersInPersistenceXml(config)
        return initEntityManagerFactory(config)
    }

    private fun replacePlaceholdersInPersistenceXml(config: Config) {
//        val persistenceXmlFile = File(plugin.dataFolder, "META-INF/persistence.xml")
        var persistenceXmlFile: File? = null

        Thread.currentThread().contextClassLoader.resources("META-INF/persistence.xml").forEach {
            if(it.path.contains("META-INF"))
                plugin.logger.info("path: ${it.path}")
        }

        searchInProject { jarFile ->
            jarFile.versionedStream().forEach { jarEntry ->
                val path = jarEntry.toString()
                if(path.endsWith("META-INF/persistence.xml")) {
                    persistenceXmlFile = File(plugin.dataFolder, path)
                    return@forEach
                }
            }
        }

        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val document = dBuilder.parse(persistenceXmlFile)
        val properties = document.getElementsByTagName("property")

        for (i in 0 until properties.length) {
            val property = properties.item(i)
            val name = property.attributes.getNamedItem("name").nodeValue
            when (name) {
                "javax.persistence.jdbc.url" -> property.attributes.getNamedItem("value").nodeValue = config.getURL()
                "javax.persistence.jdbc.user" -> property.attributes.getNamedItem("value").nodeValue = config.username
                "javax.persistence.jdbc.password" -> property.attributes.getNamedItem("value").nodeValue = config.password
            }
        }

        val unit = document.getElementsByTagName("persistence-unit").item(0)
        unit.attributes.getNamedItem("name").nodeValue = config.database

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        val source = DOMSource(document)
        val result = StreamResult(FileOutputStream(persistenceXmlFile))
        transformer.transform(source, result)
    }

    private fun initEntityManagerFactory(config: Config): EntityManagerFactory {
        val manager = Persistence.createEntityManagerFactory(config.database, mapOf(
            "db.url" to config.getURL(),
            "db.username" to config.username,
            "db.password" to config.password,
            "db.name" to config.database,
            "db.show_sql" to config.showSQL,
            "db.ddl" to config.ddl,
        ))
        entityManagerFactory = manager
        return manager
    }
}
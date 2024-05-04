package zinc.doiche.database

import jakarta.persistence.AttributeConverter

class StringArrayConverter: AttributeConverter<Array<String>, String>{
    override fun convertToDatabaseColumn(attribute: Array<String>): String {
        TODO("Not yet implemented")
    }

    override fun convertToEntityAttribute(dbData: String): Array<String> {
        TODO("Not yet implemented")
    }
}
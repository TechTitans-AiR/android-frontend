package hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement

class Catalog (
    val id: String?,
    val name: String,
    val articles: String,
    val services: String,
    val users: String,
    val date_created: String?,
    val date_modified: String?,
    var disabled: Boolean
)

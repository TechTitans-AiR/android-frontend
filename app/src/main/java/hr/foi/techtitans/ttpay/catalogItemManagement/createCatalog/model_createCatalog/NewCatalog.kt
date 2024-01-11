package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

data class NewCatalog(
    val name: String,
    var articles:List<String>,
    var services:List<String>,
    var users:List<String>?,
    var disabled: Boolean
)

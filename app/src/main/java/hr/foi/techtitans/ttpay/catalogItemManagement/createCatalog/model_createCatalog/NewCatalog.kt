package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

data class NewCatalog(
    val name: String,
    val articles:List<String>,
    val services:List<String>,
    val users:List<String>?,
    var disabled: Boolean
)

package ru.arturprgr.mybrowser

class Preferences(context: android.content.Context) {
    private val sPrefs = context.getSharedPreferences("sPrefs", android.content.Context.MODE_PRIVATE)

    private fun set(key: String, value: Any) {
        sPrefs.edit().putString(key, "$value").apply()
    }

    private fun get(key: String): String? {
        return sPrefs.getString(key, "")
    }

    fun getQuantityBookmarks(): Int {
        return get("quantityBookmarks")!!.toInt()
    }

    fun getQuantityDownloads(): Int {
        return get("quantityDownloads")!!.toInt()
    }

    fun getQuantityHistory(): Int {
        return get("quantityHistory")!!.toInt()
    }

    fun getAccount(): String {
        return get("account")!!
    }

    fun getEmail(): String {
        return get("email")!!
    }

    fun getName(): String {
        return get("name")!!
    }

    fun setQuantityBookmarks(value: Int) {
        set("quantityBookmarks", value)
    }

    fun setQuantityDownloads(value: Int) {
        set("quantityDownloads", value)
    }

    fun setQuantityHistory(value: Int) {
        set("quantityHistory", value)
    }

    fun setAccount(value: String) {
        set("account", value)
    }

    fun setEmail(value: String) {
        set("email", value)
    }

    fun setName(value: String) {
        set("name", value)
    }
}
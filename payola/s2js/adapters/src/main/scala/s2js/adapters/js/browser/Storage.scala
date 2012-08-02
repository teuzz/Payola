package s2js.adapters.js.browser

abstract class Storage
{
    val length: Long

    def key(index: Long): String

    def getItem(key: String): String

    def setItem(key: String, value: String)

    def removeItem(key: String)

    def clear()
}

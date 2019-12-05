@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.InputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 8. <p/>
 */

abstract class BaseXPath constructor() {
    companion object {
        private val log = LoggerFactory.getLogger(BaseXPath::class.java)
    }

    lateinit var document: Document

    private val xpath     = XPathFactory.newInstance().newXPath()
    protected val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    /**
     * xml 파일 핸을 연다
     */
    fun loadXml(fp: File) {
        if (!fp.exists()) {
            log.error("ERROR: FILE NOT FOUND ($fp)")
            return
        }

        try {
            document = builder.parse(fp)
            parsing()
        } catch (e: Exception) {
            log.error("ERROR: ${e.message}")
        }
    }

    /**
     * xml input stream 을 연다
     */
    fun loadXml(ism: InputStream) {
        try {
            document = builder.parse(ism)
            parsing()
        } catch (e: Exception) {
            log.error("ERROR: ${e.message}")
        }
    }

    /**
     * xml string 로드 한다
     */
    fun loadXml(xml: String) {
        try {
            document = builder.parse(InputSource(StringReader(xml)))
            parsing()
        } catch (e: Exception) {
            log.error("ERROR: ${e.message}")
        }
    }

    /** string 으로 반환 */
    fun string(expr: String) = xpath.evaluate(expr, document, XPathConstants.STRING).toString().trim()
    /** int 로 반환 */
    fun int(expr: String)    = string(expr).toInt()
    /** float 으로 반환 */
    fun float(expr: String)  = string(expr).toFloat()
    /** double 로 반환 */
    fun double(expr: String) = string(expr).toDouble()
    /** bool 로 반환 */
    fun bool(expr: String)   = string(expr).toBoolean()

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun parsing()
}

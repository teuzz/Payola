package cz.payola.web.client.views.elements

import s2js.adapters.js.browser.document
import s2js.adapters.js.html
import cz.payola.web.client.View
import s2js.adapters.js.html.Element

class Text(initialValue: String) extends View
{
    private val textNode = document.createTextNode(initialValue)

    def text: String = textNode.textContent

    def text_=(value: String) {
        textNode.textContent = value
    }

    def render(parent: html.Element) {
        parent.appendChild(textNode)
    }

    def destroy() {
        textNode.parentNode.removeChild(textNode)
    }

    def blockHtmlElement: Element = null
}

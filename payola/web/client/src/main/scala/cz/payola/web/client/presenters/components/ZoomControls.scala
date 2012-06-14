package cz.payola.web.client.presenters.components

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.events._
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Icon

class ZoomControls(var currentZoom: Double) extends Component
{
    /**
      * How much zoom (movement) causes one rotation of mouse wheel.
      */
    val zoomStep = 0.09

    private val maximumZoomOut = 25
    private val maximumZoomIn = 200

    val zoomIncreased = new ZoomChangedEvent[ZoomControls]
    val zoomDecreased = new ZoomChangedEvent[ZoomControls]

    private val spanCaption = new Text(getStatusCaption)

    val plus = new Span(List(new Icon(Icon.zoom_in)),"btn")
    val status = new Span(List(spanCaption))
    val minus = new Span(List(new Icon(Icon.zoom_out)),"btn")

    val wrapper = new Div(List(plus, status, minus), "zoom-controls")

    def canZoomIn: Boolean = {
        currentZoom < maximumZoomIn
    }

    def canZoomOut: Boolean = {
        currentZoom > maximumZoomOut
    }

    private def getStatusCaption = {
        val wholeNum = math.round(currentZoom)

        if(10 < wholeNum && wholeNum < 100) {
            " " + wholeNum + " %"
        } else {
            wholeNum + " %"
        }
    }

    def setZoom(zoom: Double) {
        currentZoom = zoom
        spanCaption.setText(getStatusCaption)
    }

    def increaseZoomInfo() {
        setZoom(currentZoom + (zoomStep*50))
    }

    def decreaseZoomInfo() {
        setZoom(currentZoom - (zoomStep*50))
    }

    def render(parent: Element = document.body) {
        wrapper.render(parent)
    }

    plus.clicked += { evt =>
        zoomIncreased.trigger(new ZoomChangedEventArgs[ZoomControls](this))
        false
    }

    minus.clicked += { evt =>
        zoomDecreased.trigger(new ZoomChangedEventArgs[ZoomControls](this))
        false
    }
}

package cz.payola.web.client.presenters.entity.plugins

import scala.collection.mutable
import s2js.adapters.html
import cz.payola.web.shared.managers._
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.Presenter
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.entity.plugins._
import cz.payola.common.ValidationException
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.web.client.events._
import cz.payola.common.rdf.IdentifiedVertex
import s2js.adapters.browser.window
import s2js.compiler.javascript

class DataSourceBrowser(
    val viewElement: html.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
    extends Presenter
{
    private val view = new DataSourceBrowserView(dataSourceName)

    private val graphPresenter = new GraphPresenter(view.graphViewSpace.htmlElement)

    private var history = mutable.ListBuffer.empty[String]

    private var historyPosition = -1

    @javascript("""return encodeURIComponent(uri)""")
    def encodeURIComponent(uri: String) : String = ""

    @javascript("""return decodeURIComponent(uri)""")
    def decodeURIComponent(uri: String) : String = ""

    def initialize() {
        // Initialize the sub presenters.
        graphPresenter.initialize()

        // Register the event handlers.
        view.backButton.mouseClicked += onBackButtonClicked _
        view.nextButton.mouseClicked += onNextButtonClicked _
        view.goButton.mouseClicked += onGoButtonClicked _
        view.sparqlQueryButton.mouseClicked += onSparqlQueryButtonClicked _
        view.nodeUriInput.keyPressed += onNodeUriKeyPressed _
        graphPresenter.view.vertexBrowsing += onVertexBrowsing _

        view.render(viewElement)

        if (window.location.hash.size == 0) {
            // If the default URI isn't specified, display the initial graph.
            if (initialVertexUri == "") {
                blockPage("Fetching the initial graph...")
                DataSourceManager.getInitialGraph(dataSourceId) { graph =>
                    graphPresenter.view.updateGraph(graph, true)
                    updateNavigationView()
                    unblockPage()
                }(fatalErrorHandler(_))
            } else {
                addToHistoryAndGo(initialVertexUri, false)
            }
        } else {
            addToHistoryAndGo(decodeURIComponent(window.location.hash.substring(1)), false)
        }
    }

    private def onVertexBrowsing(e: VertexEventArgs[_]) {
        e.vertex match {
            case i: IdentifiedVertex => addToHistoryAndGo(i.uri, false)
        }
    }

    private def onBackButtonClicked(e: EventArgs[_]): Boolean = {
        if (canGoBack) {
            historyPosition -= 1
            updateView(true)
        }
        false
    }

    private def onNextButtonClicked(e: EventArgs[_]): Boolean = {
        if (canGoNext) {
            historyPosition += 1
            updateView(true)
        }
        false
    }

    private def onGoButtonClicked(e: EventArgs[_]): Boolean = {
        addToHistoryAndGo(view.nodeUriInput.value, true)
        false
    }

    private def onSparqlQueryButtonClicked(e: EventArgs[_]): Boolean = {
        val modal = new SparqlQueryModal
        modal.confirming += { _ =>
            modal.block("Executing the SPARQL query.")
            DataSourceManager.executeSparqlQuery(dataSourceId, modal.sparqlQueryInput.value) { g =>
                modal.unblock()
                modal.destroy()

                history = mutable.ListBuffer.empty[String]
                historyPosition = -1
                updateNavigationView()

                graphPresenter.view.clear()
                graphPresenter.view.updateGraph(g, true)
            } { error =>
                modal.unblock()
                error match {
                    case v: ValidationException => AlertModal.display("Error", v.message)
                    case t => fatalErrorHandler(t)
                }
            }
            false
        }
        modal.render()
        false
    }

    private def onNodeUriKeyPressed(e: KeyboardEventArgs[_]): Boolean = {
        if (e.keyCode == 13) {
            onGoButtonClicked(e)
        } else {
            true
        }
    }

    private def addToHistoryAndGo(uri: String, clearGraph: Boolean) {
        // Remove all next items from the history.
        while (historyPosition < history.length - 1) {
            history.remove(historyPosition + 1)
        }

        // Add the new item.
        history += uri
        historyPosition += 1

        window.location.hash = encodeURIComponent(uri)

        updateView(clearGraph)
    }

    private def updateView(clearGraph: Boolean) {
        val uri = history(historyPosition)
        view.nodeUriInput.value = uri
        view.nodeUriInput.setIsEnabled(false)

        blockPage("Fetching the node neighbourhood...")
        DataSourceManager.getNeighbourhood(dataSourceId, uri) { graph =>
            if (clearGraph) {
                graphPresenter.view.clear()
            }
            graphPresenter.view.updateGraph(graph, true)
            updateNavigationView()

            view.nodeUriInput.setIsEnabled(true)
            unblockPage()
        }(fatalErrorHandler(_))
    }

    private def updateNavigationView() {
        view.backButton.setIsEnabled(canGoBack)
        view.nextButton.setIsEnabled(canGoNext)
        if (historyPosition < 0) {
            view.nodeUriInput.value = ""
        }
    }

    private def canGoBack = history.nonEmpty && historyPosition > 0

    private def canGoNext = history.nonEmpty && historyPosition < (history.length - 1)
}

package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.ComposedView
import cz.payola.common.entities.Analysis
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.elements.form.fields._
import s2js.adapters.browser.`package`._
import scala.Some

class AnalysisEditorView(analysis: Analysis, newName: Option[String], newDesc: Option[String], pageTitle: String) extends ComposedView
{
    val name = new InputControl("Analysis name:", new TextInput("name", if(newName.isDefined){newName.get}else{analysis.name}, "Analysis name"), Some("nofloat"))

    val description = new InputControl("Description:", new TextArea("description",  if(newDesc.isDefined){newDesc.get}else{analysis.description}, "Anaylsis description"), Some("nofloat"))

    protected val properties = new Div(List(name, description))

    val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))

    protected val addPluginLinkLi = new ListItem(List(addPluginLink))

    val addDataCubeLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add DataCube plugin")))

    protected val addDataCubeLinkLi = new ListItem(List(addDataCubeLink))

    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add data source")))

    protected val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))

    protected val mergeBranchesLi = new ListItem(List(mergeBranches))

    protected val menu = new UnorderedList(List(addPluginLinkLi, addDataSourceLinkLi, mergeBranchesLi))

    val visualizer = new EditableAnalysisVisualizer(analysis)

    protected val leftColContent = new Div(List(menu, properties), "well")

    val analysisCanvas = new Div(List(visualizer), "plugin-space")

    protected val leftCol = new Div(List(leftColContent), "span3")

    protected val rightCol = new Div(List(analysisCanvas), "span9 relative")

    val h1 = new Heading(List(new Text(pageTitle)),1,"span10")
    val runButton = new Button(new Text("Run"), "span1", new Icon(Icon.play))

    val mainHeader = new Div(List(h1, runButton),"main-header row-fluid")
    val row = new Div(List(leftCol, rightCol))
    protected val container = new Div(List(mainHeader, row))

    name.field.addCssClass("span12")
    description.field.addCssClass("span12")

    def setName(newValue: String) {
        name.field.value = newValue
    }

    def createSubViews = List(container)
}
